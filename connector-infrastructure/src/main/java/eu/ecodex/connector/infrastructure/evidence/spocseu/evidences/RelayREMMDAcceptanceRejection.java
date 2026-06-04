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
import eu.ecodex.connector.infrastructure.evidence.spocseu.exception.SpocsWrongInputDataException;
import eu.ecodex.connector.infrastructure.evidence.spocseu.model.EDeliveryDetails;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import java.io.InputStream;
import java.io.OutputStream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;

/**
 * The RelayREMMDAcceptanceRejection class represents an evidence type used within the system for
 * creating or identifying relay acceptance/rejection events. It encapsulates the REMEvidenceType
 * object and provides various constructors for creating instances of this evidence based on
 * different input configurations or prior evidence.
 *
 * <p>The class also includes utility methods for initializing evidence data and serializing the
 * underlying JAXB object.
 */
@Slf4j
public class RelayREMMDAcceptanceRejection extends Evidence {
    public static final String LOG_SUCCESS =
            "Create RelayREMMDAcceptanceRejection in success case.";
    public static final String LOG_FAULT = "Create RelayREMMDAcceptanceRejection in fault case.";

    @Getter
    private boolean successful;

    /**
     * Constructs a new instance of the {@code RelayREMMDAcceptanceRejection} class using the
     * provided REM evidence type.
     *
     * @param evidenceType The {@code REMEvidenceType} object representing the evidence type for the
     *                     acceptance or rejection event.
     */
    public RelayREMMDAcceptanceRejection(REMEvidenceType evidenceType) {
        super(evidenceType);
    }

    /**
     * Constructs a new instance of {@code RelayREMMDAcceptanceRejection} using the provided XML
     * stream. The XML stream is unmarshalled to extract a {@code REMEvidenceType} object.
     *
     * @param acceptanceRejectionStream The input stream containing XML data representing the REM
     *                                  evidence type for an acceptance or rejection event.
     *
     * @throws SpocsWrongInputDataException If the input XML stream cannot be unmarshalled into a
     *                                      valid {@code REMEvidenceType} or if the unmarshalling
     *                                      process encounters errors.
     */
    public RelayREMMDAcceptanceRejection(InputStream acceptanceRejectionStream)
            throws SpocsWrongInputDataException {
        try {
            var unmarshalled = JaxbContextHolder.getSpocsJaxBContext()
                                                .createUnmarshaller()
                                                .unmarshal(acceptanceRejectionStream);
            if (!(unmarshalled instanceof JAXBElement<?> element)
                    || !(element.getValue() instanceof REMEvidenceType rem)) {
                throw new SpocsWrongInputDataException(
                        "XML stream did not unmarshal to a REMEvidenceType; got: "
                                + (unmarshalled == null
                                ? "null"
                                : unmarshalled.getClass().getSimpleName()));
            }
            jaxbObj = rem;
        } catch (JAXBException ex) {
            throw new SpocsWrongInputDataException(
                    "Error reading the RelayREMMDAcceptanceRejection xml stream.",
                    ex
            );
        }
    }

    /**
     * This constructor initializes a new instance of the RelayREMMDAcceptanceRejection class using
     * the provided delivery details and evidence of submission acceptance or rejection.
     *
     * @param details                       An object containing the eDelivery configuration details
     *                                      such as gateway information and delivery metadata.
     * @param submissionAcceptanceRejection The evidence object representing the acceptance or
     *                                      rejection of a submission in the eDelivery system.
     */
    public RelayREMMDAcceptanceRejection(
            EDeliveryDetails details,
            Evidence submissionAcceptanceRejection) {
        super(details);
        init(submissionAcceptanceRejection.getXSDObject(), true);
    }

    /**
     * Creates a new instance of RelayREMMDAcceptanceRejection using the specified delivery details,
     * evidence of submission acceptance or rejection, and acceptance status.
     *
     * @param details                       Configuration object containing relevant delivery
     *                                      details.
     * @param submissionAcceptanceRejection The evidence that represents either an acceptance or
     *                                      rejection of submission.
     * @param isAcceptance                  Flag indicating whether the evidence corresponds to an
     *                                      acceptance (true) or rejection (false).
     */
    public RelayREMMDAcceptanceRejection(
            EDeliveryDetails details,
            Evidence submissionAcceptanceRejection,
            boolean isAcceptance) {
        super(details);
        init(submissionAcceptanceRejection.getXSDObject(), isAcceptance);
    }

    public RelayREMMDAcceptanceRejection(
            EDeliveryDetails details,
            REMEvidenceType submissionAcceptanceRejection,
            boolean isAcceptance) {
        initEvidenceIssuerDetailsWithEDeliveryDetails(details);
        init(submissionAcceptanceRejection, isAcceptance);
    }

    /**
     * Constructs a new instance of {@code RelayREMMDAcceptanceRejection} by initializing it with
     * the provided single evidence and delivery details. The constructor sets up the evidence type,
     * configures the event code, and initializes the instance using the provided evidence.
     *
     * @param singleEvidence The {@code REMEvidenceType} object representing the evidence related to
     *                       a relay REM MD acceptance or rejection event.
     * @param details        The {@code EDeliveryDetails} object containing the configuration
     *                       details and metadata for eDelivery.
     */
    public RelayREMMDAcceptanceRejection(REMEvidenceType singleEvidence, EDeliveryDetails details) {
        super(details);
        init(singleEvidence, false);
    }

    @Override
    public void serialize(OutputStream out) throws JAXBException {
        JaxbContextHolder.getSpocsJaxBContext()
                         .createMarshaller()
                         .marshal(
                                 new ObjectFactory().createRelayREMMDAcceptanceRejection(jaxbObj),
                                 out
                         );
    }

    private void init(REMEvidenceType submissionAcceptanceRejection, boolean isAcceptance) {
        evidenceType = Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION;

        if (isAcceptance) {
            log.debug(LOG_SUCCESS);
            setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION.getSuccessEventCode());
        } else {
            log.debug(LOG_FAULT);
            setEventCode(Evidences.RELAY_REM_MD_ACCEPTANCE_REJECTION.getFaultEventCode());
        }

        initWithPrevious(submissionAcceptanceRejection);
        successful = isAcceptance;
    }
}
