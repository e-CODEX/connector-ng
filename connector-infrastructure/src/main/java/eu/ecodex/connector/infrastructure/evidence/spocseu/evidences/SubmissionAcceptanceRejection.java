/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/* ---------------------------------------------------------------------------
             COMPETITIVENESS AND INNOVATION FRAMEWORK PROGRAMME
                   ICT Policy Support Programme (ICT PSP)
           Preparing the implementation of the Services Directive
                   ICT PSP call identifier: ICT PSP-2008-2
             ICT PSP main Theme identifier: CIP-ICT-PSP.2008.1.1
                           Project acronym: SPOCS
   Project full title: Simple Procedures Online for Cross-border Services
                         Grant agreement no.: 238935
                               www.eu-spocs.eu
------------------------------------------------------------------------------
    WP3 Interoperable delivery, eSafe, secure and interoperable exchanges
                       and acknowledgement of receipt
------------------------------------------------------------------------------
        Open module implementing the eSafe document exchange protocol
------------------------------------------------------------------------------

$URL: svn:https://svnext.bos-bremen.de/SPOCS/AllWpImplementation/EDelivery-Gateway
$Date: 2010-05-13 18:55:57 +0200 (Do, 14. Okt 2010) $
$Revision: 86 $

See SPOCS_WP3_LICENSE_URL for license information
--------------------------------------------------------------------------- */

package eu.ecodex.connector.infrastructure.evidence.spocseu.evidences;

import eu.ecodex.connector.infrastructure.evidence.spocseu.JaxbContextHolder;
import eu.ecodex.connector.infrastructure.evidence.spocseu.common.SpocsConstants.Evidences;
import eu.ecodex.connector.infrastructure.evidence.spocseu.messageparts.SpocsFragments;
import eu.ecodex.connector.infrastructure.evidence.spocseu.model.EDeliveryDetails;
import jakarta.xml.bind.JAXBException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.etsi.uri._02640.soapbinding.v1_.Destinations;
import org.etsi.uri._02640.soapbinding.v1_.MsgMetaData;
import org.etsi.uri._02640.soapbinding.v1_.Originators;
import org.etsi.uri._02640.soapbinding.v1_.REMDispatchType;
import org.etsi.uri._02640.v2.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2.EntityDetailsListType;
import org.etsi.uri._02640.v2.EntityDetailsType;
import org.etsi.uri._02640.v2.MessageDetailsType;
import org.etsi.uri._02640.v2.ObjectFactory;
import org.etsi.uri._02640.v2.REMEvidenceType;

/**
 * The SubmissionAcceptanceRejection class represents an evidence object that encapsulates
 * information about the acceptance or rejection of a submission.
 */
@Slf4j
public class SubmissionAcceptanceRejection extends Evidence {
    public static final String LOG_SUCCESS =
            "Create SubmissionAcceptanceRejection in success case.";
    public static final String LOG_FAULT =
            "Create SubmissionAcceptanceRejection in fault case.";

    protected SubmissionAcceptanceRejection(EDeliveryDetails details) {
        super(details);
    }

    protected SubmissionAcceptanceRejection(EDeliveryDetails details, boolean isAcceptance) {
        super(details);
        applyEventCode(isAcceptance);
    }

    /**
     * This constructor creates a SubmissionAcceptanceRejection object based on a given
     * DispatchMessage. The purpose is to create the SubmissionAcceptanceRejection which will be
     * attached to the Dispatch Message which will be sent out.
     *
     * @param details      Configuration object to set some properties
     * @param dispatch     DispatchMessage as input information for this object. This is a Dispatch
     *                     that should be sent out!
     * @param isAcceptance If true is given, a success event will be created.
     */
    public SubmissionAcceptanceRejection(
            EDeliveryDetails details,
            REMDispatchType dispatch,
            boolean isAcceptance) {
        super(details);
        evidenceType = Evidences.SUBMISSION_ACCEPTANCE_REJECTION;
        applyEventCode(isAcceptance);
        populateFromDispatch(dispatch);
    }

    /**
     * Constructor for creating a {@code SubmissionAcceptanceRejection} instance using the specified
     * evidence type.
     *
     * @param evidenceType The type of Remote Evidence Message (REMEvidenceType) that forms the
     *                     basis of this SubmissionAcceptanceRejection object.
     */
    public SubmissionAcceptanceRejection(REMEvidenceType evidenceType) {
        super(evidenceType);
    }

    @Override
    public void serialize(OutputStream out) throws JAXBException {
        JaxbContextHolder
                .getSpocsJaxBContext()
                .createMarshaller()
                .marshal(new ObjectFactory().createSubmissionAcceptanceRejection(jaxbObj), out);
    }

    private void applyEventCode(boolean isAcceptance) {
        if (isAcceptance) {
            log.debug(LOG_SUCCESS);
            setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION.getSuccessEventCode());
        } else {
            log.debug(LOG_FAULT);
            log.debug(
                    "FaultCode: {}", Evidences.SUBMISSION_ACCEPTANCE_REJECTION.getFaultEventCode()
            );
            setEventCode(Evidences.SUBMISSION_ACCEPTANCE_REJECTION.getFaultEventCode());
        }
    }

    private void populateFromDispatch(REMDispatchType dispatch) {
        var msgMeta = dispatch.getMsgMetaData();
        Objects.requireNonNull(msgMeta, "dispatch MsgMetaData must not be null");

        var originators = Objects.requireNonNull(
                msgMeta.getOriginators(), "dispatch originators must not be null");

        jaxbObj.setSenderDetails(extractSenderDetails(originators.getFrom()));
        jaxbObj.setSenderMessageDetails(buildMessageDetails(msgMeta));
        jaxbObj.setRecipientsDetails(extractRecipientDetails(msgMeta.getDestinations()));
        jaxbObj.setSubmissionTime(msgMeta.getDeliveryConstraints().getInitialSend());
        jaxbObj.setReplyToAddress(resolveReplyToAddress(originators));
        jaxbObj.setId(UUID.randomUUID().toString());
    }

    private EntityDetailsType extractSenderDetails(EntityDetailsType from) {
        var senderAddress = SpocsFragments.getAttributedElectronicAddress(from);
        var senderEAddress = senderAddress.getValue();
        var senderName = senderAddress.getDisplayName();
        var senderPostalNames = extractPostalNames(from);
        return createEntityDetailsType(senderEAddress, senderName, senderPostalNames);
    }

    private MessageDetailsType buildMessageDetails(MsgMetaData msgMeta) {
        var messageDetailsType = new MessageDetailsType();
        messageDetailsType.setMessageIdentifierByREMMD(
                msgMeta.getMsgIdentification().getMessageID());
        return messageDetailsType;
    }

    private EntityDetailsListType extractRecipientDetails(Destinations destinations) {
        var recipient = destinations.getRecipient();
        var recipientAddr = SpocsFragments.getAttributedElectronicAddress(recipient);
        var recipientNames = extractPostalNames(recipient);

        var detailList = new EntityDetailsListType();
        detailList.getEntityDetails().add(
                createEntityDetailsType(
                        recipientAddr.getValue(),
                        recipientAddr.getDisplayName(),
                        recipientNames
                ));
        return detailList;
    }

    private AttributedElectronicAddressType resolveReplyToAddress(Originators originators) {
        var replyTo = originators.getReplyTo();
        if (replyTo != null
                && replyTo.getAttributedElectronicAddressOrElectronicAddress() != null) {
            return SpocsFragments.getFirstElectronicAddressWithURI(replyTo);
        }
        return SpocsFragments.getAttributedElectronicAddress(originators.getFrom());
    }

    private String[] extractPostalNames(EntityDetailsType entity) {
        var namesPostal = entity.getNamesPostalAddresses();

        if (namesPostal == null) {
            return new String[0];
        }

        var firstAddress = namesPostal.getNamePostalAddress().getFirst();

        if (firstAddress == null) {
            return new String[0];
        }

        return firstAddress.getEntityName().getName().toArray(new String[0]);
    }
}
