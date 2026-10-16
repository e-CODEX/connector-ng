/*
 * Copyright 2026 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.infrastructure.adapter.out.soap;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import org.apache.wss4j.common.ext.WSPasswordCallback;

/**
 * Callback handler for retrieving the keystore password.
 */
public class KeystorePasswordCallback implements CallbackHandler {
    private final String privateKeyPassword;

    public KeystorePasswordCallback(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    @Override
    public void handle(Callback[] callbacks) {
        for (Callback cb : callbacks) {
            if (cb instanceof WSPasswordCallback wpc) {
                wpc.setPassword(privateKeyPassword);
            }
        }
    }
}
