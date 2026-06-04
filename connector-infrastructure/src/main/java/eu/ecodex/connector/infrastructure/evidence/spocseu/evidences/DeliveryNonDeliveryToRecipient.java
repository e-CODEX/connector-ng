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
import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;

/**
 * The DeliveryNonDeliveryToRecipient class represents an evidence object that encapsulates
 * information about the delivery or non-delivery of a message to its recipient based on specified
 * configurations or previous evidence.
 *
 * <p>This class provides several constructors to create instances using different input types such
 * as JAXB objects, serialized XML streams, or derived from existing evidence. It also offers
 * functionality to serialize the underlying evidence object back into an XML format.
 *
 * <p>The class differentiates between successful (delivery) and fault (non-delivery) cases through
 * the `isSuccessful` property and event codes, logging distinct messages for each scenario.
 */
@Slf4j
public class DeliveryNonDeliveryToRecipient extends Evidence {
    public static final String LOG_SUCCESS =
            "Create DeliveryNonDeliveryToRecipient in success case.";
    public static final String LOG_FAULT = "Create DeliveryNonDeliveryToRecipient in fault case.";

    @Getter
    private boolean successful;

    /**
     * This constructor creates this DeliveryNonDeliveryToRecipient evidence with the given JAXB
     * object and the configuration.
     *
     * @param evidenceType The JAXB object.
     */
    public DeliveryNonDeliveryToRecipient(REMEvidenceType evidenceType) {
        super(evidenceType);
    }

    /**
     * This constructor can be used to parse a serialized DeliveryNonDeliveryToRecipient xml stream
     * to create a JAXB evidence object.
     *
     * @param deliveryNonDeliveryStream The XML input stream with the evidence XML data.
     *
     * @throws SpocsWrongInputDataException In the case of parsing errors
     */
    public DeliveryNonDeliveryToRecipient(InputStream deliveryNonDeliveryStream)
            throws SpocsWrongInputDataException {
        try {
            var unmarshalled = JaxbContextHolder
                    .getSpocsJaxBContext()
                    .createUnmarshaller()
                    .unmarshal(deliveryNonDeliveryStream);
            if (!(unmarshalled instanceof JAXBElement<?> element)
                    || !(element.getValue() instanceof REMEvidenceType rem)) {
                throw new SpocsWrongInputDataException(
                        "XML stream did not unmarshal to a REMEvidenceType; got: "
                                + (unmarshalled == null ? "null"
                                : unmarshalled.getClass().getSimpleName()));
            }

            jaxbObj = rem;
        } catch (JAXBException ex) {
            throw new SpocsWrongInputDataException(
                    "Error reading the DeliveryNonDeliveryToRecipient xml stream.",
                    ex
            );
        }
    }

    /**
     * This constructor creates a DeliveryNonDeliveryToRecipient object based on
     * SubmissionAcceptanceRejection evidence.
     *
     * @param details                       Configuration object to set some properties
     * @param submissionAcceptanceRejection The previous SubmissionAcceptanceRejection
     */
    public DeliveryNonDeliveryToRecipient(
            EDeliveryDetails details,
            Evidence submissionAcceptanceRejection) {
        super(details);
        init(submissionAcceptanceRejection.getXSDObject(), true);
    }

    /**
     * This constructor creates a NonDeliveryToRecipient (false) evidence based on
     * SubmissionAcceptanceRejection evidence.
     *
     * @param details                       Configuration object to set some properties
     * @param submissionAcceptanceRejection The previous SubmissionAcceptanceRejection
     */
    public DeliveryNonDeliveryToRecipient(
            EDeliveryDetails details,
            Evidence submissionAcceptanceRejection,
            EventReasonType eventReason) {
        super(details);
        init(submissionAcceptanceRejection.getXSDObject(), false);
        super.setEventReason(eventReason);
    }

    /**
     * This constructor creates a DeliveryNonDeliveryToRecipient object based on previous
     * SubmissionAcceptanceRejection evidence.
     *
     * @param details                       Configuration object to set some properties
     * @param submissionAcceptanceRejection The previous SubmissionAcceptanceRejection
     * @param isDelivery                    If this value is false, a fault evidence event will be
     *                                      set.
     */
    public DeliveryNonDeliveryToRecipient(
            EDeliveryDetails details,
            Evidence submissionAcceptanceRejection,
            boolean isDelivery) {
        super(details);
        init(submissionAcceptanceRejection.getXSDObject(), isDelivery);
    }

    public DeliveryNonDeliveryToRecipient(
            EDeliveryDetails details,
            REMEvidenceType submissionAcceptanceRejection,
            boolean isDelivery) {
        initEvidenceIssuerDetailsWithEDeliveryDetails(details);
        init(submissionAcceptanceRejection, isDelivery);
    }

    @Override
    public void serialize(OutputStream out) throws JAXBException {
        JaxbContextHolder
                .getSpocsJaxBContext()
                .createMarshaller()
                .marshal(
                        new ObjectFactory().createDeliveryNonDeliveryToRecipient(jaxbObj),
                        out
                );
    }

    private void init(REMEvidenceType submissionAcceptanceRejection, boolean isDelivery) {
        evidenceType = Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT;

        if (isDelivery) {
            log.debug(LOG_SUCCESS);
            setEventCode(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT.getSuccessEventCode());
        } else {
            log.debug(LOG_FAULT);
            setEventCode(Evidences.DELIVERY_NON_DELIVERY_TO_RECIPIENT.getFaultEventCode());
        }

        initWithPrevious(submissionAcceptanceRejection);
        successful = isDelivery;
    }
}
