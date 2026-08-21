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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

import eu.ecodex.connector.ProcessingModeTestFixtures;
import eu.ecodex.connector.domain.model.pmode.ConnectorParty;
import eu.ecodex.connector.domain.model.pmode.ConnectorPartyRoleType;
import eu.ecodex.connector.domain.spi.ConnectorProcessingModeParser;
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
public class ConnectorDomProcessingModeParserTest {
    private static final String HOME_PARTY = "service_blue_ecodex";
    private static final String REMOTE_PARTY = "service_red_ecodex";
    private static final String ECODEX_TYPE =
        "urn:oasis:names:tc:ebcore:partyid-type:ecodex";

    private static final String VALID_PMODE = ProcessingModeTestFixtures.createWithBusinessDomain()
                                                                        .content();

    private final ConnectorProcessingModeParser parser = new ConnectorDomProcessingModeParser();

    private static byte[] xml(String content) {
        return content.getBytes(StandardCharsets.UTF_8);
    }

    @Nested
    @DisplayName("when the definition is valid")
    class ValidDefinition {
        @Test
        void should_expose_the_home_party_declared_on_the_root_element() {
            var parsed = parser.parse(xml(VALID_PMODE));

            assertThat(parsed.homePartyName()).isEqualTo(HOME_PARTY);
        }

        @Test
        void should_create_one_entry_per_party_and_role_type() {
            var parsed = parser.parse(xml(VALID_PMODE));

            // 2 parties x INITIATOR/RESPONDER
            assertThat(parsed.parties())
                .hasSize(2 * ConnectorPartyRoleType.values().length);

            assertThat(parsed.parties())
                .extracting(ConnectorParty::name, ConnectorParty::roleType)
                .containsExactlyInAnyOrder(
                    tuple(
                        HOME_PARTY,
                        ConnectorPartyRoleType.INITIATOR
                    ),
                    tuple(
                        HOME_PARTY,
                        ConnectorPartyRoleType.RESPONDER
                    ),
                    tuple(
                        REMOTE_PARTY,
                        ConnectorPartyRoleType.INITIATOR
                    ),
                    tuple(
                        REMOTE_PARTY,
                        ConnectorPartyRoleType.RESPONDER
                    )
                );
        }

        @Test
        void should_resolve_the_identifier_type_from_the_party_id_type_declarations() {
            var parsed = parser.parse(xml(VALID_PMODE));

            assertThat(parsed.parties())
                .filteredOn(party -> HOME_PARTY.equals(party.name()))
                .allSatisfy(party -> {
                    assertThat(party.identifier()).isEqualTo("BL");
                    assertThat(party.identifierType()).isEqualTo(ECODEX_TYPE);
                });

            assertThat(parsed.parties())
                .filteredOn(party -> REMOTE_PARTY.equals(party.name()))
                .allSatisfy(party -> {
                    assertThat(party.identifier()).isEqualTo("RE");
                    assertThat(party.identifierType()).isEqualTo(ECODEX_TYPE);
                });
        }

        @Test
        void should_flag_only_the_home_party_as_home() {
            var parsed = parser.parse(xml(VALID_PMODE));

            assertThat(parsed.parties())
                .filteredOn(ConnectorParty::isHome)
                .isNotEmpty()
                .allSatisfy(party -> assertThat(party.name()).isEqualTo(HOME_PARTY));

            assertThat(parsed.parties())
                .filteredOn(party -> !party.isHome())
                .allSatisfy(party -> assertThat(party.name()).isEqualTo(REMOTE_PARTY));
        }

        @Test
        void should_assign_the_gateway_role_to_every_party() {
            var parsed = parser.parse(xml(VALID_PMODE));

            assertThat(parsed.parties())
                .extracting(ConnectorParty::role)
                .containsOnly("GW");
        }

        @Test
        void should_read_services_from_the_value_attribute_and_keep_the_type() {
            var parsed = parser.parse(xml(VALID_PMODE));

            assertThat(parsed.services())
                .extracting("name", "type")
                .contains(
                    tuple("EPO", "urn:e-codex:services:"),
                    tuple("SmallClaims", "urn:e-codex:services:")
                );
        }

        @Test
        void should_read_actions_from_the_value_attribute() {
            var parsed = parser.parse(xml(VALID_PMODE));

            assertThat(parsed.actions())
                .extracting("name")
                .contains("Form_D", "Form_A", "Form_B", "Form_C");
        }

        @Test
        void should_return_a_service_without_type_when_the_attribute_is_absent() {
            var parsed = parser.parse(xml("""
                                              <configuration party="blue_gw">
                                                  <partyIdType name="t" value="urn:type"/>
                                                  <party name="blue_gw"><identifier partyId="id" partyIdType="t"/></party>
                                                  <service value="EPO"/>
                                                  <action value="Form_A"/>
                                              </configuration>
                                              """));

            assertThat(parsed.services())
                .singleElement()
                .satisfies(service -> assertThat(service.type()).isNull());
        }

        @Test
        void should_trim_surrounding_whitespace_on_attribute_values() {
            var parsed = parser.parse(xml("""
                                              <configuration party="blue_gw">
                                                  <partyIdType name="t" value="  urn:type  "/>
                                                  <party name="blue_gw"><identifier partyId="  id  " partyIdType="t"/></party>
                                                  <service value="  EPO  "/>
                                                  <action value="  Form_A  "/>
                                              </configuration>
                                              """));

            assertThat(parsed.parties())
                .allSatisfy(party -> {
                    assertThat(party.identifier()).isEqualTo("id");
                    assertThat(party.identifierType()).isEqualTo("urn:type");
                });
            assertThat(parsed.services()).extracting("name").containsOnly("EPO");
            assertThat(parsed.actions()).extracting("name").containsOnly("Form_A");
        }

        @Test
        void should_not_fail_when_the_definition_contains_an_external_entity_declaration() {
            // SecureXmlParserUtil must neutralize this rather than resolve it.
            var withDoctype = """
                <?xml version="1.0" encoding="UTF-8"?>
                <!DOCTYPE configuration [<!ENTITY xxe SYSTEM "file:///etc/passwd">]>
                <configuration party="blue_gw">
                    <partyIdType name="t" value="urn:type"/>
                    <party name="blue_gw"><identifier partyId="id" partyIdType="t"/></party>
                    <service value="EPO"/>
                    <action value="Form_A"/>
                </configuration>
                """;

            assertThatThrownBy(() -> parser.parse(xml(withDoctype)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class);
        }
    }

    @Nested
    @DisplayName("when the definition cannot be read")
    class UnreadableDefinition {
        static Stream<Arguments> emptyContents() {
            return Stream.of(
                Arguments.of((Object) null),
                Arguments.of((Object) new byte[0])
            );
        }

        @ParameterizedTest
        @MethodSource("emptyContents")
        void should_reject_an_empty_definition(byte[] content) {
            assertThatThrownBy(() -> parser.parse(content))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("empty");
        }

        @Test
        void should_reject_a_malformed_document() {
            assertThatThrownBy(() -> parser.parse(xml("<configuration party=\"blue_gw\">")))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("well-formed");
        }

        @Test
        void should_not_leak_the_payload_in_the_error_message() {
            var secret = "<configuration party=\"blue_gw\"><secret>topsecret</secret>";

            assertThatThrownBy(() -> parser.parse(xml(secret)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageNotContaining("topsecret");
        }
    }

    @Nested
    @DisplayName("when the definition is structurally invalid")
    class InvalidDefinition {
        @Test
        void should_reject_a_root_element_without_a_party_attribute() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration>
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <party name="blue_gw"><identifier partyId="id" partyIdType="t"/></party>
                                                              <service value="EPO"/>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("party");
        }

        @Test
        void should_reject_a_home_party_that_is_not_declared_in_the_parties_list() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="unknown_gw">
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <party name="blue_gw"><identifier partyId="id" partyIdType="t"/></party>
                                                              <service value="EPO"/>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("unknown_gw");
        }

        @Test
        void should_reject_a_party_without_an_identifier_element() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="blue_gw">
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <party name="blue_gw"/>
                                                              <service value="EPO"/>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("blue_gw");
        }

        @Test
        void should_reject_a_party_without_a_name() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="blue_gw">
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <party><identifier partyId="id" partyIdType="t"/></party>
                                                              <service value="EPO"/>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("name");
        }

        @Test
        void should_reject_an_identifier_without_a_party_id() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="blue_gw">
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <party name="blue_gw"><identifier partyIdType="t"/></party>
                                                              <service value="EPO"/>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("partyId");
        }

        @Test
        void should_reject_an_identifier_referencing_an_undeclared_party_id_type() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="blue_gw">
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <party name="blue_gw"><identifier partyId="id" partyIdType="missing"/></party>
                                                              <service value="EPO"/>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("missing");
        }

        @Test
        void should_reject_duplicate_party_id_types_with_conflicting_values() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="blue_gw">
                                                              <partyIdType name="t" value="urn:type:one"/>
                                                              <partyIdType name="t" value="urn:type:two"/>
                                                              <party name="blue_gw"><identifier partyId="id" partyIdType="t"/></party>
                                                              <service value="EPO"/>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("Duplicate");
        }

        @Test
        void should_accept_duplicate_party_id_types_with_identical_values() {
            assertThatNoException().isThrownBy(() -> parser.parse(xml("""
                                                                          <configuration party="blue_gw">
                                                                              <partyIdType name="t" value="urn:type"/>
                                                                              <partyIdType name="t" value="urn:type"/>
                                                                              <party name="blue_gw"><identifier partyId="id" partyIdType="t"/></party>
                                                                              <service value="EPO"/>
                                                                              <action value="Form_A"/>
                                                                          </configuration>
                                                                          """)));
        }

        @Test
        void should_reject_a_definition_without_any_party() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="blue_gw">
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <service value="EPO"/>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class);
        }

        @Test
        void should_reject_a_definition_without_any_service() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="blue_gw">
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <party name="blue_gw"><identifier partyId="id" partyIdType="t"/></party>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("service");
        }

        @Test
        void should_reject_a_definition_without_any_action() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="blue_gw">
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <party name="blue_gw"><identifier partyId="id" partyIdType="t"/></party>
                                                              <service value="EPO"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("action");
        }

        @Test
        void should_reject_a_service_without_a_value() {
            assertThatThrownBy(() -> parser.parse(xml("""
                                                          <configuration party="blue_gw">
                                                              <partyIdType name="t" value="urn:type"/>
                                                              <party name="blue_gw"><identifier partyId="id" partyIdType="t"/></party>
                                                              <service name="testService1"/>
                                                              <action value="Form_A"/>
                                                          </configuration>
                                                          """)))
                .isInstanceOf(ConnectorProcessingModeParsingException.class)
                .hasMessageContaining("value");
        }
    }
}
