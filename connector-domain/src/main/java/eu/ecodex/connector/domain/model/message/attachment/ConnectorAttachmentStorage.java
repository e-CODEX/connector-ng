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
 * Enum representing different types of storage for connector attachments. Currently, only Amazon S3
 * Bucket is supported.
 */
public enum ConnectorAttachmentStorage {
    /**
     * Represents an attachment stored in an Amazon S3 bucket.
     */
    S3_BUCKET
}
