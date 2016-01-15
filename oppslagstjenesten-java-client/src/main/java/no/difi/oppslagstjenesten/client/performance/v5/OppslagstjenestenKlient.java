package no.difi.oppslagstjenesten.client.performance.v5;

import no.difi.kontaktinfo.wsdl.oppslagstjeneste_16_02.Oppslagstjeneste1602;
import no.difi.oppslagstjenesten.client.cxf.WSS4JInterceptorHelper;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class OppslagstjenestenKlient {

    private Oppslagstjeneste1602 oppslagstjenstePort;
    private Oppslagstjeneste1602 oppslagstjenestenWithSigningPaaVegneAv;

    public OppslagstjenestenKlient(String url, String alias) {

        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(Oppslagstjeneste1602.class);
        jaxWsProxyFactoryBean.setAddress(url);
        jaxWsProxyFactoryBean.setBindingId(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING);

        // Configures WS-Security
        WSS4JInterceptorHelper.addWSS4JInterceptorsWithErrorLogger(jaxWsProxyFactoryBean, false, alias);
        oppslagstjenstePort = (Oppslagstjeneste1602) jaxWsProxyFactoryBean.create();

        // Make a new instance of Oppslagstjenesten client with signing of PaaVegneAv header element
        jaxWsProxyFactoryBean.getInInterceptors().clear();
        jaxWsProxyFactoryBean.getOutInterceptors().clear();

        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBean, true);
        oppslagstjenestenWithSigningPaaVegneAv = (Oppslagstjeneste1602) jaxWsProxyFactoryBean.create();

        disableSSL(oppslagstjenstePort, oppslagstjenestenWithSigningPaaVegneAv);

    }

    private void disableSSL(Object kontaktinfoPort, Object oppslagstjenestenWithSigningPaaVegneAv) {
        disableSslChecks(ClientProxy.getClient(kontaktinfoPort));
        disableSslChecks(ClientProxy.getClient(oppslagstjenestenWithSigningPaaVegneAv));
        System.setProperty("com.sun.net.ssl.checkRevocation", "false");
    }

    public static void disableSslChecks(Client client) {
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setDisableCNCheck(true);
        tlsClientParameters.setTrustManagers(new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

        }});
        httpConduit.setTlsClientParameters(tlsClientParameters);
    }


    public Oppslagstjeneste1602 getOppslagstjenstePort() {
        return oppslagstjenstePort;
    }

    public Oppslagstjeneste1602 getOppslagstjenestenWithSigningPaaVegneAv() {
        return oppslagstjenestenWithSigningPaaVegneAv;
    }
}
