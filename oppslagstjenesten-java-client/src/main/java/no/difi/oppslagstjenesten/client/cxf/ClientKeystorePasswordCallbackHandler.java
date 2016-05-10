package no.difi.oppslagstjenesten.client.cxf;

import org.apache.ws.security.WSPasswordCallback;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;

/**
 * Callback handler to set correct password for the client's private key (used for signing and decrypting).
 */
public class ClientKeystorePasswordCallbackHandler implements CallbackHandler {
    public void handle(Callback[] callbacks) {
        for (Callback callback : callbacks) {
            WSPasswordCallback pc = (WSPasswordCallback) callback;
            pc.setPassword("changeit");
            return;

        }
    }
}
