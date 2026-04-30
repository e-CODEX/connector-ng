/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.util;

import eu.ecodex.connector.infrastructure.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenOriginalValidationReportContainer;
import eu.ecodex.connector.infrastructure.security.token.trustok.xml.TokenJAXBObjectFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for serializing {@link ConnectorToken} instances into XML streams.
 *
 * <p>This class provides helper methods for marshalling connector tokens into UTF-8 encoded XML
 * using JAXB.
 */
@Slf4j
public class XMLStreamUtil {
    // a message to be outputted in case a JAXBException occurs
    public static final String EXCEPTION_MESSAGE =
            "A JAXBException occurred; likely caused by non-compliant data in "
            + ConnectorTokenOriginalValidationReportContainer.class.getSimpleName()
            + ". Use simple types wrapped in "
            + ConnectorTokenOriginalValidationReportContainer.SimpleTypeEntry.class.getSimpleName()
            + ".";
    private static final TokenJAXBObjectFactory FACTORY = new TokenJAXBObjectFactory();

    /**
     * Private utility constructor.
     */
    private XMLStreamUtil() {
    }

    /**
     * Marshals a {@link ConnectorToken} to a UTF-8 encoded XML stream.
     *
     * @param token the token to encode
     *
     * @return a stream containing the formatted XML
     * @throws JAXBException if the token contains non-compliant data
     */
    public static ByteArrayOutputStream encodeXMLStream(final ConnectorToken token)
            throws Exception {
        var marshaller = JAXBContextHolder.INSTANCE.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");

        final var output = new ByteArrayOutputStream();

        try {
            marshaller.marshal(FACTORY.createTrustOkToken(token), output);
        } catch (JAXBException e) {
            log.error(EXCEPTION_MESSAGE);
            throw e;
        }

        return output;
    }

    /**
     * Holder for the shared JAXBContext, initialized on first use (initialization-on-demand).
     * JAXBContext creation is expensive; this ensures it happens exactly once.
     */
    private static class JAXBContextHolder {
        private static final JAXBContext INSTANCE;

        static {
            try {
                INSTANCE = JAXBContext.newInstance(
                        TokenJAXBObjectFactory.class,
                        ConnectorTokenOriginalValidationReportContainer.SimpleTypeEntry.class
                );
            } catch (final JAXBException e) {
                log.error("Fatal: JAXBContext could not be created: [{}]", e.getMessage());
                throw new ExceptionInInitializerError(e);
            }
        }
    }
}
