/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence.model;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents the details of an ECodex message. It stores information such as the sender
 * address, recipient address, national message ID, ebms message ID, hash value, and hash
 * algorithm.
 */
@Getter
@Setter
public class ConnectorEvidenceMessageDetails {
    private String senderAddress;
    private String recipientAddress;
    private String nationalMessageId;
    private String ebmsMessageId;
    private byte[] hashValue;
    private String hashAlgorithm;
}
