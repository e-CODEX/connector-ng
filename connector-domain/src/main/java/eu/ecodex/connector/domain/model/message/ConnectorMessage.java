/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message;

import eu.ecodex.connector.domain.model.businessdomain.ConnectorBusinessDomainIdentifier;
import eu.ecodex.connector.domain.model.message.attachment.ConnectorMessageAttachment;
import eu.ecodex.connector.domain.model.message.content.ConnectorMessageBusinessContent;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import lombok.Builder;


/**
 * Represents the main structure of a connector message within the system.
 *
 * <p>This record encapsulates the complete state of a message flowing within the system, which
 * includes its identifiers, associated metadata, direction, timestamps, and businessContent. It is
 * used for tracking, auditing, processing, and transforming messages as they transition between the
 * backend and the gateway.
 *
 * @param businessDomainIdentifier            The uuid for the business domain where the message is
 *                                            being used.
 * @param uuid                                A globally unique uuid for the message. Maybe null.
 * @param identifier                          A mandatory unique uuid for the message within the
 *                                            system scope.
 * @param backendMessageIdentifier            A mandatory unique uuid for the message from the
 *                                            backend perspective.
 * @param referenceToBackendMessageIdentifier An optional reference to a related backend message
 *                                            uuid.
 * @param backendName                         The name of the backend system. Maybe null.
 * @param gatewayName                         The name of the gateway associated with this message.
 *                                            Maybe null.
 * @param as4Properties                       The AS4-specific metadata and configuration associated
 *                                            with the message.
 * @param direction                           The direction of message flow between the backend
 *                                            system and the gateway.
 * @param createdAt                           The timestamp indicating when the message was first
 *                                            created. Maybe null.
 * @param updatedAt                           The timestamp indicating the most recent update to the
 *                                            message. Maybe null.
 * @param deletedAt                           The timestamp indicating when the message was
 *                                            logically deleted. Maybe null.
 * @param rejectedAt                          The timestamp indicating when the message was rejected
 *                                            during processing. Maybe null.
 * @param confirmedAt                         The timestamp indicating when the backend or gateway
 *                                            confirmed the message. Maybe null.
 * @param deliveredToGatewayAt                The timestamp indicating when the message was
 *                                            successfully delivered to the gateway. Maybe null.
 * @param deliveredToBackendAt                The timestamp indicating when the message was
 *                                            successfully delivered to the backend. Maybe null.
 * @param businessContent                     The core businessContent of the message, encapsulated
 *                                            as XML data and potentially a printable
 *                                            businessDocument. This field determines if the message
 *                                            is considered a business message.
 * @param attachments                         A list of attachment files associated with the
 *                                            message. Maybe null.
 * @param errors                              A list of errors, if any, encountered during message
 *                                            processing. Maybe null.
 * @param evidences                           A list of evidences associated with the message,
 *                                            aiding in auditing and validation. Maybe null.
 * @param transportedEvidences                A subset of evidences that were transported alongside
 *                                            the message, potentially during AS4 transmission.
 *                                            Maybe null.
 */
@Builder(toBuilder = true)
public record ConnectorMessage(
        // TODO check if caused by should be set to connector message definition
        @Nonnull ConnectorBusinessDomainIdentifier businessDomainIdentifier,
        @Nullable String uuid,
        @Nullable String identifier,
        @Nullable String backendMessageIdentifier,
        @Nullable String referenceToBackendMessageIdentifier,
        @Nullable String backendName,
        @Nullable String gatewayName,
        @Nonnull ConnectorMessageAS4Properties as4Properties,
        @Nullable ConnectorMessageDirection direction,
        @Nullable Instant createdAt,
        @Nullable Instant updatedAt,
        @Nullable Instant deletedAt,
        @Nullable Instant rejectedAt,
        @Nullable Instant confirmedAt,
        @Nullable Instant deliveredToGatewayAt,
        @Nullable Instant deliveredToBackendAt,
        @Nullable ConnectorMessageBusinessContent businessContent,
        @Nullable List<ConnectorMessageAttachment> attachments,
        @Nullable List<ConnectorMessageError> errors,
        @Nullable List<ConnectorMessageEvidence> evidences,
        @Nullable List<ConnectorMessageEvidence> transportedEvidences
) implements Serializable {
    /**
     * Determines whether the current message has been rejected.
     *
     * <p>A message is considered rejected if the `rejectedAt` timestamp
     * is not null, indicating that rejection has been recorded for the message.
     *
     * @return {@code true} if the message is rejected; {@code false} otherwise.
     */
    public boolean isRejected() {
        return this.rejectedAt() != null;
    }

    /**
     * Determines whether the current message qualifies as an evidence message.
     *
     * <p>A message is categorized as an evidence message if it has no associated
     * business content and contains at least one evidence. This is established by checking if the
     * {@code businessContent} is {@code null} and the {@code evidences} list is non-null and not
     * empty.
     *
     * @return {@code true} if the message is an evidence message; {@code false} otherwise.
     */
    public boolean isEvidenceMessage() {
        return this.businessContent() == null
                && this.transportedEvidences() != null
                && !this.transportedEvidences().isEmpty();
    }

    /**
     * Determines whether the current message qualifies as a business message.
     *
     * <p>A message is classified as a business message if it is not an evidence message.
     * This method relies on the {@code isEvidenceMessage()} method to determine whether the current
     * message falls into the evidence category.
     *
     * @return {@code true} if the message is a business message, {@code false} otherwise.
     */
    public boolean isBusinessMessage() {
        return !this.isEvidenceMessage();
    }

    /**
     * Determines whether the current message is classified as an evidence trigger message.
     *
     * <p>A message is defined as an evidence trigger message if:
     * <ul>
     *     <li>It has no associated business content (i.e., `businessContent` is null).
     *     <li>It contains exactly one transported evidence.
     *     <li>The sole transported evidence has no attachment.
     * </ul>
     *
     * @return {@code true} if the message is an evidence trigger message; {@code false} otherwise.
     */
    public boolean isEvidenceTriggerMessage() {
        if (this.businessContent() != null) {
            return false;
        }

        var transported = this.transportedEvidences();

        return transported != null
                && transported.size() == 1
                && transported.getFirst().content() == null;
    }

    /**
     * Determines whether evidence triggering is allowed for the current message.
     *
     * <p>A message qualifies for evidence triggering if its direction is set to
     * {@code ConnectorMessageDirection.BACKEND_TO_GATEWAY}, indicating that the message originates
     * from the backend system and is directed toward the gateway.
     *
     * @return {@code true} if evidence triggering is allowed for the message; {@code false}
     *         otherwise.
     */
    public boolean isEvidenceTriggeringAllowed() {
        return this.direction() == ConnectorMessageDirection.BACKEND_TO_GATEWAY;
    }

    /**
     * Switches the direction of the current message by altering its AS4 properties and swapping the
     * roles and parties involved. The method reassigns the sender and receiver roles, switches the
     * original sender and final recipient, and updates the direction of the message.
     *
     * <p>The resulting message maintains its general structure and compliance with the expected
     * properties, but with reversed sender-to-receiver directions.
     *
     * @return A new {@code ConnectorMessage} instance with updated direction, roles, and party
     *         information, reflecting the switched communication flow.
     */
    @Nonnull
    public ConnectorMessage switchDirection() {
        final var as4Properties = this.as4Properties();
        final var direction = this.direction();
        final var fromParty = as4Properties.fromParty();
        final var toParty = as4Properties.toParty();

        var switchedAS4PropertiesBuilder = this.as4Properties().toBuilder();
        // switching party, but keep Role and RoleType
        final var switchedFromParty = toParty.toBuilder()
                                             .roleType(ConnectorPartyRoleType.INITIATOR)
                                             .role(fromParty.role())
                                             .build();

        final var switchedToParty = fromParty.toBuilder()
                                             .roleType(ConnectorPartyRoleType.RESPONDER)
                                             .role(toParty.role())
                                             .build();

        switchedAS4PropertiesBuilder.fromParty(switchedFromParty);
        switchedAS4PropertiesBuilder.toParty(switchedToParty);
        switchedAS4PropertiesBuilder.originalSender(as4Properties.finalRecipient());
        switchedAS4PropertiesBuilder.finalRecipient(as4Properties.originalSender());

        var switchedMessageBuilder = this.toBuilder();
        switchedMessageBuilder.direction(
                ConnectorMessageDirection.from(direction.getTarget(), direction.getSource())
        );
        switchedMessageBuilder.as4Properties(switchedAS4PropertiesBuilder.build());

        return switchedMessageBuilder.build();
    }

    @Override
    @Nonnull
    public String toString() {
        return String.format(
                "{identifier=%s, backendMessageIdentifier=%s, backendName=%s, gatewayName=%s, "
                        + "referenceToBackendMessageIdentifier=%s,  direction=%s, as4Properties=%s "
                        + "businessContent=%s",
                identifier, backendMessageIdentifier, backendName, gatewayName,
                referenceToBackendMessageIdentifier, direction, as4Properties, businessContent
        );
    }
}
