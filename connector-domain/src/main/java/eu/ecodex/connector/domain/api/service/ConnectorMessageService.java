/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.api.service;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * Service interface for managing and persisting connector messages.
 */
public interface ConnectorMessageService {
    /**
     * Registers a new connector message within the system.
     *
     * @param message the {@link ConnectorMessage} to be registered; must not be null
     *
     * @return the registered {@link ConnectorMessage} instance with any applied modifications
     */
    ConnectorMessage register(@Nonnull ConnectorMessage message);

    /**
     * Creates a new connector message representing an evidence message based on the supplied
     * business message and evidence.
     *
     * @param businessMessage the original business message to which the evidence relates; must not
     *                        be null
     * @param evidence        the evidence to be associated with the created evidence message; must
     *                        not be null
     *
     * @return a newly created evidence message containing the specified evidence
     */
    ConnectorMessage createEvidenceMessage(
            @Nonnull ConnectorMessage businessMessage, @Nonnull ConnectorMessageEvidence evidence);

    /**
     * Retrieves a {@link ConnectorMessage} using the given unique uuid.
     *
     * @param identifier the unique uuid of the connector message; must not be null.
     *
     * @return the {@link ConnectorMessage} corresponding to the specified uuid, or {@code null} if
     *         no such message exists.
     */
    ConnectorMessage findByIdentifier(@Nonnull String identifier);

    /**
     * Retrieves a {@link ConnectorMessage} based on the specified message and direction.
     *
     * @param message   the connector message to be matched; must not be null
     * @param direction the direction of the connector message to be matched; must not be null
     *
     * @return the {@link ConnectorMessage} that matches the given message and direction, or
     *         {@code null} if no match is found
     */
    ConnectorMessage findByIdentifierAndDirection(
            @Nonnull ConnectorMessage message, @Nonnull ConnectorMessageDirection direction);

    /**
     * Retrieves a list of {@link ConnectorMessage} entities that are associated with the specified
     * conversation uuid.
     *
     * @param conversationIdentifier the unique uuid of the conversation to search for; must not be
     *                               null.
     *
     * @return a list of {@link ConnectorMessage} instances that match the given conversation uuid;
     *         an empty list if no such messages are found.
     */
    List<ConnectorMessage> findByConversationIdentifier(@Nonnull String conversationIdentifier);

    /**
     * Adds a specified evidence to a given connector message and returns the updated message
     * instance.
     *
     * @param message  the connector message to which the evidence will be added; must not be null
     * @param evidence the evidence to be associated with the connector message; must not be null
     *
     * @return the updated connector message instance containing the newly added evidence
     */
    ConnectorMessage addEvidence(
            @Nonnull ConnectorMessage message, @Nonnull ConnectorMessageEvidence evidence);

    /**
     * Validates and processes the party information associated with the provided connector
     * message.
     *
     * @param message the connector message whose party information is to be verified and processed;
     *                must not be null
     */
    void checkPartiesInfo(@Nonnull ConnectorMessage message);

    /**
     * Determines whether the given connector message is classified as a business message.
     *
     * @param message the connector message to be evaluated; must not be null
     *
     * @return {@code true} if the message is a business message, {@code false} otherwise
     */
    boolean isBusinessMessage(@Nonnull ConnectorMessage message);

    /**
     * Determines whether the given connector message is classified as an evidence message.
     *
     * @param message the connector message to be evaluated; must not be null
     *
     * @return {@code true} if the message is an evidence message, {@code false} otherwise
     */
    boolean isEvidenceMessage(@Nonnull ConnectorMessage message);

    /**
     * Determines whether the given connector message qualifies as an evidence trigger message.
     *
     * @param message the connector message to be evaluated; must not be null
     *
     * @return {@code true} if the message is classified as an evidence trigger message,
     *         {@code false} otherwise
     */
    boolean isEvidenceTriggerMessage(@Nonnull ConnectorMessage message);

    /**
     * Marks the specified {@link ConnectorMessage} as rejected by updating its internal state to
     * reflect that it has been declined or deemed unacceptable. The rejection timestamp is set, and
     * the updated message instance is returned.
     *
     * @param message the connector message to be marked as rejected; must not be null
     *
     * @return the updated instance of the connector message with the rejection status applied
     */
    ConnectorMessage setAsRejected(@Nonnull ConnectorMessage message);

    /**
     * Marks the specified {@link ConnectorMessage} as confirmed by updating its internal state to
     * reflect that it has been successfully processed or acknowledged. The confirmation timestamp
     * is set, and the updated message instance is returned.
     *
     * @param message the connector message to be marked as confirmed; must not be null
     *
     * @return the updated instance of the connector message with the confirmation status applied
     */
    ConnectorMessage setAsConfirmed(@Nonnull ConnectorMessage message);

    /**
     * Checks whether the provided connector message has been marked as rejected.
     *
     * @param message the connector message to be checked; must not be null
     *
     * @return {@code true} if the message is marked as rejected, {@code false} otherwise
     */
    boolean isRejected(@Nonnull ConnectorMessage message);

    /**
     * Reverses the direction of the given connector message and returns the updated message
     * instance.
     *
     * @param message the connector message whose direction is to be switched, must not be null
     *
     * @return the connector message with its direction switched
     */
    ConnectorMessage switchDirection(@Nonnull ConnectorMessage message);

    /**
     * Assigns a unique EBMS (eBusiness Messaging Service) uuid to the specified connector message.
     * The uuid is generated and associated with the message to ensure its uniqueness within the
     * context of the messaging system.
     *
     * @param message the connector message to which the EBMS uuid will be assigned; must not be
     *                null
     *
     * @return the updated connector message instance with the assigned EBMS uuid
     */
    ConnectorMessage assignEbmsIdentifier(@Nonnull ConnectorMessage message);
}
