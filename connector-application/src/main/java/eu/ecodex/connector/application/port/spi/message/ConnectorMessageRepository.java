/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.application.port.spi.message;

import eu.ecodex.connector.domain.model.message.ConnectorBusinessMessage;
import eu.ecodex.connector.domain.model.message.ConnectorMessageDirection;
import eu.ecodex.connector.domain.model.paging.ConnectorPageRequest;
import eu.ecodex.connector.domain.model.paging.ConnectorPageResult;
import jakarta.annotation.Nonnull;
import java.util.List;

/**
 * e-Codex Connector Message Repository.
 */
public interface ConnectorMessageRepository {
    /**
     * Persists a given {@link ConnectorBusinessMessage} instance in the repository.
     *
     * @param message the {@link ConnectorBusinessMessage} to be saved; must not be null. This
     *                object contains the detailed metadata and businessContent of the connector
     *                message.
     *
     * @return the saved {@link ConnectorBusinessMessage}, including any automatically generated
     *     fields such as timestamps or identifiers. If the save operation fails, this method may
     *     throw an exception.
     */
    ConnectorBusinessMessage save(@Nonnull ConnectorBusinessMessage message);

    ConnectorBusinessMessage updateGatewayName(@Nonnull String identifier, @Nonnull String name);


    ConnectorBusinessMessage updateBackendName(@Nonnull String identifier, @Nonnull String name);

    ConnectorBusinessMessage updateEbmsIdentifier(
        @Nonnull String identifier, @Nonnull String ebmsIdentifier);

    /**
     * Updates the backend identifier of a {@link ConnectorBusinessMessage} with the given
     * identifier. This method modifies the backend-specific identification for the message in the
     * repository.
     *
     * @param identifier        the unique identifier of the {@link ConnectorBusinessMessage} to be
     *                          updated; must not be null.
     * @param backendIdentifier the new backend identifier to associate with the
     *                          {@link ConnectorBusinessMessage}; must not be null.
     *
     * @return the updated {@link ConnectorBusinessMessage} after the backend identifier has been
     *     modified.
     */
    ConnectorBusinessMessage updateBackendIdentifier(
        @Nonnull String identifier, @Nonnull String backendIdentifier);

    /**
     * Retrieves a paginated result of {@link ConnectorBusinessMessage} objects that match the
     * provided criteria based on the specified {@code request}, {@code identifier}, and
     * {@code backendName}. This method facilitates fetching connector messages that match the given
     * filters.
     *
     * @param request                  the pagination and sorting information for the retrieval
     *                                 operation. This includes details such as page size, page
     *                                 number, and sorting order; must not be null.
     * @param identifier               a unique identifier to filter
     *                                 {@link ConnectorBusinessMessage} objects. This can be used to
     *                                 narrow down the search results to messages associated with a
     *                                 specific identifier; may be null or empty if no filtering by
     *                                 identifier is needed.
     * @param backendName              the name of the backend to filter
     *                                 {@link ConnectorBusinessMessage} objects. This allows for
     *                                 scoping the search results to messages associated with a
     *                                 specific backend name; may be null or empty if no backend
     *                                 filtering is required.
     * @param businessDomainIdentifier the identifier of the business domain to filter
     * @param service                  the service name to filter messages
     * @param action                   the action name to filter messages
     *
     * @return a {@link ConnectorPageResult} containing a list of {@link ConnectorBusinessMessage}
     *     instances that match the specified criteria. The result includes pagination details such
     *     as total elements and total pages. If no messages match the criteria, an empty result is
     *     returned.
     */
    ConnectorPageResult<ConnectorBusinessMessage> findAll(
        ConnectorPageRequest request,
        String identifier,
        String backendName,
        String businessDomainIdentifier,
        String service,
        String action
    );

    /**
     * Retrieves a list of all {@link ConnectorBusinessMessage} instances associated with the
     * provided identifiers.
     *
     * @param identifiers a list of unique identifiers used to filter
     *                    {@link ConnectorBusinessMessage} instances. Each identifier represents a
     *                    target message; must not be null or empty. If the list is empty or null,
     *                    no results will be returned.
     *
     * @return a list of {@link ConnectorBusinessMessage} instances that match one or more of the
     *     specified identifiers. If no messages match, an empty list is returned.
     */
    List<ConnectorBusinessMessage> findAllByIdentifier(@Nonnull List<String> identifiers);

    /**
     * Finds a {@link ConnectorBusinessMessage} by its unique identifier.
     *
     * @param identifier the unique identifier of the connector message; must not be null or blank.
     *
     * @return the {@link ConnectorBusinessMessage} matching the specified identifier, or null if no
     *     such message exists in the repository.
     */
    ConnectorBusinessMessage findByIdentifier(@Nonnull String identifier);

    /**
     * Finds a {@link ConnectorBusinessMessage} by its ebMS message identifier.
     *
     * @param ebmsMessageIdentifier the ebMS message identifier; must not be null or blank
     * @param direction             the direction of the message; must not be null
     *
     * @return the matching {@link ConnectorBusinessMessage}, or null if no such message exists
     */
    ConnectorBusinessMessage findByEbmsMessageIdentifierAndDirection(
        @Nonnull String ebmsMessageIdentifier, @Nonnull ConnectorMessageDirection direction);

    /**
     * Finds a message by its backend-assigned message identifier.
     *
     * @param backendMessageIdentifier backend message id; must not be blank
     *
     * @return matching message, or null
     */
    ConnectorBusinessMessage findByBackendMessageIdentifier(
        @Nonnull String backendMessageIdentifier);

    ConnectorBusinessMessage findReferencedBusinessMessage(
        String referenceToMessageIdentifier, ConnectorMessageDirection triggerDirection);

    /**
     * Retrieves a list of {@link ConnectorBusinessMessage} objects associated with the specified
     * conversation uuid.
     *
     * @param conversationIdentifier the unique uuid of the conversation; must not be null or
     *                               blank.
     *
     * @return a list of {@link ConnectorBusinessMessage} instances associated with the given
     *     conversation uuid, or an empty list if no such messages exist.
     */
    List<ConnectorBusinessMessage> findByConversationIdentifier(
        @Nonnull String conversationIdentifier);

    /**
     * Marks the provided {@link ConnectorBusinessMessage} as rejected. This typically involves
     * updating the status of the message to indicate that it has been rejected, and may include
     * additional actions such as logging or persisting this state change in the repository.
     *
     * @param identifier the identifier of {@link ConnectorBusinessMessage} to be marked as
     *                   rejected; must not be null.
     *
     * @return the updated {@link ConnectorBusinessMessage} instance reflecting the rejected status.
     */
    ConnectorBusinessMessage setAsRejected(@Nonnull String identifier);

    /**
     * Marks the provided {@link ConnectorBusinessMessage} as confirmed. This typically involves
     * updating the status of the message to reflect a confirmed state and may include additional
     * persistence or logging actions.
     *
     * @param identifier the identifier of the {@link ConnectorBusinessMessage} to be marked as
     *                   confirmed; must not be null. This object contains the detailed metadata and
     *                   content of the connector message.
     *
     * @return the updated {@link ConnectorBusinessMessage} instance after being marked as
     *     confirmed.
     */
    ConnectorBusinessMessage setAsConfirmed(@Nonnull String identifier);

    /**
     * Sets the timestamp indicating when the item was delivered to the link partner, identified by
     * the given identifier.
     *
     * @param identifier A non-null unique identifier for the link partner to whom the item delivery
     *                   should be recorded.
     */
    ConnectorBusinessMessage setDeliveredToLinkPartnerAt(@Nonnull String identifier);
}
