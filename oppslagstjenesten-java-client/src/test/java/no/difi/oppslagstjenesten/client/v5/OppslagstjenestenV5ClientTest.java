package no.difi.oppslagstjenesten.client.v5;

import no.difi.kontaktinfo.wsdl.oppslagstjeneste_16_02.Oppslagstjeneste1602;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._16_02.*;
import no.difi.oppslagstjenesten.client.cxf.WSS4JInterceptorHelper;
import no.difi.oppslagstjenesten.client.performance.v5.OppslagstjenestenKlient;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

//import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
            serviceAddress = "https://kontaktinfo-ws-ver2.difi.no/kontaktinfo-external/ws-v5"
            //serviceAddress = "https://kontaktinfo-ws-ver1.difi.no/kontaktinfo-external/ws-v5";
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
        OppslagstjenestenKlient.disableSslChecks(client);
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

		HentPersonerForespoersel personas = new HentPersonerForespoersel();
        personas.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
        personas.getPersonidentifikator().addAll(Arrays.asList(TEST_SSN_1, TEST_SSN_2));

        HentPersonerRespons personasResponse = oppslagstjenestenWithSigningPaaVegneAv.hentPersoner(personas, ot);
		assertEquals(TEST_SSN_1, personasResponse.getPerson().get(0).getPersonidentifikator());
        assertEquals(TEST_SSN_2, personasResponse.getPerson().get(1).getPersonidentifikator());
    }
}
