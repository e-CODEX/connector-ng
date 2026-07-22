/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.rest.controller.linkpartner;

import eu.ecodex.connector.domain.model.link.ConnectorLinkType;
import eu.ecodex.connector.infrastructure.inbound.web.rest.dto.ConnectorLinkPartnerDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Defines the REST API for managing link partner within the connector system.
 */
@RequestMapping("/api/v1/link-partners")
@Tag(name = "LinkPartner", description = "Api for managing link partner")
public interface ConnectorLinkPartnerApi {
    @GetMapping("")
    List<ConnectorLinkPartnerDto> listLinkPartners(
        @RequestParam(name = "linkType", required = false) ConnectorLinkType linkType);
}
