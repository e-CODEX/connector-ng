/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence.spocseu.evidences;

import eu.ecodex.connector.infrastructure.evidence.spocseu.JaxbContextHolder;
import eu.ecodex.connector.infrastructure.evidence.spocseu.common.SpocsConstants.Evidences;
import eu.ecodex.connector.infrastructure.evidence.spocseu.model.EDeliveryDetails;
import jakarta.xml.bind.JAXBException;
import java.io.OutputStream;
import lombok.extern.slf4j.Slf4j;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;

/**
 * The RelayREMMDFailure class represents a specific type of evidence related to a failure event in
 * the context of relay REM MD (Registered Electronic Mail Metadata).
 */
@Slf4j
public class RelayREMMDFailure extends Evidence {
    public RelayREMMDFailure(
            EDeliveryDetails details,
            REMEvidenceType submissionAcceptanceRejection) {
        initEvidenceIssuerDetailsWithEDeliveryDetails(details);
        init(submissionAcceptanceRejection);
    }

    @Override
    public void serialize(OutputStream out) throws JAXBException {
        JaxbContextHolder
                .getSpocsJaxBContext()
                .createMarshaller()
                .marshal(
                        new ObjectFactory().createRelayREMMDFailure(jaxbObj),
                        out
                );
    }

    private void init(REMEvidenceType submissionAcceptanceRejection) {
        log.debug("Create RelayREMMDFailure.");
        evidenceType = Evidences.RELAY_REM_MD_FAILURE;
        setEventCode(Evidences.RELAY_REM_MD_FAILURE.getFaultEventCode());

        initWithPrevious(submissionAcceptanceRejection);
    }
}
