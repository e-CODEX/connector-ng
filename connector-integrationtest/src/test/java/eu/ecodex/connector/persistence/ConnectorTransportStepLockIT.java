/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence;

import static eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus.DELIVERED;
import static eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus.READY_FOR_PULL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStep;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@DisplayName("Transport step row lock")
@Sql(
    statements = "DELETE FROM connector_business_domains WHERE id > 0",
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
public class ConnectorTransportStepLockIT extends AbstractIntegrationTest {
    private static final String TRANSPORT_IDENTIFIER =
        "b0f19c4c-ac3e-438c-9951-8e3a5211fed4@connector.ecodex.eu_backend_alice";

    @Autowired
    private ConnectorMessageTransportStepRepository transportStepRepository;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterEach
    void cleanUp() {
        cleanDb();
    }

    @Test
    @WithReferenceData
    void should_make_a_second_reader_wait_when_the_step_is_read_by_its_message() throws Exception {
        var step = transportStepRepository.findByIdentifier(TRANSPORT_IDENTIFIER);

        assertThat(secondReaderWaitsForTheFirstOne(
            transportStepRepository::findByTransportedMessageIdentifierForUpdate,
            step.transportedMessageIdentifier(), step
        )).isEqualTo(DELIVERED);
    }

    @Test
    @WithReferenceData
    void should_make_a_second_reader_wait_when_the_step_is_read_by_any_of_its_identifiers()
        throws Exception {
        var step = transportStepRepository.findByIdentifier(TRANSPORT_IDENTIFIER);

        assertThat(secondReaderWaitsForTheFirstOne(
            transportStepRepository::findByStepOrMessageOrRemoteSystemIdentifierForUpdate,
            step.remoteSystemIdentifier(), step
        )).isEqualTo(DELIVERED);
    }

    /**
     * A first transaction locks the step with the given lookup and delivers the message, while a
     * second one asks for the same step. It must wait, and then see what the first one committed.
     */
    private ConnectorMessageTransportStatus secondReaderWaitsForTheFirstOne(
        Function<String, ConnectorMessageTransportStep> lookup,
        String identifier,
        ConnectorMessageTransportStep step) throws Exception {
        assertThat(step.status()).isEqualTo(READY_FOR_PULL);
        var transaction = new TransactionTemplate(transactionManager);
        var locked = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(2);

        try {
            var first = executor.submit(() -> transaction.execute(status -> {
                lookup.apply(identifier);
                locked.countDown();
                awaitLatch(release);
                transportStepRepository.update(
                    TRANSPORT_IDENTIFIER, DELIVERED, step.numberOfAttempts());
                return null;
            }));
            // the second one asks for the same step once the first one holds it
            var second = executor.submit(() -> {
                awaitLatch(locked);
                return transaction.execute(status -> lookup.apply(identifier).status());
            });

            // it keeps waiting for the lock for a whole second
            await("the second reader waits for the lock")
                .during(Duration.ofSeconds(1))
                .atMost(Duration.ofSeconds(5))
                .until(() -> !second.isDone());

            release.countDown();
            first.get(30, TimeUnit.SECONDS);
            return second.get(30, TimeUnit.SECONDS);
        } finally {
            release.countDown();
            executor.shutdownNow();
        }
    }

    private static void awaitLatch(CountDownLatch latch) {
        try {
            assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    @Sql({
        "classpath:sql/business-domain.sql",
        "classpath:sql/processing-mode.sql",
        "classpath:sql/processing-mode-party.sql",
        "classpath:sql/processing-mode-service.sql",
        "classpath:sql/processing-mode-action.sql",
        "classpath:sql/message.sql",
        "classpath:sql/message-as4-properties.sql",
        "classpath:sql/attachment.sql",
        "classpath:sql/message-business-content.sql",
        "classpath:sql/message-business-document.sql",
        "classpath:sql/message-transport-step.sql",
        "classpath:sql/message-transport-step-statuses.sql",
    })
    private @interface WithReferenceData {
    }
}
