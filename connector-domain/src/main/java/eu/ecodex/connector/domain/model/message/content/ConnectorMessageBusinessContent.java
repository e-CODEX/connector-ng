/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.domain.model.message.content;

import eu.ecodex.connector.domain.model.pmode.ConnectorAction;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import java.io.Serializable;
import lombok.Builder;

/**
 * Message businessContent holds the main businessContent of a message. This is the XML data of
 * the main Form of the message and the printable businessDocument that most of the
 * {@link ConnectorAction} require.
 *
 * <p>A message is a business message only if a messageContent is present.
 *
 * @param uuid The unique uuid of the message businessContent.
 * @param xmlContent The XML businessContent of the message.
 * @param businessDocument The printable businessDocument of the message.
 */
@Builder(toBuilder = true)
public record ConnectorMessageBusinessContent(
        @Nullable String uuid,
        @Nonnull String xmlContent,
        @Nullable ConnectorMessageBusinessDocument businessDocument // TODO set as non nullable
) implements Serializable {
    @Override
    @Nonnull
    public String toString() {
        return String.format("{uuid=%s, businessDocument=%s}", uuid, businessDocument);
    }
}
