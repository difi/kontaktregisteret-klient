package no.difi.kontaktregister.external.client.v4.sample;

import no.difi.kontaktinfo.external.client.cxf.WSS4JInterceptorHelper;


import no.difi.kontaktinfo.wsdl.oppslagstjeneste_14_05.Oppslagstjeneste1405;

import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.*;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

/**
 * Sample client tests to demonstrate how to set up and use: Kontaktinfo webservice v3 / Oppslagstjenesten for kontakt- og reservasjonsregisteret v3.
 *
 * @see <a href="https://kontaktinfo-ws.difi.no/kontaktinfo-external/dok/v1">https://kontaktinfo-ws.difi.no/kontaktinfo-external/dok/v1</a>
 */
public class KontaktregisterExternalWSSampleClient4Test {

    private static Oppslagstjeneste1405 kontaktinfoPort;

    private static final String TEST_SSN_1 = "02018090573";
    private static final String TEST_SSN_2 = "02018090301";
    
    @BeforeClass
    public static void beforeClass() {
    	// Optionally set system property "kontaktinfo.address.location" to override the default test endpoint
        String serviceAddress = System.getProperty("kontaktinfo.address.location");
        if(serviceAddress == null) {
        	serviceAddress = "http://localhost:9988/kontaktinfo-web-external/ws-v4";//"https://kontaktinfo-ws-ver2.difi.no/kontaktinfo-external/ws-v4";
        }

        // Enables running against alternative endpoints to the one specified in the WSDL
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(Oppslagstjeneste1405.class);
        jaxWsProxyFactoryBean.setAddress(serviceAddress);
        
        // Configures WS-Security
        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBean);
        kontaktinfoPort = (Oppslagstjeneste1405) jaxWsProxyFactoryBean.create();
        
        // Optionally set system property "kontaktinfo.ssl.disable" to disable SSL checks to enable running tests against endpoint with invalid SSL setup
        String disableSslChecks = System.getProperty("kontaktinfo.ssl.disable");
        if (disableSslChecks != null && disableSslChecks.equalsIgnoreCase("true")) {
            Client client = ClientProxy.getClient(kontaktinfoPort);
            HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
            TLSClientParameters tlsClientParameters = new TLSClientParameters();
            tlsClientParameters.setDisableCNCheck(true);
            httpConduit.setTlsClientParameters(tlsClientParameters);
            System.setProperty("com.sun.net.ssl.checkRevocation", "false");
        }
    }

    @Test
    public void testHentKontaktSertifikat() {
    	HentPrintSertifikatForespoersel print = new HentPrintSertifikatForespoersel(); 
    	HentPrintSertifikatRespons response = kontaktinfoPort.hentPrintSertifikat(print);
    	assertTrue(response.getPostkasseleverandoerAdresse().length() > 0);
    	assertTrue(response.getX509Sertifikat().length > 0);
    }

    @Test
    public void testHentKontaktinfo(){
    	HentPersonerForespoersel personas = new HentPersonerForespoersel();
    	personas.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
    	personas.getPersonidentifikator().addAll(Arrays.asList(TEST_SSN_1, TEST_SSN_2));
    	HentPersonerRespons personasResponse = kontaktinfoPort.hentPersoner(personas);
    	assertNotNull(personasResponse);
    	assertEquals(TEST_SSN_1, personasResponse.getPerson().get(0).getPersonidentifikator());
    	assertEquals(TEST_SSN_2, personasResponse.getPerson().get(1).getPersonidentifikator());
    }

    @Test
    public void testHentEndringer(){
    	HentEndringerForespoersel endringerForespoersel = new HentEndringerForespoersel();
    	endringerForespoersel.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
    	endringerForespoersel.setFraEndringsNummer(600);
    	HentEndringerRespons endringerRespons = kontaktinfoPort.hentEndringer(endringerForespoersel);
    	assertNotNull(endringerRespons);
    }

}
