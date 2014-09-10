package no.difi.kontaktinfo.external.client.cxf;

import no.difi.begrep.Kontaktinformasjon;
import no.difi.kontaktinfo.wsdl.oppslagstjeneste_14_05.Oppslagstjeneste1405;
import no.difi.kontaktinfo.wsdl.postkasseleverandoer.v1.PostkasseleverandoerV1;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;

public class OppslagstjenestenKlient {
    private final PostkasseleverandoerV1 postkasseleverandoerV1Port;
    private String serviceAddress;
    private Oppslagstjeneste1405 oppslagstjenstePort;

    public OppslagstjenestenKlient(String url){
        this.serviceAddress = url;


        // Enables running against alternative endpoints to the one specified in the WSDL
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(PostkasseleverandoerV1.class);
        jaxWsProxyFactoryBean.setAddress(serviceAddress);

        // Configures WS-Security
//        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBean);

        postkasseleverandoerV1Port = (PostkasseleverandoerV1) jaxWsProxyFactoryBean.create();

        disableSSL(postkasseleverandoerV1Port);

        JaxWsProxyFactoryBean jaxWsProxyFactoryBeanOppslagstjenste = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBeanOppslagstjenste.setServiceClass(Oppslagstjeneste1405.class);
        jaxWsProxyFactoryBeanOppslagstjenste.setAddress(serviceAddress);

        // Configures WS-Security
        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBeanOppslagstjenste);

        oppslagstjenstePort = (Oppslagstjeneste1405) jaxWsProxyFactoryBeanOppslagstjenste.create();

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


    public PostkasseleverandoerV1 getPostkassePort() {
        return postkasseleverandoerV1Port;
    }

    public Oppslagstjeneste1405 getOppslagstjenstePort(){
        return oppslagstjenstePort;
    }
}
