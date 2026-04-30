/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message.attachment;

/**
 * Enum representing the possible types of attachments in the Connector.
 *
 * <ul>
 *     <li>ATTACHMENT: Refers to general-purpose attachments that may be included in a message.
 *     <li>BUSINESS_DOCUMENT: Represents attachments that are classified as business documents,
 *     often containing important information relevant to the message context.
 * </ul>
 */
public enum ConnectorAttachmentType {
    ATTACHMENT, BUSINESS_DOCUMENT, ASICS, XML_TOKEN
}
