/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.evidence.spocseu;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.experimental.UtilityClass;

/**
 * This class represents a Holder for the addressing, spocs and etsi JAXB Context.
 *
 * @author R. Lindemann
 */
@UtilityClass
public class JaxbContextHolder {
    private static JAXBContext spocsContext = null;

    /**
     * Internal method to get the JAXB context to marshal and unmarshal spocs objects.
     *
     * @return The created JAXB context.
     *
     * @throws JAXBException In case of errors, creating the JAXB context.
     */
    public static jakarta.xml.bind.JAXBContext getSpocsJaxBContext()
            throws JAXBException {
        if (spocsContext == null) {
            spocsContext = JAXBContext
                    .newInstance(
                            org.etsi.uri._02640.v2.ObjectFactory.class,
                            org.etsi.uri._02640.soapbinding.v1_.ObjectFactory.class
                    );
        }
        return spocsContext;
    }
}
