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

import eu.ecodex.connector.AbstractIntegrationTest;
import eu.ecodex.connector.application.port.spi.message.ConnectorMessageTransportStepRepository;
import eu.ecodex.connector.domain.model.message.transport.ConnectorMessageTransportStatus;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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
    void should_make_a_second_reader_wait_and_see_the_status_the_first_one_committed()
        throws Exception {
        // Given
        var step = transportStepRepository.findByIdentifier(TRANSPORT_IDENTIFIER);
        assertThat(step.status()).isEqualTo(READY_FOR_PULL);
        var messageIdentifier = step.transportedMessageIdentifier();
        var transaction = new TransactionTemplate(transactionManager);
        var locked = new CountDownLatch(1);
        var release = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(2);

        try {
            // When: a first transaction locks the step and delivers the message
            var first = executor.submit(() -> transaction.execute(status -> {
                transportStepRepository
                    .findByTransportedMessageIdentifierForUpdate(messageIdentifier);
                locked.countDown();
                await(release);
                transportStepRepository.update(
                    TRANSPORT_IDENTIFIER, DELIVERED, step.numberOfAttempts());
                return null;
            }));
            assertThat(locked.await(30, TimeUnit.SECONDS)).isTrue();

            // and a second one asks for the same step meanwhile
            var second = executor.submit(() -> transaction.execute(status ->
                transportStepRepository
                    .findByTransportedMessageIdentifierForUpdate(messageIdentifier)
                    .status()));

            // Then: it waits for the first one
            Thread.sleep(1_000);
            assertThat(second.isDone()).as("the second reader waits for the lock").isFalse();

            // and, once the first one has committed, sees what it committed
            release.countDown();
            first.get(30, TimeUnit.SECONDS);
            assertThat(second.get(30, TimeUnit.SECONDS))
                .isEqualTo(ConnectorMessageTransportStatus.DELIVERED);
        } finally {
            release.countDown();
            executor.shutdownNow();
        }
    }

    private static void await(CountDownLatch latch) {
        try {
            assertThat(latch.await(30, TimeUnit.SECONDS)).isTrue();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
