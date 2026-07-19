/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.evidences;

import eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.JaxbContextHolder;
import eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.common.SpocsConstants.Evidences;
import eu.ecodex.connector.infrastructure.outbound.evidence.spocseu.model.EDeliveryDetails;
import jakarta.xml.bind.JAXBException;
import java.io.OutputStream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;

/**
 * Represents an evidence type used to handle the process of retrieval or non-retrieval of
 * information by a recipient based on predefined evidence types.
 */
@Slf4j
public class RetrievalNonRetrievalByRecipient extends Evidence {
    public static final String LOG_SUCCESS =
        "Create RetrievalNonRetrievalByRecipient in success case.";
    public static final String LOG_FAULT = "Create RetrievalNonRetrievalByRecipient in fault case.";

    @Getter
    private boolean successful;

    public RetrievalNonRetrievalByRecipient(
        EDeliveryDetails details,
        REMEvidenceType evidenceType,
        boolean isAcceptance) {
        initEvidenceIssuerDetailsWithEDeliveryDetails(details);
        init(evidenceType, isAcceptance);
    }

    @Override
    public void serialize(OutputStream out) throws JAXBException {
        JaxbContextHolder
            .getSpocsJaxBContext()
            .createMarshaller()
            .marshal(
                new ObjectFactory()
                    .createRetrievalNonRetrievalByRecipient(jaxbObj),
                out
            );
    }

    private void init(REMEvidenceType previousEvidence, boolean isAcceptance) {
        evidenceType = Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT;
        if (isAcceptance) {
            log.debug(LOG_SUCCESS);
            setEventCode(Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT
                             .getSuccessEventCode());
        } else {
            log.debug(LOG_FAULT);
            setEventCode(Evidences.RETRIEVAL_NON_RETRIEVAL_BY_RECIPIENT
                             .getFaultEventCode());
        }
        initWithPrevious(previousEvidence);
        successful = isAcceptance;
    }
}
