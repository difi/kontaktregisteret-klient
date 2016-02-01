package no.difi.oppslagstjenesten.client.v5;

import no.difi.oppslagstjenesten.client.cxf.WSS4JInterceptorHelper;



import no.difi.kontaktinfo.wsdl.oppslagstjeneste_16_02.Oppslagstjeneste1602;

import no.difi.kontaktinfo.xsd.oppslagstjeneste._16_02.*;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.security.cert.X509Certificate;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

/**
 * Sample client tests to demonstrate how to set up and use: Oppslagstjenesten for kontakt- og reservasjonsregisteret v5.
 *
 * @see <a href="https://begrep.difi.no/">https://begrep.difi.no/</a>
 */
public class OppslagstjenestenV5ClientTest {

    private static Oppslagstjeneste1602 oppslagstjenesten;
    private static Oppslagstjeneste1602 oppslagstjenestenWithSigningPaaVegneAv;

    private static final String TEST_SSN_1 = "02018090573";
    private static final String TEST_SSN_2 = "02018090301";

    @BeforeClass
    public static void beforeClass() {
        // Optionally set system property "kontaktinfo.address.location" to override the default test endpoint
        String serviceAddress = System.getProperty("kontaktinfo.address.location");
        if (serviceAddress == null) {
            serviceAddress = "https://kontaktinfo-ws-ver2.difi.no/kontaktinfo-external/ws-v5";
        }

        oppslagstjenesten = getOppslagstjenestePort(serviceAddress, false);
        oppslagstjenestenWithSigningPaaVegneAv = getOppslagstjenestePort(serviceAddress, true);

        // Optionally set system property "kontaktinfo.ssl.disable" to disable SSL checks to enable running tests against endpoint with invalid SSL setup
        String disableSslChecks = System.getProperty("kontaktinfo.ssl.disable");
        disableSslChecks = "true";
        if (disableSslChecks != null && disableSslChecks.equalsIgnoreCase("true")) {
            disableSslChecks(oppslagstjenesten);
            disableSslChecks(oppslagstjenestenWithSigningPaaVegneAv);
            System.setProperty("com.sun.net.ssl.checkRevocation", "false");
        }
    }

    private static Oppslagstjeneste1602 getOppslagstjenestePort(String serviceAddress, boolean usePaaVegneAv ) {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(Oppslagstjeneste1602.class);
        jaxWsProxyFactoryBean.setAddress(serviceAddress);
        jaxWsProxyFactoryBean.setBindingId(javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING);
        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBean, usePaaVegneAv);

        return (Oppslagstjeneste1602) jaxWsProxyFactoryBean.create();
    }

    private static void disableSslChecks(Oppslagstjeneste1602 oppslagstjenesten) {
        Client client = ClientProxy.getClient(oppslagstjenesten);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setDisableCNCheck(true);
        tlsClientParameters.setTrustManagers(new TrustManager[] { new X509TrustManager()  {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }

        } });
        httpConduit.setTlsClientParameters(tlsClientParameters);
    }

    @Test
    public void testHentKontaktSertifikat() {
        HentPrintSertifikatForespoersel print = new HentPrintSertifikatForespoersel();
        HentPrintSertifikatRespons response = oppslagstjenesten.hentPrintSertifikat(print, null);
        assertTrue(response.getPostkasseleverandoerAdresse().length() > 0);
        assertTrue(response.getX509Sertifikat().length > 0);
    }

    @Test
    public void testHentKontaktinfo() {
        HentPersonerForespoersel personas = new HentPersonerForespoersel();
        personas.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
        personas.getPersonidentifikator().addAll(Arrays.asList(TEST_SSN_1, TEST_SSN_2));
        HentPersonerRespons personasResponse = oppslagstjenesten.hentPersoner(personas, null);
        assertNotNull(personasResponse);
        assertEquals(TEST_SSN_1, personasResponse.getPerson().get(0).getPersonidentifikator());
        assertEquals(TEST_SSN_2, personasResponse.getPerson().get(1).getPersonidentifikator());
    }

    @Test
    public void testHentEndringer() {
        HentEndringerForespoersel endringerForespoersel = new HentEndringerForespoersel();
        endringerForespoersel.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
        endringerForespoersel.setFraEndringsNummer(600);
        HentEndringerRespons endringerRespons = oppslagstjenesten.hentEndringer(endringerForespoersel, null);
        assertNotNull(endringerRespons);
    }

    @Test
    public void testHentKontaktinfoWitPaaVegneAv() {

        Oppslagstjenesten ot = new Oppslagstjenesten();
        ot.setPaaVegneAv("991825827");

        HentEndringerForespoersel endringer = new HentEndringerForespoersel();
        endringer.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
        endringer.setFraEndringsNummer(0);

        HentEndringerRespons endringerRepsons = oppslagstjenestenWithSigningPaaVegneAv.hentEndringer(endringer, ot);
        assertNotNull(endringerRepsons);

    }
}
