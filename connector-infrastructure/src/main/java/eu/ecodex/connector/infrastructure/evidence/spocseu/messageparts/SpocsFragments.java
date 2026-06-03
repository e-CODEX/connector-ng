/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence.spocseu.messageparts;

import java.net.MalformedURLException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.etsi.uri._02640.v2.AttributedElectronicAddressType;
import org.etsi.uri._02640.v2.EntityDetailsType;

/**
 * This class contains several Helper Methods of the SPOCS Context.
 */
@Slf4j
@UtilityClass
public class SpocsFragments {
    /**
     * Creates an AttributedElectronicAddressType object with the given address and display name.
     *
     * @param address     The email address.
     * @param displayName The display name for the email address.
     *
     * @return The created AttributedElectronicAddressType object.
     *
     * @throws MalformedURLException If the address is not a valid email address.
     */
    public static AttributedElectronicAddressType createElectronicAddress(
            String address, String displayName) throws MalformedURLException {
        var electronicAddress = new AttributedElectronicAddressType();
        if (displayName != null) {
            electronicAddress.setDisplayName(displayName);
        }
        electronicAddress.setValue(address);
        electronicAddress.setScheme("mailto");
        // No addressValidation for e-CODEX because there is no format defined

        return electronicAddress;
    }

    /**
     * This Method gets the first Electronic Address where the URI is set out of a *
     * {@link EntityDetailsType}.
     *
     * @param jaxbObj The {@link EntityDetailsType} object from which to retrieve the electronic
     *                address.
     *
     * @return The first instance of {@link AttributedElectronicAddressType} with a URI value found
     *         in the {@link EntityDetailsType}, or null if no such instance is found.
     */
    public static AttributedElectronicAddressType getFirstElectronicAddressWithURI(
            EntityDetailsType jaxbObj) {

        AttributedElectronicAddressType electronicAddressType =
                (AttributedElectronicAddressType) jaxbObj
                        .getAttributedElectronicAddressOrElectronicAddress().getFirst();
        if (electronicAddressType.getValue() != null) {
            return electronicAddressType;
        } else {
            log.info("Electronic Address has no value!");
            return null;
        }
    }

    /**
     * Creates an XMLGregorianCalendar with the given date object.
     *
     * @param date The date/time that is to be included in the XMLGregorianCalendar.
     *
     * @return The created XMLGregorianCalendar object.
     *
     * @throws DatatypeConfigurationException If there are converting errors with the date objects.
     */
    public static XMLGregorianCalendar createXMLGregorianCalendar(Date date)
            throws DatatypeConfigurationException {
        var cal = new GregorianCalendar();
        cal.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    /**
     * Retrieves the first AttributedElectronicAddressType object from the given EntityDetailsType.
     *
     * @param details The EntityDetailsType object containing electronic addresses.
     *
     * @return The first instance of AttributedElectronicAddressType found in the EntityDetailsType,
     *         or null if no such instance is found.
     */
    public static AttributedElectronicAddressType getAttributedElectronicAddress(
            EntityDetailsType details) {
        List<Object> electronicAddress = details
                .getAttributedElectronicAddressOrElectronicAddress();
        for (Object object : electronicAddress) {
            if (object instanceof AttributedElectronicAddressType electronicAddressType) {
                return electronicAddressType;
            }
        }
        return null;
    }
}
