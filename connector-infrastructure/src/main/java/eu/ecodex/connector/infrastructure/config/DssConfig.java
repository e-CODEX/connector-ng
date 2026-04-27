/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.config;

import eu.ecodex.connector.infrastructure.property.dss.ConnectorDssProperties;
import eu.ecodex.connector.infrastructure.property.dss.DssProxyProperties;
import eu.europa.esig.dss.service.NonceSource;
import eu.europa.esig.dss.service.SecureRandomNonceSource;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.http.commons.CommonsDataLoader;
import eu.europa.esig.dss.service.http.commons.OCSPDataLoader;
import eu.europa.esig.dss.service.http.proxy.ProxyConfig;
import eu.europa.esig.dss.service.http.proxy.ProxyProperties;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * Configuration class for setting up DSS-related beans and properties.
 */
@Slf4j
@Configuration
@SuppressWarnings("checkstyle:MissingJavadocMethod")
public class DssConfig {
    @Bean
    public ProxyConfig proxyConfig(ConnectorDssProperties properties) {
        var proxyConfig = new ProxyConfig();
        var proxyProperties = properties.getProxy();

        resolveProxyProperties(
                "https",
                proxyProperties,
                proxyProperties != null ? proxyProperties.getHttps() : null,
                "https.proxyHost",
                "https.proxyPort",
                "HTTPS_PROXY"
        )
                .ifPresent(proxyConfig::setHttpsProperties);

        resolveProxyProperties(
                "http",
                proxyProperties,
                proxyProperties != null ? proxyProperties.getHttp() : null,
                "http.proxyHost",
                "http.proxyPort",
                "HTTP_PROXY"
        )
                .ifPresent(proxyConfig::setHttpProperties);

        return proxyConfig;
    }

    @Bean
    public DataLoader dssDataLoader(ProxyConfig proxyConfig) {
        var commonsDataLoader = new CommonsDataLoader();
        commonsDataLoader.setProxyConfig(proxyConfig);

        return commonsDataLoader;
    }

    @Bean
    public NonceSource defaultNonceSource() {
        return new SecureRandomNonceSource();
    }

    @Bean
    public OnlineOCSPSource defaultOcspOnlineSource(
            ProxyConfig proxyConfig,
            NonceSource nonceSource) {
        var ocspDataLoader = new OCSPDataLoader();
        ocspDataLoader.setProxyConfig(proxyConfig);

        var onlineOCSPSource = new OnlineOCSPSource();
        onlineOCSPSource.setDataLoader(ocspDataLoader);
        onlineOCSPSource.setNonceSource(nonceSource);

        return onlineOCSPSource;
    }

    @Bean
    public OnlineCRLSource defaultOnlineCrlSource(DataLoader dataLoader) {
        var onlineCRLSource = new OnlineCRLSource();
        onlineCRLSource.setDataLoader(dataLoader);

        return onlineCRLSource;
    }

    /**
     * Resolves proxy properties for a given scheme using a three-tier fallback: 1. Explicit config
     * property 2. JVM system properties (e.g. https.proxyHost / https.proxyPort) 3. Environment
     * variable (e.g. HTTPS_PROXY)
     */
    private Optional<ProxyProperties> resolveProxyProperties(
            String scheme,
            DssProxyProperties proxyProperties,
            ProxyProperties explicitConfig,
            String sysPropHost,
            String sysPropPort,
            String envVar) {

        if (proxyProperties != null) {
            if (explicitConfig != null) {
                log.info("setting DSS {} proxy configuration from application config", scheme);
                return Optional.of(explicitConfig);
            }
            return Optional.empty();
        }

        if (StringUtils.hasText(System.getProperty(sysPropHost))) {
            log.info("setting DSS {} proxy configuration from system properties", scheme);
            var props = new ProxyProperties();
            props.setHost(System.getProperty(sysPropHost));
            parsePort(System.getProperty(sysPropPort), scheme).ifPresent(props::setPort);
            return Optional.of(props);
        }

        if (StringUtils.hasText(System.getenv(envVar))) {
            var props = parseEnvProxy(System.getenv(envVar), scheme);
            if (props.isPresent()) {
                log.info(
                        "Setting DSS {} proxy configuration from environment variable [{}]",
                        scheme, envVar
                );
            }
            return props;
        }

        log.info("No DSS {} proxy configuration found", scheme);
        return Optional.empty();
    }

    private Optional<ProxyProperties> parseEnvProxy(String value, String scheme) {
        // Use find() not matches() — matches() requires the entire string to match,
        // which fails for URLs with trailing slashes or paths
        var matcher = Pattern.compile("(?:https?://)([\\w.]+):(\\d+)").matcher(value);
        if (!matcher.find()) {
            log.warn("could not parse {} proxy from environment value: {}", scheme, value);
            return Optional.empty();
        }
        var props = new ProxyProperties();
        props.setHost(matcher.group(1));
        parsePort(matcher.group(2), scheme).ifPresent(props::setPort);
        return Optional.of(props);
    }

    private Optional<Integer> parsePort(String value, String scheme) {
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            log.warn("invalid {} proxy port value [{}], port will not be set", scheme, value);
            return Optional.empty();
        }
    }
}
