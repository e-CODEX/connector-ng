/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.inbound.web.soap;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of the {@code CallbackHandler} interface, used for handling callbacks
 * during SOAP web service interactions. It logs the callbacks for traceability.
 *
 * <p>This class is typically used within the context of configuring security settings for
 * SOAP-based communication endpoints, where it acts as the handler for security-related callbacks
 * such as password or certificate validation requests.
 */
@Slf4j
public class DefaultWsCallbackHandler implements CallbackHandler {
    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        log.trace("Default callback handler called with callbacks [{}]", (Object[]) callbacks);
    }
}
