package no.difi.kontaktinfo.external.client.cxf;

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


    private static final WSS4JInInterceptor wss4JInInterceptor;
    private static final WSS4JOutInterceptor wss4JOutInterceptor;
    private static final WSS4JOutInterceptor wss4JOutInterceptorV310;


    static {
        final Map<String, Object> outProps = new HashMap<String, Object>();
        final Map<String, Object> inProps = new HashMap<String, Object>();

        // for outgoing messages: Signature and Timestamp validation
        outProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " " + WSHandlerConstants.TIMESTAMP);
        outProps.put(WSHandlerConstants.USER, "client-alias");
        outProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientKeystorePasswordCallbackHandler.class.getName());
        outProps.put(WSHandlerConstants.SIG_PROP_FILE, "client_sec.properties");

        HashMap<String, Object> outPropsV310 = new HashMap<String, Object>(outProps);
        outPropsV310.put(WSHandlerConstants.SIGNATURE_PARTS, "{}{http://schemas.xmlsoap.org/soap/envelope/}Body ;" +
                "{}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd} Timestamp");

        wss4JOutInterceptorV310 = new WSS4JOutInterceptor(outPropsV310);



        // for incoming messages: Signature and Timestamp validation. Response is Encrypted
        inProps.put(WSHandlerConstants.ACTION, WSHandlerConstants.SIGNATURE + " " + WSHandlerConstants.TIMESTAMP + " " + WSHandlerConstants.ENCRYPT);
        inProps.put(WSHandlerConstants.PW_CALLBACK_CLASS, ClientKeystorePasswordCallbackHandler.class.getName());
        inProps.put(WSHandlerConstants.SIG_PROP_FILE, "server_sec.properties");
        inProps.put(WSHandlerConstants.DEC_PROP_FILE, "client_sec.properties");
        
        wss4JInInterceptor = new WSS4JInInterceptor(inProps);
        wss4JOutInterceptor = new WSS4JOutInterceptor(outProps);

    }


    public static void addWSS4JInterceptors(InterceptorProvider interceptorProvider) {
        interceptorProvider.getInInterceptors().add(wss4JInInterceptor);
        interceptorProvider.getInInterceptors().add(new LoggingInInterceptor());
        interceptorProvider.getOutInterceptors().add(wss4JOutInterceptor);
        interceptorProvider.getOutInterceptors().add(new LoggingOutInterceptor());
    }

    public static void addWSS4JInterceptorsV310(InterceptorProvider interceptorProvider) {
        interceptorProvider.getInInterceptors().add(wss4JInInterceptor);
        interceptorProvider.getInInterceptors().add(new LoggingInInterceptor());
        interceptorProvider.getOutInterceptors().add(wss4JOutInterceptorV310);
        interceptorProvider.getOutInterceptors().add(new LoggingOutInterceptor());
    }
}
