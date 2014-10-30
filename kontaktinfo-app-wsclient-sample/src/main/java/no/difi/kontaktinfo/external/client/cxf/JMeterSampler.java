package no.difi.kontaktinfo.external.client.cxf;


import com.cedarsoftware.util.io.JsonReader;
import no.difi.begrep.Person;
import no.difi.kontaktinfo.wsdl.oppslagstjeneste_14_05.Oppslagstjeneste1405;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPersonerForespoersel;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPersonerRespons;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.Informasjonsbehov;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.ws.security.handler.WSHandlerConstants;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JMeterSampler extends AbstractJavaSamplerClient implements Serializable {


    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("Endpoint", "http://localhost:8888/kontaktinfo-web-external/ws-v4");
        defaultParameters.addArgument("Data", "[[\"12121212345\",\"0\",\"0\"]]");
        defaultParameters.addArgument("informasjonsbehov", "PERSON,SERTIFIKAT");
        defaultParameters.addArgument("alias", "client_alias");

        return defaultParameters;
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        String endpoint = javaSamplerContext.getParameter("Endpoint");
        String ssn = javaSamplerContext.getParameter("Data");
        String behov = javaSamplerContext.getParameter("informasjonsbehov");
        String alias = javaSamplerContext.getParameter("alias");

        System.out.println("called sampler " + String.format("%s,%s,%s,%s", endpoint, ssn, behov, alias));

        SampleResult result = new SampleResult();
        try {
            OppslagstjenestenKlient oppslagstjenestenKlient = new OppslagstjenestenKlient(endpoint, alias, createWSConfigForV4());
            Oppslagstjeneste1405 kontaktinfoPort = oppslagstjenestenKlient.getOppslagstjenstePort();

            HentPersonerForespoersel req = new HentPersonerForespoersel();
            setInformasjonsbehov(behov, req);

            Object[] list = (Object[]) JsonReader.jsonToJava(ssn);
            final Map<String, Object[]> map = mapSsnTilConfig(req, list);


            HentPersonerRespons hentPersonerRespons = executeHentPersoner(ssn, behov, alias, result, kontaktinfoPort, req);

            if (hentPersonerRespons == null || hentPersonerRespons.getPerson() == null) {
                result.setSuccessful(false);
            } else if (map.size() != hentPersonerRespons.getPerson().size()) {
                result.setSuccessful(false);
            } else {
                validerRespons(result, map, hentPersonerRespons);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (result.getStartTime() == 0l) {
                result.sampleStart();
                result.setSuccessful(false);
            }
        }

        System.out.println("called sampler done!");
        return result;

    }

    private Map<String, String> createWSConfigForV4() {
        Map<String, String> prop = new HashMap<String, String>();
        prop.put(WSHandlerConstants.SIG_KEY_ID, "X509KeyIdentifier");
        prop.put(WSHandlerConstants.SIGNATURE_PARTS, "{}{http://schemas.xmlsoap.org/soap/envelope/}Body;{}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp}");
        return prop;
    }

    private Map<String, Object[]> mapSsnTilConfig(HentPersonerForespoersel req, Object[] list) {
        Map<String, Object[]> map = new HashMap<String, Object[]>();
        for (Object data : list) {
            Object[] listOfData = (Object[]) data;
            String s = (String) listOfData[0];
            req.getPersonidentifikator().add(s);
            map.put(s, (Object[]) data);
        }
        return map;
    }

    private void setInformasjonsbehov(String behov, HentPersonerForespoersel req) {
        for (String behovStreng : behov.split(",")) {
            Informasjonsbehov b = Informasjonsbehov.valueOf(behovStreng);
            req.getInformasjonsbehov().add(b);
        }
    }

    private HentPersonerRespons executeHentPersoner(String ssn, String behov, String alias, SampleResult result, Oppslagstjeneste1405 kontaktinfoPort, HentPersonerForespoersel req) {
        HentPersonerRespons hentPersonerRespons;
        try {
            result.setSamplerData(alias + "@" + behov + ": " + ssn);
            result.sampleStart();
            hentPersonerRespons = kontaktinfoPort.hentPersoner(req);
        } finally {
            result.sampleEnd();
        }
        return hentPersonerRespons;
    }

    private void validerRespons(SampleResult result, Map<String, Object[]> map, HentPersonerRespons hentPersonerRespons) {
        int i = 0;
        for (Person p : hentPersonerRespons.getPerson()) {
            boolean havePostkasse = map.get(p.getPersonidentifikator())[2].equals("1");
            System.out.println(p.getPersonidentifikator());
            if (!p.getKontaktinformasjon().getEpostadresse().getValue().contains(p.getPersonidentifikator()))
                result.setSuccessful(false);

            System.out.println(p.getSikkerDigitalPostAdresse());
            if (havePostkasse) {
                result.setSuccessful(p.getSikkerDigitalPostAdresse() != null);
            } else {
                result.setSuccessful(p.getSikkerDigitalPostAdresse() == null);
            }

            if (result.isSuccessful()) {
                result.setResponseMessage(p.getKontaktinformasjon().getEpostadresse().getValue());
                result.setSuccessful(true);
            }


        }
    }
}
