/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.logging;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("LogLineInjectionTest")
@SuppressWarnings({"MissingJavadocType", "MissingJavadocMethod"})
class LogLineInjectionTest {
    @Test
    void should_keep_a_message_with_line_breaks_on_a_single_log_line() {
        var configuration = LoggerContext.getContext(false).getConfiguration();
        Appender console = configuration.getAppender("Console");
        assertThat(console).isNotNull();

        var event = Log4jLogEvent
            .newBuilder()
            .setLoggerName("eu.ecodex.connector.test")
            .setLevel(Level.INFO)
            .setMessage(new SimpleMessage("user\r\n2026-01-01 INFO forged line"))
            .build();

        var line = new String(console.getLayout().toByteArray(event));

        // the layout ends the entry with a single line break
        assertThat(line.stripTrailing()).doesNotContain("\r", "\n");
        assertThat(line).contains("user\\r\\n2026-01-01 INFO forged line");
    }
}
