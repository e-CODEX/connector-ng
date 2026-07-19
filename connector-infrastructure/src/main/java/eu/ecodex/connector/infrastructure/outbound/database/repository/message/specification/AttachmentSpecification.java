/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.database.repository.message.specification;

import eu.ecodex.connector.domain.model.message.attachment.ConnectorAttachmentType;
import eu.ecodex.connector.infrastructure.outbound.database.entity.message.ConnectorMessageAttachmentEntity;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification for {@link ConnectorMessageAttachmentEntity}.
 */
public class AttachmentSpecification {
    private static final String MESSAGE_FIELD = "message";
    private static final String IDENTIFIER_FIELD = "identifier";
    private static final String TYPE_FIELD = "type";

    public static Specification<ConnectorMessageAttachmentEntity> hasMessageIdentifierAndTypeIn(
        String messageUuid,
        List<ConnectorAttachmentType> types) {
        return Specification.where(withMessageUuid(messageUuid)).and(withTypes(types));
    }

    private static Specification<ConnectorMessageAttachmentEntity> withMessageUuid(
        String messageUuid) {
        return ((root, query, cb) -> {
            if (messageUuid == null) {
                return null;
            }

            return cb.equal(root.get(MESSAGE_FIELD).get(IDENTIFIER_FIELD), messageUuid);
        });
    }

    private static Specification<ConnectorMessageAttachmentEntity> withTypes(
        List<ConnectorAttachmentType> types) {
        return ((root, query, cb) -> {
            if (types == null || types.isEmpty()) {
                return null;
            }

            return root.get(TYPE_FIELD).in(types);
        });
    }
}
