package eu.ecodex.connector;

import jakarta.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class EmbeddedActiveMqConfig {
    @Bean
    @Primary
    public ConnectionFactory connectionFactory() {
        return new ActiveMQConnectionFactory("vm://embedded?broker.persistent=false");
    }
}
