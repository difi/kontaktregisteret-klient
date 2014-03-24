package no.difi.kontaktinfo.external.client.cxf;

import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.handler.WSHandlerConstants;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper to setup the required client side WSS security interceptors required by the Kontaktinfo external Web Service.
 */
public class WSS4JInterceptorHelper {


    private static final WSS4JInInterceptor wss4JInInterceptor;
    private static final WSS4JOutInterceptor wss4JOutInterceptor;

    static {
        final Map<String, Object> outProps = new HashMap<String, Object>();
        final Map<String, Object> inProps = new HashMap<String, Object>();

        // for outgoing messages: Signature and Timestamp validation
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " " + WSHandlerConstants.TIMESTAMP);
        outProps.put(WSHandlerConstants.USER, "client-alias");
        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS,
                ClientKeystorePasswordCallbackHandler.class.getName());
        outProps.put(WSHandlerConstants.SIG_PROP_FILE, "client_sec.properties");

        // for incoming messages: Signature and Timestamp validation. Response is Encrypted
        inProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " " + WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.ENCRYPT);
        inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientKeystorePasswordCallbackHandler.class.getName());
        inProps.put(WSHandlerConstants.SIG_PROP_FILE, "server_sec.properties");
        inProps.put(WSHandlerConstants.DEC_PROP_FILE, "client_sec.properties");
        

        wss4JInInterceptor = new WSS4JInInterceptor(inProps);
        wss4JOutInterceptor = new WSS4JOutInterceptor(outProps);
    }


    /**
     * Adds the required WSS4J interceptors to the given provider.
     *
     * @param interceptorProvider the provider to configure.
     */
    public static void addWSS4JInterceptors(org.apache.cxf.interceptor.InterceptorProvider interceptorProvider) {
        interceptorProvider.getInInterceptors().add(wss4JInInterceptor);
        interceptorProvider.getInInterceptors().add(new LoggingInInterceptor());
        interceptorProvider.getOutInterceptors().add(wss4JOutInterceptor);
    }
}
