/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.spi.message;

import eu.ecodex.connector.domain.model.message.ConnectorMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.message.evidence.ConnectorMessageEvidence;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * e-Codex Connector Message Repository.
 */
public interface ConnectorMessageRepository {
    /**
     * Persists a given {@link ConnectorMessage} instance in the repository.
     *
     * @param message the {@link ConnectorMessage} to be saved; must not be null. This object
     *                contains the detailed metadata and businessContent of the connector message.
     *
     * @return the saved {@link ConnectorMessage}, including any automatically generated fields such
     *         as timestamps or identifiers. If the save operation fails, this method may throw an
     *         exception.
     */
    ConnectorMessage save(@Nonnull ConnectorMessage message);

    ConnectorMessage updateGatewayName(@Nonnull String identifier, @Nonnull String name);


    ConnectorMessage updateBackendName(@Nonnull String identifier, @Nonnull String name);

    ConnectorMessage updateEbmsIdentifier(
            @Nonnull String identifier, @Nonnull String ebmsIdentifier);

    /**
     * Updates the backend identifier of a {@link ConnectorMessage} with the given identifier. This
     * method modifies the backend-specific identification for the message in the repository.
     *
     * @param identifier        the unique identifier of the {@link ConnectorMessage} to be updated;
     *                          must not be null.
     * @param backendIdentifier the new backend identifier to associate with the
     *                          {@link ConnectorMessage}; must not be null.
     *
     * @return the updated {@link ConnectorMessage} after the backend identifier has been modified.
     */
    ConnectorMessage updateBackendIdentifier(
            @Nonnull String identifier, @Nonnull String backendIdentifier);

    /**
     * Retrieves a paginated result of {@link ConnectorMessage} objects that match the provided
     * criteria based on the specified {@code request}, {@code identifier}, and {@code backendName}.
     * This method facilitates fetching connector messages that match the given filters.
     *
     * @param request     the pagination and sorting information for the retrieval operation. This
     *                    includes details such as page size, page number, and sorting order; must
     *                    not be null.
     * @param identifier  a unique identifier to filter {@link ConnectorMessage} objects. This can
     *                    be used to narrow down the search results to messages associated with a
     *                    specific identifier; may be null or empty if no filtering by identifier is
     *                    needed.
     * @param backendName the name of the backend to filter {@link ConnectorMessage} objects. This
     *                    allows for scoping the search results to messages associated with a
     *                    specific backend name; may be null or empty if no backend filtering is
     *                    required.
     *
     * @return a {@link ConnectorPageResult} containing a list of {@link ConnectorMessage} instances
     *         that match the specified criteria. The result includes pagination details such as
     *         total elements and total pages. If no messages match the criteria, an empty result is
     *         returned.
     */
    ConnectorPageResult<ConnectorMessage> findAll(
            ConnectorPageRequest request,
            String identifier,
            String backendName
    );

    /**
     * Retrieves a list of all {@link ConnectorMessage} instances associated with the provided
     * identifiers.
     *
     * @param identifiers a list of unique identifiers used to filter {@link ConnectorMessage}
     *                    instances. Each identifier represents a target message; must not be null
     *                    or empty. If the list is empty or null, no results will be returned.
     *
     * @return a list of {@link ConnectorMessage} instances that match one or more of the specified
     *         identifiers. If no messages match, an empty list is returned.
     */
    List<ConnectorMessage> findAllByIdentifier(@Nonnull List<String> identifiers);

    /**
     * Finds a {@link ConnectorMessage} by its unique identifier.
     *
     * @param identifier the unique identifier of the connector message; must not be null or blank.
     *
     * @return the {@link ConnectorMessage} matching the specified identifier, or null if no such
     *         message exists in the repository.
     */
    ConnectorMessage findByIdentifier(String identifier);

    /**
     * Finds a {@link ConnectorMessage} by its ebMS message identifier.
     *
     * @param ebmsMessageIdentifier the ebMS message identifier; must not be null or blank
     *
     * @return the matching {@link ConnectorMessage}, or null if no such message exists
     */
    ConnectorMessage findByEbmsMessageIdentifier(@Nonnull String ebmsMessageIdentifier);

    /**
     * Updates backend routing metadata required to deliver a gateway confirmation message to the
     * originating backend system.
     *
     * @param identifier                            the confirmation message identifier; must not be
     *                                              null
     * @param backendName                           the backend link partner name; must not be null
     * @param referenceToBackendMessageIdentifier   the backend identifier of the referenced
     *                                              business message; must not be null
     *
     * @return the updated {@link ConnectorMessage}
     */
    ConnectorMessage updateBackendContext(
            @Nonnull String identifier,
            @Nonnull String backendName,
            @Nonnull String referenceToBackendMessageIdentifier
    );

    /**
     * Finds a {@link ConnectorMessage} in the repository based on the provided message instance and
     * the specified direction. This method is used to retrieve a connector message that matches
     * both the content of the given {@code message} and the {@code direction}.
     *
     * @param message   the {@link ConnectorMessage} instance containing the identifier or content
     *                  to search for; must not be null.
     * @param direction the {@link ConnectorMessageDirection} specifying the direction of the
     *                  message (such as inbound or outbound); must not be null.
     *
     * @return the {@link ConnectorMessage} that matches both the specified {@code message} and
     *         {@code direction}, or null if no matching message is found in the repository.
     */
    ConnectorMessage findByIdentifierAndDirection(
            ConnectorMessage message, ConnectorMessageDirection direction);

    /**
     * Retrieves a list of {@link ConnectorMessage} objects associated with the specified
     * conversation uuid.
     *
     * @param conversationIdentifier the unique uuid of the conversation; must not be null or
     *                               blank.
     *
     * @return a list of {@link ConnectorMessage} instances associated with the given conversation
     *         uuid, or an empty list if no such messages exist.
     */
    List<ConnectorMessage> findByConversationIdentifier(@Nonnull String conversationIdentifier);

    /**
     * Adds the specified evidence to the given connector message.
     *
     * @param message  the connector message to which the evidence will be added; must not be null.
     * @param evidence the evidence to be added to the message; must not be null.
     *
     * @return the updated connector message after the evidence has been added.
     */
    ConnectorMessage addEvidence(ConnectorMessage message, ConnectorMessageEvidence evidence);

    /**
     * Marks the provided {@link ConnectorMessage} as rejected. This typically involves updating the
     * status of the message to indicate that it has been rejected, and may include additional
     * actions such as logging or persisting this state change in the repository.
     *
     * @param identifier the identifier of {@link ConnectorMessage} to be marked as rejected; must
     *                   not be null.
     *
     * @return the updated {@link ConnectorMessage} instance reflecting the rejected status.
     */
    ConnectorMessage setAsRejected(@Nonnull String identifier);

    /**
     * Marks the provided {@link ConnectorMessage} as confirmed. This typically involves updating
     * the status of the message to reflect a confirmed state and may include additional persistence
     * or logging actions.
     *
     * @param identifier the identifier of the {@link ConnectorMessage} to be marked as confirmed;
     *                   must not be null. This object contains the detailed metadata and content of
     *                   the connector message.
     *
     * @return the updated {@link ConnectorMessage} instance after being marked as confirmed.
     */
    ConnectorMessage setAsConfirmed(@Nonnull String identifier);

    ConnectorMessage setDeliveredToGatewayAt(@Nonnull String identifier);

    ConnectorMessage setDeliveredToBackendAt(@Nonnull String identifier);
}
