package no.difi.kontaktinfo.external.client.cxf;

import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;
import org.apache.ws.security.handler.WSHandlerConstants;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Helper to setup the required client side WSS security interceptors required by the Kontaktinfo external Web Service.
 */
public class WSS4JInterceptorHelper {


    public static Map<String, Object> getOutProperties(String clientAlias) {
        final Map<String, Object> outProps = new HashMap<String, Object>();
        // for outgoing messages: Signature and Timestamp validation
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " " + WSHandlerConstants.TIMESTAMP);
        outProps.put(WSHandlerConstants.USER, clientAlias);
        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientKeystorePasswordCallbackHandler.class.getName());
        outProps.put(WSHandlerConstants.SIG_PROP_FILE, "client_sec.properties");
        return outProps;
    }

    public static Map<String, Object> getInProperties() {
        final Map<String, Object> inProps = new HashMap<String, Object>();
        // for incoming messages: Signature and Timestamp validation. Response is Encrypted
        inProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " " + WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.ENCRYPT);
        inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientKeystorePasswordCallbackHandler.class.getName());
        inProps.put(WSHandlerConstants.SIG_PROP_FILE, "server_sec.properties");
        inProps.put(WSHandlerConstants.DEC_PROP_FILE, "client_sec.properties");
        return inProps;
    }

    private static Logger logger = Logger.getLogger("WSS4JInterceptorHelper");

    /**
     * Adds the required WSS4J interceptors to the given provider.
     *
     * @param interceptorProvider the provider to configure.
     */
    public static void addWSS4JInterceptors(InterceptorProvider interceptorProvider, List<String> list) {
        interceptorProvider.getInInterceptors().add(new WSS4JInInterceptor(getInProperties()));
        interceptorProvider.getInInterceptors().add(new LoggingInInterceptor());
        interceptorProvider.getInInterceptors().add(new SOAPMessageInterceptor(list));


        interceptorProvider.getOutInterceptors().add(new WSS4JOutInterceptor(getOutProperties("client-alias")));
        interceptorProvider.getOutInterceptors().add(new LoggingOutInterceptor());
    }


    /**
     * Adds the required WSS4J interceptors to the given provider.
     *
     * @param interceptorProvider the provider to configure.
     * @param list
     */
    public static void addWSS4JInterceptors(InterceptorProvider interceptorProvider, WSS4JInInterceptor in, WSS4JOutInterceptor out, List<String> list) {
        interceptorProvider.getInInterceptors().add(in);
        interceptorProvider.getInInterceptors().add(new LoggingInInterceptor());
        if(list != null){
            interceptorProvider.getInInterceptors().add(new SOAPMessageInterceptor(list));
        }
        interceptorProvider.getOutInterceptors().add(out);
        interceptorProvider.getOutInterceptors().add(new LoggingOutInterceptor());
    }
}
