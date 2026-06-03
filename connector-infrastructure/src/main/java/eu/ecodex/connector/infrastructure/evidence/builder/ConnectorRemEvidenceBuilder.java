/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence.builder;

import eu.ecodex.connector.infrastructure.dss.ConnectorDssDocumentSigner;
import eu.ecodex.connector.infrastructure.dss.ConnectorDssSigningTokenProvider;
import eu.ecodex.connector.infrastructure.evidence.exception.ConnectorEvidenceBuilderException;
import eu.ecodex.connector.infrastructure.evidence.model.ConnectorEvidenceMessageDetails;
import eu.ecodex.connector.infrastructure.evidence.spocseu.evidences.DeliveryNonDeliveryToRecipient;
import eu.ecodex.connector.infrastructure.evidence.spocseu.evidences.Evidence;
import eu.ecodex.connector.infrastructure.evidence.spocseu.evidences.RelayREMMDAcceptanceRejection;
import eu.ecodex.connector.infrastructure.evidence.spocseu.evidences.RelayREMMDFailure;
import eu.ecodex.connector.infrastructure.evidence.spocseu.evidences.RetrievalNonRetrievalByRecipient;
import eu.ecodex.connector.infrastructure.evidence.spocseu.evidences.SubmissionAcceptanceRejection;
import eu.ecodex.connector.infrastructure.evidence.spocseu.messageparts.SpocsFragments;
import eu.ecodex.connector.infrastructure.evidence.spocseu.model.EDeliveryDetails;
import eu.ecodex.connector.infrastructure.evidence.util.RemEvidenceUnmarshaller;
import eu.ecodex.connector.infrastructure.property.evidence.ConnectorEvidencesProperties;
import eu.europa.esig.dss.model.InMemoryDocument;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
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
 * The {@code ConnectorRemEvidenceBuilder} class is responsible for constructing and signing
 * REM evidences according to the Connector architecture requirements.
 */
@Slf4j
@Component
public class ConnectorRemEvidenceBuilder implements ConnectorEvidenceBuilder {
    private final ConnectorDssDocumentSigner documentSigner;
    private final ConnectorEvidencesProperties evidencesProperties;
    private final ConnectorDssSigningTokenProvider signingTokenProvider;

    /**
     * Constructs a new instance of {@code ConnectorRemEvidenceBuilder}.
     *
     */
    public ConnectorRemEvidenceBuilder(
            ConnectorDssDocumentSigner documentSigner,
            ConnectorEvidencesProperties evidencesProperties) {
        this.documentSigner = documentSigner;
        this.evidencesProperties = evidencesProperties;
        this.signingTokenProvider = new ConnectorDssSigningTokenProvider(
                evidencesProperties.getSignature().getKeystore(),
                evidencesProperties.getSignature().getPrivateKey()
        );
    }

    @Override
    public byte[] createSubmissionAcceptanceRejection(
            boolean isAcceptance,
            EventReasonType eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            ConnectorEvidenceMessageDetails messageDetails)
            throws ConnectorEvidenceBuilderException {
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
            log.warn("Electronic address", e);
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

        var gregorianCalendar = new GregorianCalendar();
        XMLGregorianCalendar initialSend;

        try {
            initialSend = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new ConnectorEvidenceBuilderException(e);
        }

        var deliveryConstraints = new DeliveryConstraints();
        deliveryConstraints.setInitialSend(initialSend);
        msgMetaData.setDeliveryConstraints(deliveryConstraints);

        var dispatch = new REMDispatchType();
        dispatch.setMsgMetaData(msgMetaData);

        var evidence = new SubmissionAcceptanceRejection(
                evidenceIssuerDetails,
                dispatch,
                isAcceptance
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        evidence.setUAMessageId(messageDetails.getNationalMessageId());
        evidence.setHashInformation(
                messageDetails.getHashValue(),
                messageDetails.getHashAlgorithm()
        );

        byte[] signEvidence = signEvidence(evidence, false);

        log.info("SubmissionAcceptanceRejection evidence created and signed successfully");

        return signEvidence;
    }

    @Override
    public byte[] createRelayREMMDAcceptanceRejection(
            boolean isAcceptance,
            EventReasonType eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ConnectorEvidenceBuilderException {

        var previousEvidence = parseSignedEvidence(previousEvidenceInByte);

        var evidence = new RelayREMMDAcceptanceRejection(
                evidenceIssuerDetails, previousEvidence, isAcceptance
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        byte[] signEvidence = signEvidence(evidence, true);

        log.info("RelayREMMDAcceptanceRejection evidence created and signed successfully");

        return signEvidence;
    }

    @Override
    public byte[] createRelayREMMDFailure(
            EventReasonType eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ConnectorEvidenceBuilderException {
        var previousEvidence = parseSignedEvidence(previousEvidenceInByte);

        var evidence = new RelayREMMDFailure(evidenceIssuerDetails, previousEvidence);

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        byte[] signEvidence = signEvidence(evidence, true);

        log.info("RelayREMMDFailure evidence created and signed successfully");

        return signEvidence;
    }

    @Override
    public byte[] createDeliveryNonDeliveryToRecipient(
            boolean isDelivery,
            EventReasonType eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ConnectorEvidenceBuilderException {

        var previousEvidence = parseSignedEvidence(previousEvidenceInByte);

        var evidence = new DeliveryNonDeliveryToRecipient(
                evidenceIssuerDetails, previousEvidence, isDelivery
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        byte[] signEvidence = signEvidence(evidence, true);

        log.info("DeliveryNonDeliveryToRecipient evidence created and signed successfully");

        return signEvidence;
    }

    @Override
    public byte[] createRetrievalNonRetrievalByRecipient(
            boolean isRetrieval,
            EventReasonType eventReason,
            EDeliveryDetails evidenceIssuerDetails,
            byte[] previousEvidenceInByte) throws ConnectorEvidenceBuilderException {

        var previousEvidence = parseSignedEvidence(previousEvidenceInByte);

        var evidence = new RetrievalNonRetrievalByRecipient(
                evidenceIssuerDetails,
                previousEvidence,
                isRetrieval
        );

        if (eventReason != null) {
            evidence.setEventReason(eventReason);
        }

        byte[] signEvidence = signEvidence(evidence, true);

        log.info("RetrievalNonRetrievalByRecipient evidence created and signed successfully");

        return signEvidence;
    }

    /**
     * Signs provided REM evidence with an enveloped XAdES-B signature. Optionally removes any
     * existing signature before applying a new one.
     *
     * @param evidenceToBeSigned the {@code Evidence} object to be signed.
     * @param removeOldSignature a boolean flag indicating whether the old signature (if exists)
     *                           should be removed before signing the evidence.
     *
     * @return a byte array containing the serialized and newly signed REM evidence.
     *
     * @throws ConnectorEvidenceBuilderException if an error occurs during evidence serialization or
     *                                           signing.
     */
    private byte[] signEvidence(Evidence evidenceToBeSigned, boolean removeOldSignature)
            throws ConnectorEvidenceBuilderException {
        if (removeOldSignature) {
            evidenceToBeSigned.getXSDObject().setSignature(null);
            log.debug("Old signature removed from evidence chain step");
        }

        var fo = new ByteArrayOutputStream();

        try {
            evidenceToBeSigned.serialize(fo);
        } catch (JAXBException e) {
            throw new ConnectorEvidenceBuilderException("Cannot serialize evidence", e);
        }

        byte[] bytes = fo.toByteArray();

        return sign(bytes);
    }

    protected REMEvidenceType parseSignedEvidence(byte[] bytes) {
        return RemEvidenceUnmarshaller.parseSignedEvidenceXml(bytes);
    }

    protected byte[] sign(byte[] unsignedXml) throws ConnectorEvidenceBuilderException {
        var document = new InMemoryDocument(unsignedXml);
        var signature = evidencesProperties.getSignature();
        try {
            var signedDocument = documentSigner.signWithXAdES(
                    document,
                    signature.getEncryptionAlgorithm(),
                    signature.getDigestAlgorithm(),
                    signingTokenProvider
            );
            try (var stream = signedDocument.openStream()) {
                return stream.readAllBytes();
            }
        } catch (IOException e) {
            log.error("Failed to read signed REM evidence document bytes", e);
            throw new ConnectorEvidenceBuilderException("Signing REM evidence failed", e);
        }
    }
}
