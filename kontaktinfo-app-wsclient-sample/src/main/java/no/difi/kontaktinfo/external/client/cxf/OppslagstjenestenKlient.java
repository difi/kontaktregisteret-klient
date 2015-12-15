package no.difi.kontaktinfo.external.client.cxf;

import no.difi.kontaktinfo.wsdl.oppslagstjeneste_16_02.Oppslagstjeneste1602;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;

public class OppslagstjenestenKlient {
    private String serviceAddress;
    private Oppslagstjeneste1602 oppslagstjenstePort;

    public OppslagstjenestenKlient(String url){
        this.serviceAddress = url;


        JaxWsProxyFactoryBean jaxWsProxyFactoryBeanOppslagstjenste = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBeanOppslagstjenste.setServiceClass(Oppslagstjeneste1602.class);
        jaxWsProxyFactoryBeanOppslagstjenste.setAddress(serviceAddress);

        // Configures WS-Security
        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBeanOppslagstjenste);

        oppslagstjenstePort = (Oppslagstjeneste1602) jaxWsProxyFactoryBeanOppslagstjenste.create();

        disableSSL(oppslagstjenstePort);



    }

    private void disableSSL(Object kontaktinfoPort) {
        Client client = ClientProxy.getClient(kontaktinfoPort);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(tlsClientParameters);
        System.setProperty("com.sun.net.ssl.checkRevocation", "false");
    }



    public Oppslagstjeneste1602 getOppslagstjenstePort(){
        return oppslagstjenstePort;
    }
}
