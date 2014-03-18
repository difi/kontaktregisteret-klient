package no.difi.kontaktregister.external.client.sample;

import no.difi.kontaktinfo.external.client.cxf.WSS4JInterceptorHelper;


import no.difi.kontaktinfo.external.client.v1.generated.Kontaktinfo;
import no.difi.kontaktinfo.wsdl.oppslagstjeneste_14_05.Oppslagstjeneste1405;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentEndringerForespoersel;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentEndringerRespons;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPersonerForespoersel;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPersonerRespons;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPrintSertifikatForespoersel;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPrintSertifikatRespons;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.Informasjonsbehov;

import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.MalformedURLException;
import java.util.Arrays;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

/**
 * Sample client tests to demonstrate how to set up and use the Kontaktinfo External Web Service.
 *
 * @see <a href="https://kontaktinfo-ws.difi.no/kontaktinfo-external/dok/v3">https://kontaktinfo-ws.difi.no/kontaktinfo-external/dok/v3</a>
 */
public class KontaktregisterExternalWSSampleClientTest {

    private static Oppslagstjeneste1405 kontaktinfoPort;

    private static final String TEST_SSN_1 = "02018090573";
    private static final String TEST_SSN_2 = "02018090301";
    
    @BeforeClass
    public static void beforeClass() throws MalformedURLException {
        String serviceAddress = System.getProperty("kontaktinfo.address.location");
        if(serviceAddress == null) {
        	serviceAddress = "https://kontaktinfo-systest.dmz.local/kontaktinfo-external/ws-v3/";
        	System.out.println("kontaktinfo.address.location not set - using " +serviceAddress+" as default");
        }

        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(Oppslagstjeneste1405.class);
        jaxWsProxyFactoryBean.setAddress(serviceAddress);
        
        // Configure WS Security
        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBean);

        kontaktinfoPort = (Oppslagstjeneste1405) jaxWsProxyFactoryBean.create();
        
        // Disables SSL certificate revocation and certificate common name check
        Client client = ClientProxy.getClient(kontaktinfoPort);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(tlsClientParameters);
        System.setProperty("com.sun.net.ssl.checkRevocation", "false");
    }

    @Test
    public void testHentKontaktSertifikat() {
    	
    	HentPrintSertifikatForespoersel print = new HentPrintSertifikatForespoersel(); 
    	HentPrintSertifikatRespons response = kontaktinfoPort.hentPrintSertifikat(print);
    	 
    	assertTrue(response.getPostkasseleverandorAdresse().length() > 0);
    	assertTrue(response.getX509Certificate().length() > 0);
    }
    
    @Test
    public void testHentKontaktinfo(){
    	String testSSN = "20019950078";
    	
    	HentPersonerForespoersel personas = new HentPersonerForespoersel();
    	personas.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
    	personas.getPersonidentifikator().add(testSSN);
    	HentPersonerRespons personasResponse = kontaktinfoPort.hentPersoner(personas);
    	
    	assertNotNull(personasResponse);
    	
    	String ssn = "";
    	String tempSSN = "";
    		tempSSN = personasResponse.getPerson().get(0).getPersonidentifikator();
    		if(testSSN.equals(tempSSN)){
    			ssn = tempSSN;
    		}
    	assertEquals(ssn, testSSN);
    	
    }
    
    @Test
    public void testHentEndringer(){
    	
    	HentEndringerForespoersel endringerForespoersel = new HentEndringerForespoersel();
    	endringerForespoersel.getInformasjonsbehov().add(Informasjonsbehov.KONTAKTINFO);
    	endringerForespoersel.setFraEndringsNummer(600);
//    	endringerForespoersel.getFraEndringsNummer();
    	HentEndringerRespons endringerRespons = kontaktinfoPort.hentEndringer(endringerForespoersel);
    	
    	
    	assertNotNull(endringerRespons);
    	assertEquals(1000,endringerRespons.getPerson().size());
    	
    }
}
