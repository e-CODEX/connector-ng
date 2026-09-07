/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.outbound.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.tuple;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.infrastructure.outbound.adapter.exception.ConnectorProcessingModeParsingException;
import eu.ecodex.connector.infrastructure.outbound.adapter.pmode.ConnectorDomProcessingModeParser;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConnectorDomProcessingModeParserTest {
    private static final String HOME_PARTY = "blue_gw";
    private static final String REMOTE_PARTY = "red_gw";

    private static final String INITIATOR_ROLE =
        "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator";
    private static final String RESPONDER_ROLE =
        "http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder";

    private static final String URN_TYPE =
        "urn:oasis:names:tc:ebcore:partyid-type:unregistered";
    private static final String ECODEX_TYPE =
        "urn:oasis:names:tc:ebcore:partyid-type:ecodex";
    private static final String ROLES = """
        <role name="defaultInitiatorRole"
              value="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator"/>
        <role name="defaultResponderRole"
              value="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/responder"/>
        """;
    private static final String PARTIES = """
        <party name="blue_gw">
            <identifier partyId="domibus-blue" partyIdType="partyTypeUrn"/>
        </party>
        <party name="red_gw">
            <identifier partyId="domibus-red" partyIdType="partyTypeEcodex"/>
        </party>
        """;
    private static final String SERVICES = """
        <service name="testService1" value="EPO" type="urn:e-codex:services:"/>
        <service name="testService2" value="EPO-Reply" type="urn:e-codex:services:"/>
        """;
    private static final String ACTIONS = """
        <action name="tc1Action" value="Form_A"/>
        <action name="tc2Action" value="Form_B"/>
        """;
    private static final String PROCESSES = """
        <process name="eioProcess" mep="oneway" binding="push"
                 initiatorRole="defaultInitiatorRole" responderRole="defaultResponderRole">
            <initiatorParties><initiatorParty name="blue_gw"/></initiatorParties>
            <responderParties><responderParty name="red_gw"/></responderParties>
        </process>
        """;
    private final ConnectorDomProcessingModeParser parser = new ConnectorDomProcessingModeParser();

    private static byte[] xml(String content) {
        return content.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Builds a structurally valid PMode. The placeholders let a single negative test override
     * exactly one section without repeating the whole document.
     */
    private static String pmode(
        String rootParty, String roles, String parties, String processes,
        String services, String actions) {
        return """
            <?xml version="1.0" encoding="UTF-8"?>
            <configuration party="%s">
                <businessProcesses>
                    <roles>%s</roles>
                    <partyIdTypes>
                        <partyIdType name="partyTypeUrn" value="%s"/>
                        <partyIdType name="partyTypeEcodex" value="%s"/>
                    </partyIdTypes>
                    <parties>%s</parties>
                    <services>%s</services>
                    <actions>%s</actions>
                    <processes>%s</processes>
                </businessProcesses>
            </configuration>
            """.formatted(
            rootParty, roles, URN_TYPE, ECODEX_TYPE,
            parties, services, actions, processes
        );
    }

    private static String validPmode() {
        return pmode(HOME_PARTY, ROLES, PARTIES, PROCESSES, SERVICES, ACTIONS);
    }

    @Nested
    @DisplayName("when the definition is valid")
    class ValidDefinition {

        @Test
        void should_expose_the_home_party_declared_on_the_root_element() {
            assertThat(parser.parse(xml(validPmode())).homePartyName()).isEqualTo(HOME_PARTY);
        }

        @Test
        void should_create_one_entry_per_party_and_role_type() {
            var parsed = parser.parse(xml(validPmode()));

            assertThat(parsed.parties())
                .hasSize(2 * ConnectorPartyRoleType.values().length)
                .extracting(ConnectorParty::name, ConnectorParty::roleType)
                .containsExactlyInAnyOrder(
                    tuple(HOME_PARTY, ConnectorPartyRoleType.INITIATOR),
                    tuple(HOME_PARTY, ConnectorPartyRoleType.RESPONDER),
                    tuple(REMOTE_PARTY, ConnectorPartyRoleType.INITIATOR),
                    tuple(REMOTE_PARTY, ConnectorPartyRoleType.RESPONDER)
                );
        }

        @Test
        void should_map_the_role_value_from_the_referenced_process_roles() {
            var parsed = parser.parse(xml(validPmode()));

            assertThat(parsed.parties())
                .filteredOn(party -> party.roleType() == ConnectorPartyRoleType.INITIATOR)
                .extracting(ConnectorParty::role)
                .containsOnly(INITIATOR_ROLE);

            assertThat(parsed.parties())
                .filteredOn(party -> party.roleType() == ConnectorPartyRoleType.RESPONDER)
                .extracting(ConnectorParty::role)
                .containsOnly(RESPONDER_ROLE);
        }

        @Test
        void should_resolve_the_identifier_type_from_the_party_id_type_declarations() {
            var parsed = parser.parse(xml(validPmode()));

            assertThat(parsed.parties())
                .filteredOn(party -> HOME_PARTY.equals(party.name()))
                .allSatisfy(party -> {
                    assertThat(party.identifier()).isEqualTo("domibus-blue");
                    assertThat(party.identifierType()).isEqualTo(URN_TYPE);
                });

            assertThat(parsed.parties())
                .filteredOn(party -> REMOTE_PARTY.equals(party.name()))
                .allSatisfy(party -> {
                    assertThat(party.identifier()).isEqualTo("domibus-red");
                    assertThat(party.identifierType()).isEqualTo(ECODEX_TYPE);
                });
        }

        @Test
        void should_flag_only_the_home_party_as_home() {
            var parsed = parser.parse(xml(validPmode()));

            assertThat(parsed.parties())
                .filteredOn(ConnectorParty::isHome)
                .isNotEmpty()
                .allSatisfy(party -> assertThat(party.name()).isEqualTo(HOME_PARTY));
        }

        @Test
        void should_read_services_from_the_value_attribute_and_keep_the_type() {
            assertThat(parser.parse(xml(validPmode())).services())
                .extracting("name", "type")
                .containsExactlyInAnyOrder(
                    tuple("EPO", "urn:e-codex:services:"),
                    tuple("EPO-Reply", "urn:e-codex:services:")
                );
        }

        @Test
        void should_read_actions_from_the_value_attribute() {
            assertThat(parser.parse(xml(validPmode())).actions())
                .extracting("name")
                .containsExactlyInAnyOrder("Form_A", "Form_B");
        }
    }

    @Nested
    @DisplayName("when the role mapping is invalid")
    class InvalidRoleMapping {

        @Test
        void should_reject_a_process_referencing_an_undeclared_role() {
            var processes = """
                <process name="eioProcess"
                         initiatorRole="missingRole" responderRole="defaultResponderRole">
                    <initiatorParties><initiatorParty name="blue_gw"/></initiatorParties>
                    <responderParties><responderParty name="red_gw"/></responderParties>
                </process>
                """;

            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode(HOME_PARTY, ROLES, PARTIES, processes, SERVICES, ACTIONS))))
                .withMessageContaining("missingRole");
        }

        @Test
        void should_reject_a_process_without_an_initiator_role_attribute() {
            var processes = """
                <process name="eioProcess" responderRole="defaultResponderRole">
                    <initiatorParties><initiatorParty name="blue_gw"/></initiatorParties>
                    <responderParties><responderParty name="red_gw"/></responderParties>
                </process>
                """;

            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode(HOME_PARTY, ROLES, PARTIES, processes, SERVICES, ACTIONS))))
                .withMessageContaining("initiatorRole");
        }

        @Test
        void should_reject_a_definition_without_any_process() {
            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode(HOME_PARTY, ROLES, PARTIES, "", SERVICES, ACTIONS))))
                .withMessageContaining("process");
        }

        @Test
        void should_reject_processes_with_differing_role_values() {
            var extraRole = ROLES + """
                <role name="altInitiatorRole" value="urn:role:alt-initiator"/>
                """;
            var processes = PROCESSES + """
                <process name="secondProcess"
                         initiatorRole="altInitiatorRole" responderRole="defaultResponderRole">
                    <initiatorParties><initiatorParty name="blue_gw"/></initiatorParties>
                    <responderParties><responderParty name="red_gw"/></responderParties>
                </process>
                """;

            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode(HOME_PARTY, extraRole, PARTIES, processes, SERVICES, ACTIONS))));
        }

        @Test
        void should_reject_a_role_without_a_value() {
            var roles = """
                <role name="defaultInitiatorRole"/>
                <role name="defaultResponderRole" value="urn:role:responder"/>
                """;

            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode(HOME_PARTY, roles, PARTIES, PROCESSES, SERVICES, ACTIONS))))
                .withMessageContaining("value");
        }

        @Test
        void should_reject_duplicate_roles_with_conflicting_values() {
            var roles = ROLES + """
                <role name="defaultInitiatorRole" value="urn:role:conflicting"/>
                """;

            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode(HOME_PARTY, roles, PARTIES, PROCESSES, SERVICES, ACTIONS))))
                .withMessageContaining("Duplicate");
        }

        @Test
        void should_accept_duplicate_roles_with_identical_values() {
            var roles = ROLES + """
                <role name="defaultInitiatorRole"
                      value="http://docs.oasis-open.org/ebxml-msg/ebms/v3.0/ns/core/200704/initiator"/>
                """;

            assertThatNoException().isThrownBy(() -> parser.parse(
                xml(pmode(HOME_PARTY, roles, PARTIES, PROCESSES, SERVICES, ACTIONS))));
        }
    }

    @Nested
    @DisplayName("when the definition is structurally invalid")
    class InvalidStructure {

        @Test
        void should_reject_a_root_element_without_a_party_attribute() {
            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode("", ROLES, PARTIES, PROCESSES, SERVICES, ACTIONS))))
                .withMessageContaining("party");
        }

        @Test
        void should_reject_a_home_party_absent_from_the_parties_list() {
            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode("unknown_gw", ROLES, PARTIES, PROCESSES, SERVICES, ACTIONS))))
                .withMessageContaining("unknown_gw");
        }

        @Test
        void should_reject_a_party_without_an_identifier_element() {
            var parties = "<party name=\"blue_gw\"/>";

            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode(HOME_PARTY, ROLES, parties, PROCESSES, SERVICES, ACTIONS))))
                .withMessageContaining("blue_gw");
        }

        @Test
        void should_reject_an_identifier_referencing_an_undeclared_party_id_type() {
            var parties = """
                <party name="blue_gw">
                    <identifier partyId="id" partyIdType="missing"/>
                </party>
                """;

            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode(HOME_PARTY, ROLES, parties, PROCESSES, SERVICES, ACTIONS))))
                .withMessageContaining("missing");
        }

        @Test
        void should_reject_a_service_without_a_value() {
            var services = "<service name=\"testService1\"/>";

            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(
                    xml(pmode(HOME_PARTY, ROLES, PARTIES, PROCESSES, services, ACTIONS))))
                .withMessageContaining("value");
        }
    }

    @Nested
    @DisplayName("when the definition cannot be read")
    class UnreadableDefinition {

        static Stream<Arguments> emptyContents() {
            return Stream.of(Arguments.of((Object) null), Arguments.of((Object) new byte[0]));
        }

        @ParameterizedTest
        @MethodSource("emptyContents")
        void should_reject_an_empty_definition(byte[] content) {
            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(content))
                .withMessageContaining("empty");
        }

        @Test
        void should_reject_a_malformed_document() {
            assertThatExceptionOfType(ConnectorProcessingModeParsingException.class)
                .isThrownBy(() -> parser.parse(xml("<configuration party=\"blue_gw\">")))
                .withMessageContaining("well-formed");
        }
    }
}
