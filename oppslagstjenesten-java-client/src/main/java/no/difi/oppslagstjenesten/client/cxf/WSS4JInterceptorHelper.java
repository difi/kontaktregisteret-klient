package no.difi.oppslagstjenesten.client.cxf;

import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.handler.WSHandlerConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper to setup the required client side WSS security interceptors required by the Kontaktinfo external Web Service.
 */
public class WSS4JInterceptorHelper {

    private static WSS4JOutInterceptor getWss4JOutInterceptor(boolean signPaaVegneAv) {
        final Map<String, Object> outProps = new HashMap<String, Object>();

        // for outgoing messages: Signature and Timestamp validation
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " " + WSHandlerConstants.TIMESTAMP);
        outProps.put(WSHandlerConstants.USER, "client_alias");
        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientKeystorePasswordCallbackHandler.class.getName());
        outProps.put(WSHandlerConstants.SIG_PROP_FILE, "client_sec.properties");
        outProps.put(WSHandlerConstants.SIG_KEY_ID, "DirectReference"); // Using "X509KeyIdentifier" is also supported by oppslagstjenesten
        if (signPaaVegneAv) {
            outProps.put(WSHandlerConstants.SIGNATURE_PARTS, "{}{}Body;{}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp};{}{http://kontaktinfo.difi.no/xsd/oppslagstjeneste/16-02}Oppslagstjenesten");
        } else {
            outProps.put(WSHandlerConstants.SIGNATURE_PARTS, "{}{}Body;{}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp}");
        }
        outProps.put(WSHandlerConstants.SIG_ALGO, "http://www.w3.org/2001/04/xmldsig-more#rsa-sha256");

        return new WSS4JOutInterceptor(outProps);
    }

    private static WSS4JInInterceptor getWss4JInInterceptor() {

        final Map<String, Object> inProps = new HashMap<String, Object>();

        // for incoming messages: Signature and Timestamp validation. Response is Encrypted
        inProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " " + WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.ENCRYPT);
        inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientKeystorePasswordCallbackHandler.class.getName());
        inProps.put(WSHandlerConstants.SIG_PROP_FILE, "server_sec.properties");
        inProps.put(WSHandlerConstants.DEC_PROP_FILE, "client_sec.properties");

        return new WSS4JInInterceptor(inProps);

    }

    /**
     * Adds the required WSS4J interceptors to the given provider.
     *
     * @param interceptorProvider the provider to configure.
     */
    public static void addWSS4JInterceptors(InterceptorProvider interceptorProvider, boolean signPaaVegneAv) {
        interceptorProvider.getInInterceptors().add(getWss4JInInterceptor());
        interceptorProvider.getInInterceptors().add(new LoggingInInterceptor());
        interceptorProvider.getOutInterceptors().add(getWss4JOutInterceptor(signPaaVegneAv));
        interceptorProvider.getOutInterceptors().add(new LoggingOutInterceptor());
    }
}
