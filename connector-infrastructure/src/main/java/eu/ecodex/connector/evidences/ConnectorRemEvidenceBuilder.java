/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.evidences;

import eu.ecodex.connector.evidences.exception.ECodexEvidenceBuilderException;
import eu.ecodex.connector.evidences.types.ECodexMessageDetails;
import eu.ecodex.connector.infrastructure.evidence.DssConnectorRemEvidenceXmlSigner;
import eu.ecodex.connector.infrastructure.evidence.RemEvidenceUnmarshaller;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.evidences.DeliveryNonDeliveryToRecipient;
import eu.spocseu.edeliverygw.evidences.Evidence;
import eu.spocseu.edeliverygw.evidences.RelayREMMDAcceptanceRejection;
import eu.spocseu.edeliverygw.evidences.RelayREMMDFailure;
import eu.spocseu.edeliverygw.evidences.RetrievalNonRetrievalByRecipient;
import eu.spocseu.edeliverygw.evidences.SubmissionAcceptanceRejection;
import eu.spocseu.edeliverygw.messageparts.SpocsFragments;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.extern.slf4j.Slf4j;
import org.etsi.uri._02640.soapbinding.v1_.DeliveryConstraints;
import org.etsi.uri._02640.soapbinding.v1_.Destinations;
import org.etsi.uri._02640.soapbinding.v1_.MsgIdentification;
import org.etsi.uri._02640.soapbinding.v1_.MsgMetaData;
import org.etsi.uri._02640.soapbinding.v1_.Originators;
import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.v2.EntityDetailsType;
import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.springframework.stereotype.Component;

/**
 * Spring implementation of {@link EvidenceBuilder}: marshals SPOCS REM evidence objects to XML,
 * then signs them with an enveloped XAdES-B signature via {@link DssConnectorRemEvidenceXmlSigner}.
 *
 * <p>Chain steps that extend a previous evidence parse {@code previousEvidenceInByte} with
 * {@link RemEvidenceUnmarshaller} to obtain a {@link REMEvidenceType} root before building the
 * next wrapper.
 */
@Slf4j
@Component
public class ConnectorRemEvidenceBuilder implements EvidenceBuilder {
    private final DssConnectorRemEvidenceXmlSigner xmlSigner;

    /**
     * Creates the builder with the REM XML signer.
     *
     * @param xmlSigner signs unsigned marshalled evidence XML
     */
    public ConnectorRemEvidenceBuilder(DssConnectorRemEvidenceXmlSigner xmlSigner) {
        this.xmlSigner = xmlSigner;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createSubmissionAcceptanceRejection(
            boolean isAcceptance, REMErrorEvent eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            ECodexMessageDetails messageDetails) throws ECodexEvidenceBuilderException {
        EventReasonType reason = null;
        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }
        return createSubmissionAcceptanceRejection(isAcceptance, reason, evidenceIssuerDetails,
                                                   messageDetails);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createSubmissionAcceptanceRejection(
            boolean isAcceptance, EventReasonType eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            ECodexMessageDetails messageDetails) throws ECodexEvidenceBuilderException {

        var recipient = new EntityDetailsType();
        var sender = new EntityDetailsType();
        try {
            recipient.getAttributedElectronicAddressOrElectronicAddress().add(
                    SpocsFragments.createElectronicAddress(
                            messageDetails.getSenderAddress(),
                            "displayName"
                    ));
            sender.getAttributedElectronicAddressOrElectronicAddress().add(
                    SpocsFragments.createElectronicAddress(
                            messageDetails.getRecipientAddress(),
                            "displayName"
                    ));
        } catch (MalformedURLException e) {
            log.warn("electronic address", e);
        }

        var destinations = new Destinations();
        destinations.setRecipient(sender);

        var originators = new Originators();
        originators.setFrom(recipient);
        originators.setReplyTo(recipient);
        originators.setSender(recipient);

        var msgIdentification = new MsgIdentification();
        msgIdentification.setMessageID(messageDetails.getEbmsMessageId());

        var msgMetaData = new MsgMetaData();
        msgMetaData.setDestinations(destinations);
        msgMetaData.setOriginators(originators);
        msgMetaData.setMsgIdentification(msgIdentification);

        var cal = new GregorianCalendar();
        XMLGregorianCalendar initialSend;
        try {
            initialSend = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (DatatypeConfigurationException e) {
            throw new ECodexEvidenceBuilderException(e);
        }
        var deliveryConstraints = new DeliveryConstraints();
        deliveryConstraints.setInitialSend(initialSend);
        msgMetaData.setDeliveryConstraints(deliveryConstraints);

        var dispatch = new REMDispatchType();
        dispatch.setMsgMetaData(msgMetaData);

        var evidence = new SubmissionAcceptanceRejection(
                evidenceIssuerDetails, dispatch, isAcceptance
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        evidence.setUAMessageId(messageDetails.getNationalMessageId());
        evidence.setHashInformation(
                messageDetails.getHashValue(), messageDetails.getHashAlgorithm());

        var start = new Date();
        byte[] signedByteArray = signEvidence(evidence, false);
        log.info("SubmissionAcceptanceRejection evidence created in {} ms",
                 System.currentTimeMillis() - start.getTime());
        return signedByteArray;
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createRelayREMMDAcceptanceRejection(
            boolean isAcceptance, REMErrorEvent eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ECodexEvidenceBuilderException {
        EventReasonType reason = null;
        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }
        return createRelayREMMDAcceptanceRejection(
                isAcceptance, reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createRelayREMMDAcceptanceRejection(
            boolean isAcceptance, EventReasonType eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ECodexEvidenceBuilderException {

        REMEvidenceType previousEvidence =
                RemEvidenceUnmarshaller.parseSignedEvidenceXml(previousEvidenceInByte);

        var evidence = new RelayREMMDAcceptanceRejection(
                evidenceIssuerDetails, previousEvidence, isAcceptance
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        return signEvidence(evidence, true);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createRelayREMMDFailure(
            REMErrorEvent eventReason, EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ECodexEvidenceBuilderException {
        EventReasonType reason = null;
        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }
        return createRelayREMMDFailure(reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createRelayREMMDFailure(
            EventReasonType eventReason, EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ECodexEvidenceBuilderException {

        REMEvidenceType previousEvidence =
                RemEvidenceUnmarshaller.parseSignedEvidenceXml(previousEvidenceInByte);

        var evidence = new RelayREMMDFailure(evidenceIssuerDetails, previousEvidence);

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        return signEvidence(evidence, true);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createDeliveryNonDeliveryToRecipient(
            boolean isDelivery, REMErrorEvent eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ECodexEvidenceBuilderException {
        EventReasonType reason = null;
        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }
        return createDeliveryNonDeliveryToRecipient(
                isDelivery, reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createDeliveryNonDeliveryToRecipient(
            boolean isDelivery, EventReasonType eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ECodexEvidenceBuilderException {

        REMEvidenceType previousEvidence =
                RemEvidenceUnmarshaller.parseSignedEvidenceXml(previousEvidenceInByte);

        var evidence = new DeliveryNonDeliveryToRecipient(
                evidenceIssuerDetails, previousEvidence, isDelivery
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        return signEvidence(evidence, true);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createRetrievalNonRetrievalByRecipient(
            boolean isRetrieval, REMErrorEvent eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ECodexEvidenceBuilderException {
        EventReasonType reason = null;
        if (eventReason != null) {
            reason = new EventReasonType();
            reason.setCode(eventReason.getEventCode());
            reason.setDetails(eventReason.getEventDetails());
        }
        return createRetrievalNonRetrievalByRecipient(
                isRetrieval, reason, evidenceIssuerDetails, previousEvidenceInByte);
    }

    /** {@inheritDoc} */
    @Override
    public byte[] createRetrievalNonRetrievalByRecipient(
            boolean isRetrieval, EventReasonType eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ECodexEvidenceBuilderException {

        REMEvidenceType previousEvidence =
                RemEvidenceUnmarshaller.parseSignedEvidenceXml(previousEvidenceInByte);

        var evidence = new RetrievalNonRetrievalByRecipient(
                evidenceIssuerDetails, previousEvidence, isRetrieval
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        return signEvidence(evidence, true);
    }

    /**
     * Marshals {@code evidenceToBeSigned} to XML, optionally clears an existing in-object
     * signature, then returns
     * {@link DssConnectorRemEvidenceXmlSigner#signUnsignedRemEvidenceXml(byte[])} output.
     */
    private byte[] signEvidence(Evidence evidenceToBeSigned, boolean removeOldSignature)
            throws ECodexEvidenceBuilderException {

        if (removeOldSignature) {
            evidenceToBeSigned.getXSDObject().setSignature(null);
            log.debug("old signature removed from evidence chain step");
        }

        var fo = new ByteArrayOutputStream();
        try {
            evidenceToBeSigned.serialize(fo);
        } catch (JAXBException e) {
            throw new ECodexEvidenceBuilderException("cannot serialize evidence", e);
        }

        byte[] bytes = fo.toByteArray();
        return xmlSigner.signUnsignedRemEvidenceXml(bytes);
    }
}
