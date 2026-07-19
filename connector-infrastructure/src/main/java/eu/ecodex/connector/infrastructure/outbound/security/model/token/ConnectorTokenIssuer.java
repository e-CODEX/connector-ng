/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.security.model.token;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

/**
 * Represents the entity that issues a connector token, including details about the service
 * provider, country, and the type of advanced electronic system used.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "ConnectorTokenIssuerType",
    propOrder = {"serviceProvider", "country", "advancedElectronicSystem"}
)
public class ConnectorTokenIssuer implements Serializable {
    @XmlElement(name = "ServiceProvider", required = true)
    private String serviceProvider;
    @XmlElement(name = "Country", required = true)
    private String country;
    @XmlElement(name = "AdvancedElectronicSystem", required = true)
    private ConnectorTokenAESType advancedElectronicSystem;

    /**
     * Validates the state of this ConnectorTokenIssuer instance based on the following criteria: -
     * The service provider value is not null or empty. - The country value is not null or empty and
     * matches one of the ISO country codes. - The advanced electronic system is not null.
     *
     * @return true if all the validation criteria are met; false otherwise.
     */
    public boolean isValid() {
        var countries = Arrays.stream(Locale.getISOCountries()).toList();

        return StringUtils.hasText(serviceProvider)
            && StringUtils.hasText(country) && countries.contains(country.toUpperCase())
            && advancedElectronicSystem != null;
    }

    @Override
    public String toString() {
        return String.format(
            "{serviceProvider=%s, country=%s, advancedElectronicSystem=%s}",
            serviceProvider, country, advancedElectronicSystem
        );
    }
}
