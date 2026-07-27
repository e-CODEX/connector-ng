/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import eu.ecodex.connector.domain.model.pmode.ConnectorProcessingMode;
import eu.ecodex.connector.domain.model.security.ConnectorTruststore;
import eu.ecodex.connector.domain.model.security.KeystoreType;
import java.util.Set;

@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod", "checkstyle:LineLength"})
public class ProcessingModeTestFixtures {
    static String pmodeString = """
        <db:configuration xmlns:db="http://domibus.eu/configuration" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                          xsi:schemaLocation="http://domibus.eu/configuration file:/C:/development/git-repos/domibus/Domibus-MSH/domibus-configuration.xsd"
                          party="service_blue_ecodex">
            <mpcs>
                <mpc name="defaultMpc" qualifiedName="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/defaultMPC"
                     enabled="true" default="true" retention_downloaded="0" retention_undownloaded="60"/>
            </mpcs>
            <businessProcesses>
                <parties>
                    <partyIdTypes>
                        <partyIdType name="ecodex" value="urn:oasis:names:tc:ebcore:partyid-type:ecodex"/>
                    </partyIdTypes>
                    <party name="service_red_ecodex" endpoint="http://red-gateway:8080/domibus/services/msh"
                           allowChunking="false">
                        <identifier partyId="RE" partyIdType="ecodex"/>
                    </party>
                    <party name="service_blue_ecodex" endpoint="http://blue-gateway:8080/domibus/services/msh"
                           allowChunking="false">
                        <identifier partyId="BL" partyIdType="ecodex"/>
                    </party>
                </parties>
                <services>
                    <service name="MLAService" value="BC-009MLA" type="urn:e-codex:services:"/>
                    <service name="FPService" value="FP" type="urn:e-codex:services:"/>
                    <service name="EPOService" value="EPO" type="urn:e-codex:services:"/>
                    <service name="GWTESTService" value="GW-TEST" type="urn:e-codex:services:"/>
                    <service name="ConTESTService" value="Connector-TEST" type="urn:e-codex:services:"/>
                    <service name="SCService" value="SmallClaims" type="urn:e-codex:services:"/>
                    <service name="testService" value="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/service"
                             type="test"/>
                </services>
                <actions>
                    <action name="eCODEXEvidenceSubmissionAcceptanceRejection" value="SubmissionAcceptanceRejection"/>
                    <action name="eCODEXEvidenceRelayREMMDAcceptanceRejection" value="RelayREMMDAcceptanceRejection"/>
                    <action name="eCODEXEvidenceDeliveryNonDeliveryToRecipient" value="DeliveryNonDeliveryToRecipient"/>
                    <action name="eCODEXEvidenceRetrievalNonRetrievalToRecipient" value="RetrievalNonRetrievalToRecipient"/>
                    <action name="MLAActionIssueAssistance" value="BD-009001-IssueAssistance"/>
                    <action name="MLAActionAdditionalInformation" value="BD-009003-AdditionalInformation"/>
                    <action name="MLAActionLegalConformance" value="BD-009004-LegalConformance"/>
                    <action name="MLAActionStatus" value="BD-009006-Status"/>
                    <action name="MLAActionWithdrawalLegalAssistance" value="BD-009007-WithdrawalLegalAssistance"/>
                    <action name="MLAActionOutcome" value="BD-009008-Outcome"/>
                    <action name="MLAActionRequestForAdditionalInformation" value="BD-009009-RequestForAdditionalInformation"/>
                    <action name="MLAActionRegisterAssistance" value="BD-009999-RegisterAssistance"/>
                    <action name="MLAActionChangeAssistance" value="BD-009001-ChangeAssistance"/>
                    <action name="MLAActionTakeMeasure" value="BD-009001-TakeMeasure"/>
                    <action name="MLAActionVerifyLegalBasis" value="BD-009004-VerifyLegalBasis"/>
                    <action name="FPActionCertificate" value="FP_Certificate"/>
                    <action name="FPActionReceipt" value="FP_Receipt "/>
                    <action name="ActionFormA" value="Form_A"/>
                    <action name="ActionFormB" value="Form_B"/>
                    <action name="ActionFormC" value="Form_C"/>
                    <action name="ActionFormD" value="Form_D"/>
                    <action name="ActionFormE" value="Form_E"/>
                    <action name="ActionFormF" value="Form_F"/>
                    <action name="ActionFormG" value="Form_G"/>
                    <action name="ActionFreeFormLetterIn" value="FreeFormLetterIn"/>
                    <action name="ActionFreeFormLetterOut" value="FreeFormLetterOut"/>
                    <action name="GWTESTActionTestForm" value="Test_Form"/>
                    <action name="ConTESTActionTestForm" value="ConTest_Form"/>
                    <action name="ActionFreeFormLetter" value="FreeFormLetter"/>
                    <action name="ActionWithdraw" value="Withdraw"/>
                    <action name="testAction" value="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/test"/>
                </actions>
            </businessProcesses>
        </db:configuration>
        """;

    public static ConnectorProcessingMode createWithNoBusinessDomain() {
        return ConnectorProcessingMode
            .builder()
            .uuid("7b79a71b-ce4c-4e18-9f82-7fa072a29e7e")
            .description("test processing mode")
            .services(Set.of(ServiceTestFixtures.createService()))
            .actions(Set.of(ActionTestFixtures.createAction()))
            .parties(Set.of(PartyTestFixtures.createFromParty()))
            .content(pmodeString)
            .filename("pmode.xml")
            .truststore(
                ConnectorTruststore.builder()
                                   .filename("truststore.jks")
                                   .password("test")
                                   .type(KeystoreType.JKS)
                                   .content(new byte[1])
                                   .build()
            )
            .build();
    }

    public static ConnectorProcessingMode createWithNoBusinessDomainAndNoHomeParty() {
        return createWithNoBusinessDomain()
            .toBuilder()
            .content(pmodeString.replaceFirst("party=\"service_blue_ecodex\"", ""))
            .build();
    }

    public static ConnectorProcessingMode createWithBusinessDomain() {
        return createWithNoBusinessDomain()
            .toBuilder()
            .businessDomain(BusinessDomainTestFixtures.createDefaultBusinessDomain())
            .truststore(
                ConnectorTruststore.builder()
                                   .filename("truststore.jks")
                                   .password("test")
                                   .type(KeystoreType.JKS)
                                   .content(new byte[1])
                                   .build()
            )
            .build();
    }
}
