/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.security.model.token;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents authentication-related data associated with a connector token.
 */
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "AuthenticationDataType",
        propOrder = {"identityProvider", "usernameSynonym", "timeOfAuthentication"}
)
public class ConnectorTokenAuthenticationData {
    @XmlElement(name = "IdentityProvider", required = true)
    private String identityProvider;
    @XmlElement(name = "UsernameSynonym", required = true)
    private String usernameSynonym;
    @XmlSchemaType(name = "dateTime")
    @XmlElement(name = "TimeOfAuthentication", required = true)
    private XMLGregorianCalendar timeOfAuthentication;

    @Override
    public String toString() {
        return String.format(
                "{identityProvider=%s, usernameSynonym=%s, timeOfAuthentication=%s}",
                identityProvider, usernameSynonym, timeOfAuthentication
        );
    }
}
