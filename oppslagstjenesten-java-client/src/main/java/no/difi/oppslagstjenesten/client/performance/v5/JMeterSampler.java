package no.difi.oppslagstjenesten.client.performance.v5;


import com.cedarsoftware.util.io.JsonReader;
import no.difi.begrep.Kontaktinformasjon;
import no.difi.begrep.Person;
import no.difi.kontaktinfo.wsdl.oppslagstjeneste_16_02.Oppslagstjeneste1602;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._16_02.HentPersonerForespoersel;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._16_02.HentPersonerRespons;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._16_02.Informasjonsbehov;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._16_02.Oppslagstjenesten;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.log4j.Logger;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class JMeterSampler extends AbstractJavaSamplerClient implements Serializable {

    final static Logger LOGGER = Logger.getLogger(JMeterSampler.class);

    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("Endpoint", "http://eid-vag-admin.difi.local:10002/kontaktinfo-external/ws-v5");
//        defaultParameters.addArgument("Endpoint", "https://kontaktinfo-ws-yt2.difi.eon.no/kontaktinfo-external/ws-v4");
//        defaultParameters.addArgument("Data", "[[\"12121212345\",\"0\",\"0\"]]");
        defaultParameters.addArgument("Data", "[[\"23079419826\",\"0\",\"0\"]]");
        defaultParameters.addArgument("informasjonsbehov", Informasjonsbehov.PERSON + "," + Informasjonsbehov.KONTAKTINFO + "," + Informasjonsbehov.SERTIFIKAT);
        defaultParameters.addArgument("alias", "client_alias");
        defaultParameters.addArgument("paaVegneAv", "false");

        return defaultParameters;
    }

    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        String endpoint = javaSamplerContext.getParameter("Endpoint");
        String ssn = javaSamplerContext.getParameter("Data");
        String behov = javaSamplerContext.getParameter("informasjonsbehov");
        String alias = javaSamplerContext.getParameter("alias");

        boolean paaVegneAv = Boolean.TRUE.toString().equals(javaSamplerContext.getParameter("paaVegneAv"));

        final String inputData = String.format("%s,%s,%s,%s", endpoint, ssn, behov, alias);
        LOGGER.debug("called sampler " + inputData);

        SampleResult result = new SampleResult();
        try {
            OppslagstjenestenKlient oppslagstjenestenKlient = new OppslagstjenestenKlient(endpoint, alias);
            Oppslagstjeneste1602 kontaktinfoPort;
            Oppslagstjenesten ot = null;
            if(paaVegneAv){
                kontaktinfoPort = oppslagstjenestenKlient.getOppslagstjenestenWithSigningPaaVegneAv();
                ot = new Oppslagstjenesten();
                ot.setPaaVegneAv("");
            }else{
                kontaktinfoPort = oppslagstjenestenKlient.getOppslagstjenstePort();
            }

            HentPersonerForespoersel req = new HentPersonerForespoersel();
            setInformasjonsbehov(behov, req);

            Object[] list = (Object[]) JsonReader.jsonToJava(ssn);
//            JsonReader jsonReader = Json.createReader(ssn);
            final Map<String, Object[]> map = mapSsnTilConfig(req, list);


            HentPersonerRespons hentPersonerRespons = executeHentPersoner(ssn, behov, alias, result, kontaktinfoPort, req, ot);

            if (hentPersonerRespons == null || hentPersonerRespons.getPerson() == null) {
                result.setSuccessful(false);
                LOGGER.error("hentPersonerRespons is null or hentPersonerRespons.getPerson is null. hentPersonerRespons=" + hentPersonerRespons);
            } else if (map.size() != hentPersonerRespons.getPerson().size()) {
                result.setSuccessful(false);
                LOGGER.error("request map size is not equal to response size: " + map.size() + " vs " + hentPersonerRespons.getPerson().size());
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("valider respons: " + personToString(hentPersonerRespons));
                }
                validerRespons(result, map, hentPersonerRespons);
            }

        } catch (Exception e) {
            LOGGER.error(inputData, e);
        } finally {
            if (result.getStartTime() == 0l) {
                result.sampleStart();
                result.setSuccessful(false);
                LOGGER.error("Failed before sampler started with input: " + inputData);
            }
        }

        LOGGER.debug("called sampler done!");
        return result;

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

    private HentPersonerRespons executeHentPersoner(String ssn, String behov, String alias, SampleResult result, Oppslagstjeneste1602 kontaktinfoPort, HentPersonerForespoersel req, Oppslagstjenesten ot) {
        HentPersonerRespons hentPersonerRespons;
        try {
            result.setSamplerData(alias + "@" + behov + ": " + ssn);
            result.sampleStart();
            hentPersonerRespons = kontaktinfoPort.hentPersoner(req, ot);
        } finally {
            result.sampleEnd();
        }
        return hentPersonerRespons;
    }

    private void validerRespons(SampleResult result, Map<String, Object[]> map, HentPersonerRespons hentPersonerRespons) {
        for (Person p : hentPersonerRespons.getPerson()) {
            boolean havePostkasse = map.get(p.getPersonidentifikator())[2].equals("1");
            LOGGER.debug(p.getPersonidentifikator());
            if (p.getKontaktinformasjon() == null || p.getKontaktinformasjon().getEpostadresse() == null) {
                result.setSuccessful(false);
                LOGGER.error("Kontaktinformasjon is empty or epostadresse is empty for ssn=" + p.getPersonidentifikator() + ". User is probably not in the KKR database.");
            } else if (!p.getKontaktinformasjon().getEpostadresse().getValue().contains(p.getPersonidentifikator())) {
                result.setSuccessful(false);
                LOGGER.error("Epostadresse does not contain SSN: " + (p.getSikkerDigitalPostAdresse() != null ? p.getSikkerDigitalPostAdresse().toString() : null));
            }
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

    private String personToString(HentPersonerRespons hentPersonerRespons) {
        if (hentPersonerRespons == null || hentPersonerRespons.getPerson() == null) {
            return null;
        }
        String personar = "";
        for (Person person : hentPersonerRespons.getPerson()) {
            personar += "Person:[" + person.getPersonidentifikator() + ", " + kontaktinfoToString(person.getKontaktinformasjon()) + "]\n";
        }
        return personar;

    }

    private String kontaktinfoToString(Kontaktinformasjon kontaktinformasjon) {
        if (kontaktinformasjon == null) {
            return null;
        }

        return "," + kontaktinformasjon.getEpostadresse() != null ? kontaktinformasjon.getEpostadresse().getValue() : null + "," + kontaktinformasjon.getMobiltelefonnummer() != null ? kontaktinformasjon.getMobiltelefonnummer().getValue() : null;
    }
}
