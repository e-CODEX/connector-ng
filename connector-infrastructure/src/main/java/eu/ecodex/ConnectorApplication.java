package eu.ecodex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * The ConnectorApplication class serves as the entry point for the Spring Boot application. It is
 * configured to scan for components and configurations within the specified base packages.
 *
 * <p>This class initializes and starts the application using the SpringApplication.run method.
 */
@SpringBootApplication(scanBasePackages = {"eu.ecodex.connector"})
public class ConnectorApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConnectorApplication.class, args);
    }
}
