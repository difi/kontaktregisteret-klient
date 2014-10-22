package no.difi.kontaktinfo.external.client.cxf;

import no.difi.kontaktinfo.wsdl.oppslagstjeneste_14_05.Oppslagstjeneste1405;
import no.difi.kontaktinfo.wsdl.postkasseleverandoer.v1.PostkasseleverandoerV1;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.ws.security.wss4j.WSS4JInInterceptor;
import org.apache.cxf.ws.security.wss4j.WSS4JOutInterceptor;

import java.util.Map;

public class OppslagstjenestenKlient {
    private final PostkasseleverandoerV1 postkasseleverandoerV1Port;
    private String serviceAddress;
    private Oppslagstjeneste1405 oppslagstjenstePort;

    public OppslagstjenestenKlient(String url, WSS4JInInterceptor in, WSS4JOutInterceptor out){
        this.serviceAddress = url;


        // Enables running against alternative endpoints to the one specified in the WSDL
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(PostkasseleverandoerV1.class);
        jaxWsProxyFactoryBean.setAddress(serviceAddress);

        // Configures WS-Security
        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBean);

        postkasseleverandoerV1Port = (PostkasseleverandoerV1) jaxWsProxyFactoryBean.create();

        disableSSL(postkasseleverandoerV1Port);

        JaxWsProxyFactoryBean jaxWsProxyFactoryBeanOppslagstjenste = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBeanOppslagstjenste.setServiceClass(Oppslagstjeneste1405.class);
        jaxWsProxyFactoryBeanOppslagstjenste.setAddress(serviceAddress);

        // Configures WS-Security
        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBeanOppslagstjenste, in, out);

        oppslagstjenstePort = (Oppslagstjeneste1405) jaxWsProxyFactoryBeanOppslagstjenste.create();

        disableSSL(oppslagstjenstePort);

    }

    public OppslagstjenestenKlient(String url, String keystoreAlias, Map<String, String> properties) {
        this(
                url,
                new WSS4JInInterceptor(WSS4JInterceptorHelper.getInProperties()),
                new WSS4JOutInterceptor(OppslagstjenestenKlient.addExtra(WSS4JInterceptorHelper.getOutProperties(keystoreAlias), properties))
        );
    }

    private static Map<String, Object> addExtra(Map<String, Object> outProperties, Map<String, String> properties) {
        outProperties.putAll(properties);
        return outProperties;
    }

    public OppslagstjenestenKlient(String s) {
        this(s, new WSS4JInInterceptor(WSS4JInterceptorHelper.getInProperties()), new WSS4JOutInterceptor(WSS4JInterceptorHelper.getOutProperties("client_alias")));
    }


    private void disableSSL(Object kontaktinfoPort) {
        Client client = ClientProxy.getClient(kontaktinfoPort);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(tlsClientParameters);
        System.setProperty("com.sun.net.ssl.checkRevocation", "false");
    }


    public PostkasseleverandoerV1 getPostkassePort() {
        return postkasseleverandoerV1Port;
    }

    public Oppslagstjeneste1405 getOppslagstjenstePort(){
        return oppslagstjenstePort;
    }
}
