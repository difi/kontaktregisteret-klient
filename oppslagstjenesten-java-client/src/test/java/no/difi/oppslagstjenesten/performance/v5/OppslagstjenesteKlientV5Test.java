package no.difi.oppslagstjenesten.performance.v5;

import no.difi.kontaktinfo.wsdl.oppslagstjeneste_16_02.Oppslagstjeneste1602;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._16_02.*;
import no.difi.oppslagstjenesten.client.performance.v5.OppslagstjenestenKlient;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Sample client tests to demonstrate how to set up and use: Kontaktinfo webservice v3 / Oppslagstjenesten for kontakt- og reservasjonsregisteret v3.
 *
 * @see <a href="https://kontaktinfo-ws.difi.no/kontaktinfo-external/dok/v1">https://kontaktinfo-ws.difi.no/kontaktinfo-external/dok/v1</a>
 */
public class OppslagstjenesteKlientV5Test {

    private static Oppslagstjeneste1602 kontaktinfoPort;
    private static Oppslagstjeneste1602 kontaktinfoPortPaaVegneAv;

    private static final String TEST_SSN_1 = "02018090573";
    private static final String TEST_SSN_2 = "02018090301";
    private static final String TEST_SSN_3 = "01010209303";

    @BeforeClass
    public static void beforeClass() {
        // Optionally set system property "kontaktinfo.address.location" to override the default test endpoint
        String serviceAddress = System.getProperty("kontaktinfo.address.location");
        if (serviceAddress == null) {
//        	serviceAddress = "https://kontaktinfo-ws-ver2.difi.no/kontaktinfo-external/ws-v5";
//            serviceAddress = "https://eid-systest-web01.dmz.local/kontaktinfo-external/ws-v5";
            serviceAddress = "http://eid-vag-admin.difi.local:10002/kontaktinfo-external/ws-v5";
        	serviceAddress = "https://kontaktinfo-ws-test1.difi.eon.no/kontaktinfo-external/ws-v5";
            serviceAddress = "https://eid-atest-web01.dmz.local/kontaktinfo-external/ws-v5";
        }

        OppslagstjenestenKlient oppslagstjenestenKlient = new OppslagstjenestenKlient(serviceAddress, "client_alias");
        kontaktinfoPort = oppslagstjenestenKlient.getOppslagstjenstePort();
        kontaktinfoPortPaaVegneAv = oppslagstjenestenKlient.getOppslagstjenestenWithSigningPaaVegneAv();

//        OppslagstjenestenKlient oppslagstjenestenKlientPaaVegneAv = new OppslagstjenestenKlient(serviceAddress, "client_alias", true);
//        kontaktinfoPortPaaVegneAv = oppslagstjenestenKlientPaaVegneAv.getOppslagstjenstePort();

        // Optionally set system property "kontaktinfo.ssl.disable" to disable SSL checks to enable running tests against endpoint with invalid SSL setup
        String disableSslChecks = System.getProperty("kontaktinfo.ssl.disable");
        disableSslChecks = Boolean.TRUE.toString();
        if (disableSslChecks != null && disableSslChecks.equalsIgnoreCase("true")) {
            disableSSLChecks();

        }
    }

    private static void disableSSLChecks() {
        Client client = ClientProxy.getClient(kontaktinfoPort);
        OppslagstjenestenKlient.disableSslChecks(client);
    }

    @Test
    public void testHentKontaktSertifikat() {
        HentPrintSertifikatForespoersel print = new HentPrintSertifikatForespoersel();
        HentPrintSertifikatRespons response = kontaktinfoPort.hentPrintSertifikat(print, null);
        assertTrue(response.getPostkasseleverandoerAdresse().length() > 0);
        assertTrue(response.getX509Sertifikat().length > 0);
    }

    @Test
    public void testHentKontaktinfo() {
        HentPersonerForespoersel personas = new HentPersonerForespoersel();
        personas.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
        personas.getPersonidentifikator().addAll(Arrays.asList(TEST_SSN_1, TEST_SSN_2));
        HentPersonerRespons personasResponse = kontaktinfoPort.hentPersoner(personas, null);
        assertNotNull(personasResponse);
        assertEquals(TEST_SSN_1, personasResponse.getPerson().get(0).getPersonidentifikator());
        assertEquals(TEST_SSN_2, personasResponse.getPerson().get(1).getPersonidentifikator());
    }

    @Test
    public void testHentKontaktinfoMedPaaVegneAv() {

        Oppslagstjenesten ot = new Oppslagstjenesten();
        ot.setPaaVegneAv("991825827");

        HentPersonerForespoersel personas = new HentPersonerForespoersel();
        personas.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
        personas.getPersonidentifikator().addAll(Arrays.asList(TEST_SSN_1, TEST_SSN_2));
        HentPersonerRespons personasResponse = kontaktinfoPortPaaVegneAv.hentPersoner(personas, ot);
        assertNotNull(personasResponse);
        assertEquals(TEST_SSN_1, personasResponse.getPerson().get(0).getPersonidentifikator());
        assertEquals(TEST_SSN_2, personasResponse.getPerson().get(1).getPersonidentifikator());
    }

    @Test
    public void testHentKontaktinfoForPerson() {
        HentPersonerForespoersel personas = new HentPersonerForespoersel();
        personas.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
        personas.getInformasjonsbehov().add(Informasjonsbehov.SIKKER_DIGITAL_POST);
        personas.getInformasjonsbehov().add(Informasjonsbehov.SERTIFIKAT);
        personas.getInformasjonsbehov().add(Informasjonsbehov.PERSON);
        personas.getPersonidentifikator().addAll(Arrays.asList(TEST_SSN_3));
        HentPersonerRespons personasResponse = kontaktinfoPort.hentPersoner(personas, null);
        assertNotNull(personasResponse);
        assertEquals(TEST_SSN_3, personasResponse.getPerson().get(0).getPersonidentifikator());
    }

    @Test
    public void testHentEndringer() {
        HentEndringerForespoersel endringerForespoersel = new HentEndringerForespoersel();
        endringerForespoersel.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
        endringerForespoersel.setFraEndringsNummer(600);
        HentEndringerRespons endringerRespons = kontaktinfoPort.hentEndringer(endringerForespoersel, null);
        assertNotNull(endringerRespons);
    }

}
