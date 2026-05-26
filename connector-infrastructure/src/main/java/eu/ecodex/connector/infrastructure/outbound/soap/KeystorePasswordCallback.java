package eu.ecodex.connector.infrastructure.outbound.soap;

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
