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

import eu.ecodex.connector.infrastructure.security.exception.ConnectorTokenException;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorToken;
import eu.ecodex.connector.infrastructure.security.model.token.ConnectorTokenOriginalValidationReportContainer;
import eu.ecodex.connector.infrastructure.security.token.trustok.xml.TokenJAXBObjectFactory;
import eu.europa.esig.dss.model.DSSDocument;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import javax.xml.parsers.DocumentBuilderFactory;
import lombok.extern.slf4j.Slf4j;
import org.xml.sax.SAXParseException;

/**
 * Utility class for serializing {@link ConnectorToken} instances into XML streams.
 *
 * <p>This class provides helper methods for marshalling connector tokens into UTF-8 encoded XML
 * using JAXB.
 */
@Slf4j
public class XMLStreamUtil {
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
            throws JAXBException {
        var marshaller = JAXBContextHolder.INSTANCE.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
        var output = new ByteArrayOutputStream();

        marshaller.marshal(FACTORY.createTrustOkToken(token), output);
        return output;
    }

    /**
     * Unmarshals a {@link ConnectorToken} from an XML input stream.
     *
     * @param xmlInputStream the XML stream to decode
     *
     * @return the decoded token
     * @throws JAXBException           if the stream cannot be unmarshalled
     * @throws ConnectorTokenException if the root element is not a ConnectorToken
     */
    public static ConnectorToken decodeXMLStream(InputStream xmlInputStream) throws JAXBException {
        var unmarshaller = JAXBContextHolder.INSTANCE.createUnmarshaller();

        var result = unmarshaller.unmarshal(xmlInputStream);

        if (!(result instanceof JAXBElement<?> element)
            || !(element.getValue() instanceof ConnectorToken token)) {
            throw new ConnectorTokenException(
                    "XML stream did not unmarshal to a ConnectorToken; got: "
                    + (result == null ? "null" : result.getClass().getSimpleName()));
        }

        return token;
    }

    /**
     * Returns true if {@code document} contains well-formed XML.
     *
     * <p>Note: the document is fully parsed to verify well-formedness.
     * For very large documents consider a short-circuit SAX approach instead.
     *
     */
    public static boolean isXmlFile(DSSDocument document) {
        try (var inputStream = document.openStream()) {
            var factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            var parser = factory.newDocumentBuilder();
            parser.parse(inputStream);

            return true;
        } catch (SAXParseException e) {
            return false;
        } catch (Exception e) {
            log.warn(
                    "Unexpected error while checking XML validity of '{}': {}",
                    document.getName(), e.getMessage(), e
            );
            return false;
        }
    }

    /**
     * Holder for the shared JAXBContext, initialized on first use (initialization-on-demand).
     * JAXBContext creation is expensive; this ensures it happens exactly once.
     */
    private static class JAXBContextHolder {
        private static final JAXBContext INSTANCE = createContext();

        private static JAXBContext createContext() {
            try {
                return JAXBContext.newInstance(
                        TokenJAXBObjectFactory.class,
                        ConnectorTokenOriginalValidationReportContainer.SimpleTypeEntry.class
                );
            } catch (JAXBException e) {
                // Use stderr — logging framework may not be ready during static init
                log.error("Fatal: JAXBContext could not be created: {}", e.getMessage());
                throw new ExceptionInInitializerError(e);
            }
        }
    }
}
