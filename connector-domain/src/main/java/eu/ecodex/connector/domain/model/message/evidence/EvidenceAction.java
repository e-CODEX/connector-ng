/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message.evidence;

import eu.ecodex.connector.domain.model.as4.ConnectorAS4Action;
import eu.ecodex.connector.domain.model.as4.ConnectorAS4Service;
import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import lombok.Getter;

/**
 * Represents an enumeration of evidence actions used in the connector domain. Each action is linked
 * to a specific {@code ConnectorAS4Action} instance and, optionally, to a
 * {@code ConnectorAS4Service}. These actions define the various processes or operations associated
 * with the AS4 communication protocol, such as message delivery, submission, relay acceptance, or
 * rejections.
 *
 * <p>The {@code EvidenceAction} enum provides a mechanism to map these actions to their
 * corresponding
 * {@code ConnectorAction} representations.
 *
 * <ul>
 *     <li>RELAY REMMD acceptance or rejection</li>
 *     <li>Message delivery or non-delivery</li>
 *     <li>Message retrieval or non-retrieval</li>
 *     <li>Submission acceptance or rejection</li>
 * </ul>
 *
 * <p>Each action encapsulates:
 * <ul>
 *     <li>
 *         {@code ConnectorAS4Action action}: Represents the specific AS4 action linked to the
 *         entity.
 *     </li>
 *     <li>
 *         {@code ConnectorAS4Service service}: Optionally categorizes the evidence action within
 *         a service.
 *     </li>
 * </ul>
 *
 * <p>Key methods:
 * <ul>
 *     <li>{@link #toConnectionAction()} - Converts the {@code EvidenceAction} instance into a
 *     {@code ConnectorAction}, using the name of the associated {@code ConnectorAS4Action}.</li>
 * </ul>
 */
@Getter
public enum EvidenceAction {
    relayREEMDAcceptance(new ConnectorAS4Action("RelayREMMDAcceptanceRejection"), null),
    relayREEMDRejection(new ConnectorAS4Action("RelayREMMDAcceptanceRejection"), null),
    relayREMMDFailure(new ConnectorAS4Action("RelayREMMDFailure"), null),
    delivery(new ConnectorAS4Action("DeliveryNonDeliveryToRecipient"), null),
    nonDelivery(new ConnectorAS4Action("DeliveryNonDeliveryToRecipient"), null),
    nonRetrieval(new ConnectorAS4Action("DeliveryNonDeliveryToRecipient"), null),
    retrieval(new ConnectorAS4Action("RetrievalNonRetrievalToRecipient"), null),
    submissionAcceptance(new ConnectorAS4Action("SubmissionAcceptanceRejection"), null),
    submissionRejection(new ConnectorAS4Action("SubmissionAcceptanceRejection"), null);

    private final ConnectorAS4Action action;
    private final ConnectorAS4Service service;

    EvidenceAction(ConnectorAS4Action action, ConnectorAS4Service service) {
        this.action = action;
        this.service = service;
    }

    /**
     * Converts the current {@code EvidenceAction} instance to a {@code ConnectorAction} object.
     *
     * @return a {@code ConnectorAction} object built using the name of the associated
     *         {@code ConnectorAS4Action}.
     */
    public ConnectorAction toConnectionAction() {
        return ConnectorAction.builder()
                              .name(this.action.name())
                              .build();
    }
}
