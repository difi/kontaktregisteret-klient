package no.difi.kontaktinfo.external.client.sample;

import no.difi.kontaktinfo.external.client.cxf.WSS4JInterceptorHelper;
import no.difi.kontaktinfo.external.client.v1.generated.*;

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

/**
 * Sample client tests to demonstrate how to set up and use the Kontaktinfo External Web Service.
 *
 * @see <a href="https://kontaktinfo-ws.difi.no/kontaktinfo-external/dok/v1">https://kontaktinfo-ws.difi.no/kontaktinfo-external/dok/v1</a>
 */
public class KontaktinfoExternalWSSampleClientTest {

    private static KontaktinfoV10 kontaktinfoPort;
    
    private static final String TEST_SSN_1 = "05015554047";
    private static final String TEST_SSN_2 = "05015557518";
    private static final String TEST_SERVICE_OWNER = "testsp-dig";

    @BeforeClass
    public static void beforeClass() throws MalformedURLException {
    	// Get the service address - for production this is https://kontaktinfo-ws.difi.no/kontaktinfo-external/ws
        String serviceAddress = System.getProperty("kontaktinfo.address.location");
        if(serviceAddress == null) {
        	serviceAddress = "https://kontaktinfo-systest.dmz.local/kontaktinfo-external/ws";
            System.out.println("kontaktinfo.address.location not set - using https://kontaktinfo-systest.dmz.local/kontaktinfo-external/ws as default");
        }

        JaxWsProxyFactoryBean jaxWsProxyFactoryBean = new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(KontaktinfoV10.class);
        jaxWsProxyFactoryBean.setAddress(serviceAddress);

        // Configure WS Security
        WSS4JInterceptorHelper.addWSS4JInterceptors(jaxWsProxyFactoryBean);
        kontaktinfoPort = (KontaktinfoV10) jaxWsProxyFactoryBean.create();
        
        // Disables SSL certificate revocation and certificate common name check
        Client client = ClientProxy.getClient(kontaktinfoPort);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        TLSClientParameters tlsClientParameters = new TLSClientParameters();
        tlsClientParameters.setDisableCNCheck(true);
        httpConduit.setTlsClientParameters(tlsClientParameters);
        System.setProperty("com.sun.net.ssl.checkRevocation", "false");
    }

    @Test
    public void testHentKontaktinfo() {
        HentKontaktinfoRequest request = new HentKontaktinfoRequest();
        request.setFoedselsnummer(TEST_SSN_1);
        request.setTjenesteeierId(TEST_SERVICE_OWNER);
        HentKontaktinfoResponse response = kontaktinfoPort.hentKontaktinfo(request);

        assertNotNull(response);
        assertEquals(TEST_SSN_1, response.getKontaktinfo().getFoedselsnummer());
    }

    @Test
    public void testHentKontaktinfoListe() {
        HentKontaktinfolisteRequest request = new HentKontaktinfolisteRequest();
        request.getFoedselsnummer().addAll(Arrays.asList(TEST_SSN_1, TEST_SSN_2));
        request.setTjenesteeierId(TEST_SERVICE_OWNER);

        HentKontaktinfolisteResponse response = kontaktinfoPort.hentKontaktinfoliste(request);

        assertNotNull(response);
        assertEquals(2, response.getKontaktinfo().size());
        assertEquals(TEST_SSN_1, response.getKontaktinfo().get(0).getFoedselsnummer());
        assertEquals(TEST_SSN_2, response.getKontaktinfo().get(1).getFoedselsnummer());
    }
}
