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

import eu.ecodex.connector.infrastructure.evidence.spocseu.common.SpocsConstants.Evidences;
import eu.ecodex.connector.infrastructure.evidence.spocseu.messageparts.SpocsFragments;
import eu.ecodex.connector.infrastructure.evidence.spocseu.model.EDeliveryDetails;
import jakarta.xml.bind.JAXBException;
import java.io.OutputStream;
import java.util.Date;
import java.util.UUID;
import javax.xml.datatype.DatatypeConfigurationException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.etsi.uri._02640.v2.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2.EntityDetailsType;
import org.etsi.uri._02640.v2.EntityNameType;
import org.etsi.uri._02640.v2.EventReasonType;
import org.etsi.uri._02640.v2.EventReasonsType;
import org.etsi.uri._02640.v2.EvidenceIssuerPolicyIDType;
import org.etsi.uri._02640.v2.NamePostalAddressType;
import org.etsi.uri._02640.v2.NamesPostalAddressListType;
import org.etsi.uri._02640.v2.PostalAddressType;
import org.etsi.uri._02640.v2.REMEvidenceType;
import org.w3._2000._09.xmldsig_.DigestMethodType;

/**
 * The Evidence class is an abstract class that represents evidence for a specific event. It
 * provides methods for setting various properties of the evidence such as event code, event type,
 * event reason, and message ID. It also provides methods for creating and initializing different
 * types of entity details.
 *
 * @author Lindemann
 */
@Getter
@Setter
@Slf4j
public abstract class Evidence {
    protected REMEvidenceType jaxbObj;
    protected Evidences evidenceType;
    protected EDeliveryDetails details;

    protected Evidence(REMEvidenceType jaxbObj) {
        this.jaxbObj = jaxbObj;
    }

    protected Evidence() {
    }

    protected Evidence(EDeliveryDetails details) {
        initEvidenceIssuerDetailsWithEDeliveryDetails(details);
    }

    protected void initEvidenceIssuerDetailsWithEDeliveryDetails(EDeliveryDetails details) {
        this.details = details;
        jaxbObj = new REMEvidenceType();
        jaxbObj.setVersion("2.1.1");
        var issuerPolicy = new EvidenceIssuerPolicyIDType();
        issuerPolicy.getPolicyID().add("http://uri.eu-ecodex.eu/eDeliveryPolicy");

        jaxbObj.setEvidenceIssuerPolicyID(issuerPolicy);
        jaxbObj.setEvidenceIdentifier(UUID.randomUUID().toString());

        var issuerDetails = createEntityDetailsType(
                null,
                this.details.getGatewayName(),
                this.details.getStreetAddress(),
                this.details.getLocality(),
                this.details.getPostalCode(),
                this.details.getCountry(),
                this.details.getGatewayName()
        );

        var attributedElectronicAddressType = new AttributedElectronicAddressType();
        attributedElectronicAddressType.setValue(this.details.getGatewayAddress());
        attributedElectronicAddressType.setScheme("mailto");
        issuerDetails
                .getAttributedElectronicAddressOrElectronicAddress()
                .add(attributedElectronicAddressType);
        jaxbObj.setEvidenceIssuerDetails(issuerDetails);
        try {
            jaxbObj.setEventTime(SpocsFragments
                                         .createXMLGregorianCalendar(new Date()));
        } catch (DatatypeConfigurationException e) {
            log.error("Date error: {}", e.getMessage());
        }
    }

    protected void initWithPrevious(REMEvidenceType previousJaxB) {
        // set the sender details
        jaxbObj.setSenderDetails(previousJaxB.getSenderDetails());
        // set the recipient details
        jaxbObj.setRecipientsDetails(previousJaxB.getRecipientsDetails());

        jaxbObj.setReplyToAddress(previousJaxB.getReplyToAddress());
        jaxbObj.setSenderMessageDetails(previousJaxB.getSenderMessageDetails());
    }

    public void setEventCode(String eventCode) {
        jaxbObj.setEventCode(eventCode);
    }

    public REMEvidenceType getXSDObject() {
        return jaxbObj;
    }

    /**
     * Set the event reason for the evidence.
     *
     * @param eventReasonType The event reason type to set.
     */
    public void setEventReason(EventReasonType eventReasonType) {
        var reasonsType = new EventReasonsType();
        reasonsType.getEventReason().add(eventReasonType);
        jaxbObj.setEventReasons(reasonsType);
    }

    public void setUAMessageId(String id) {
        jaxbObj.getSenderMessageDetails().setUAMessageIdentifier(id);
    }

    /**
     * Sets the hash information for the evidence.
     *
     * @param hashValue     The byte array representing the hash value.
     * @param hashAlgorithm The algorithm used to calculate the hash value.
     */
    public void setHashInformation(byte[] hashValue, String hashAlgorithm) {
        jaxbObj.getSenderMessageDetails().setDigestValue(hashValue);
        var methodType = new DigestMethodType();
        methodType.setAlgorithm(hashAlgorithm);
        jaxbObj.getSenderMessageDetails().setDigestMethod(methodType);
    }

    protected EntityDetailsType createEntityDetailsType(
            String electronicAddress, String displayName, String[] postalName) {
        return createEntityDetailsType(
                electronicAddress, displayName, null, null, null, null, postalName
        );
    }

    protected EntityDetailsType createEntityDetailsType(
            String electronicAddress,
            String displayName,
            String street,
            String locality,
            String zipcode,
            String country,
            String postalName) {
        String[] array = {postalName};

        return createEntityDetailsType(
                electronicAddress,
                displayName,
                street,
                locality,
                zipcode,
                country,
                array
        );
    }

    protected EntityDetailsType createEntityDetailsType(
            String electronicAddress,
            String displayName,
            String street,
            String locality,
            String zipcode,
            String country,
            String[] postalName) {
        // prepare
        var detailsType = new EntityDetailsType();

        // set the values
        var electronicAddressType = new AttributedElectronicAddressType();

        if (postalName != null) {
            var postAddress = new NamePostalAddressType();
            var name = new EntityNameType();

            for (String string : postalName) {
                name.getName().add(string);
            }

            postAddress.setEntityName(name);

            var postalAddressType = new PostalAddressType();

            if (street != null) {
                postalAddressType.getStreetAddress().add(street);
            }
            if (locality != null) {
                postalAddressType.setLocality(locality);
            }
            if (country != null) {
                postalAddressType.setCountryName(country);
            }
            if (zipcode != null) {
                postalAddressType.setPostalCode(zipcode);
            }

            if (street != null || locality != null || zipcode != null) {
                postAddress.setPostalAddress(postalAddressType);
            }

            var postalAddressListType = new NamesPostalAddressListType();
            postalAddressListType.getNamePostalAddress().add(postAddress);
            detailsType.setNamesPostalAddresses(postalAddressListType);
        }

        if (displayName != null) {
            electronicAddressType.setDisplayName(displayName);
        }
        if (electronicAddress != null) {
            electronicAddressType.setValue(electronicAddress);
            detailsType.getAttributedElectronicAddressOrElectronicAddress()
                       .add(electronicAddressType);
        }

        return detailsType;
    }

    /**
     * Serializes the evidence object to the specified {@link OutputStream}.
     *
     * @param out The {@code OutputStream} to which the evidence object will be serialized.
     *
     * @throws JAXBException If an error occurs during the serialization process.
     */
    public abstract void serialize(OutputStream out) throws JAXBException;
}
