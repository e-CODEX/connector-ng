package eu.ecodex.connector.infrastructure.repository.message;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import eu.ecodex.connector.BusinessMessageTestFixtures;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageAS4PropertiesRepository;
import eu.ecodex.connector.infrastructure.repository.AbstractRepositoryTest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id IS NOT NULL",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
@DisplayName("ConnectorMessageAS4PropertiesRepository")
public class ConnectorMessageAS4PropertiesRepositoryTest extends AbstractRepositoryTest {
    @Autowired
    private ConnectorMessageAS4PropertiesRepository repository;

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/party.sql",
        "classpath:sql/service.sql",
        "classpath:sql/action.sql",
        "classpath:sql/message.sql",
    })
    private @interface WithMessageData {
    }

    @SuppressWarnings("DataFlowIssue")
    @Nested
    @DisplayName("save an as4 properties")
    class Save {
        @Test
        @WithMessageData
        void should_save_message_as4_properties() {
            var message = BusinessMessageTestFixtures
                .createOutboundMessage()
                .toBuilder()
                .identifier("fd2f35e0-1981-4d21-b718-10a802e884b0@connector.ecodex.eu")
                .build();

            var savedProperties = repository.save(message);

            assertNotNull(savedProperties);
            assertNotNull(savedProperties.service());
            assertNotNull(savedProperties.action());
            assertNotNull(savedProperties.fromParty());
            assertNotNull(savedProperties.toParty());
            assertNotNull(savedProperties.originalSender());
            assertNotNull(savedProperties.finalRecipient());
        }

        @Test
        void should_not_save_message_as4_properties_with_null_message() {
            assertThrows(
                NullPointerException.class, () -> repository.save(null)
            );
        }
    }
}
