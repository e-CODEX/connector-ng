/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector;

import org.springframework.core.Ordered;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

/**
 * Clears shared Testcontainers data before {@code @Sql} fixtures run.
 */
public class DatabaseCleanupTestExecutionListener implements TestExecutionListener, Ordered {
    @Override
    public void beforeTestMethod(TestContext testContext) {
        var jdbcTemplate = testContext.getApplicationContext().getBean(JdbcTemplate.class);
        TestDatabaseCleanup.truncateAll(jdbcTemplate);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
