/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import eu.ecodex.connector.BusinessDomainTestFixtures;
import eu.ecodex.connector.JsonTestFixtures;
import eu.ecodex.connector.application.exception.ConnectorBusinessDomainAlreadyExistsException;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorListBusinessDomain;
import eu.ecodex.connector.application.port.api.businessdomain.ConnectorRegisterBusinessDomain;
import eu.ecodex.connector.domain.model.link.ConnectorConfigurationSource;
import eu.ecodex.connector.infrastructure.inbound.web.rest.advice.ErrorResponse;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.AbstractWebMvcTest;
import eu.ecodex.connector.infrastructure.inbound.web.rest.controller.admin.businessdomain.ConnectorBusinessDomainAdminController;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorBusinessDomainDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.client.RestTestClient;

@SuppressWarnings("checkstyle:MissingJavadocType")
@DisplayName("ConnectorBusinessDomainAdminController")
@WebMvcTest(ConnectorBusinessDomainAdminController.class)
public class ConnectorBusinessDomainAdminControllerTest extends AbstractWebMvcTest {
    private static final String URL = "/api/v1/admin/business-domains";

    @Autowired
    private RestTestClient apiClient;
    @MockitoBean
    private ConnectorRegisterBusinessDomain registerBusinessDomain;
    @MockitoBean
    private ConnectorListBusinessDomain listBusinessDomain;

    @Nested
    @DisplayName("POST (create a business domain)")
    class CreateBusinessDomain {
        @Test
        void should_return_201_when_the_business_domain_is_created() {
            when(registerBusinessDomain.execute(any()))
                .thenReturn(BusinessDomainTestFixtures.createDefaultBusinessDomain());

            var body = JsonTestFixtures.readJson("json/business-domain.creation.json");
            var response = apiClient.post()
                                    .uri(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(body)
                                    .exchange()
                                    .expectStatus().isCreated()
                                    .returnResult(ConnectorBusinessDomainDto.class);

            var responseBody = response.getResponseBody();

            assertThat(responseBody).isNotNull();
            assert responseBody != null;

            assertThat(responseBody.enabled()).isTrue();
            assertThat(responseBody.identifier()).isEqualTo("default_business_domain");
            assertThat(responseBody.source())
                .isEqualTo(ConnectorConfigurationSource.IMPLEMENTATION);
        }

        @Test
        void should_return_409_when_the_identifier_already_exists() {
            doThrow(ConnectorBusinessDomainAlreadyExistsException.class)
                .when(registerBusinessDomain).execute(any());

            var body = JsonTestFixtures.readJson("json/business-domain.creation.json");
            var response = apiClient.post()
                                    .uri(URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(body)
                                    .exchange()
                                    .expectStatus().is4xxClientError()
                                    .returnResult(ErrorResponse.class);

            var responseBody = response.getResponseBody();

            assertThat(responseBody).isNotNull();
            assert responseBody != null;
            assertThat(responseBody.status()).isEqualTo(HttpStatus.CONFLICT.value());
        }

        @Test
        void should_return_400_when_the_request_body_is_invalid() {
            apiClient.post()
                     .uri(URL)
                     .contentType(MediaType.APPLICATION_JSON)
                     .body("{}")
                     .exchange()
                     .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("GET (list the business domains)")
    class ListBusinessDomains {
        @Test
        void should_return_200_when_listing_the_business_domains() {
            apiClient.get()
                     .uri(URL)
                     .exchange()
                     .expectStatus().isOk();
        }
    }
}
