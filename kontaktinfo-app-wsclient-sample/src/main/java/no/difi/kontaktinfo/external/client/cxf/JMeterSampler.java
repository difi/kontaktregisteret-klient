package no.difi.kontaktinfo.external.client.cxf;


import no.difi.kontaktinfo.wsdl.oppslagstjeneste_14_05.Oppslagstjeneste1405;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPrintSertifikatForespoersel;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.ws.security.handler.WSHandlerConstants;
import org.bouncycastle.util.Strings;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JMeterSampler extends AbstractJavaSamplerClient implements Serializable {


    @Override
    public Arguments getDefaultParameters() {
        Arguments defaultParameters = new Arguments();
        defaultParameters.addArgument("Endpoint", "http://localhost:8888/kontaktinfo-web-external/ws-v4");
        defaultParameters.addArgument("ssn", "12121212345");
        defaultParameters.addArgument("informasjonsbehov", "PERSON,SERTIFIKAT");
        defaultParameters.addArgument("alias", "client_alias");

        return defaultParameters;
    }
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {

        String endpoint = javaSamplerContext.getParameter("Endpoint");
        String ssn = javaSamplerContext.getParameter("ssn");
        String behov = javaSamplerContext.getParameter("informasjonsbehov");
        String alias = javaSamplerContext.getParameter("alias");

        System.out.println("called sampler " + String.format("%s,%s,%s,%s", endpoint, ssn, behov, alias) );

        SampleResult result = new SampleResult();
        try{
            Map<String, String> prop= new HashMap<String, String>();
            prop.put(WSHandlerConstants.SIG_KEY_ID, "X509KeyIdentifier");
            prop.put(WSHandlerConstants.SIGNATURE_PARTS, "{}{http://schemas.xmlsoap.org/soap/envelope/}Body;{}{http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd}Timestamp}");
            OppslagstjenestenKlient oppslagstjenestenKlient = new OppslagstjenestenKlient(endpoint, alias, prop);
            Oppslagstjeneste1405 kontaktinfoPort = oppslagstjenestenKlient.getOppslagstjenstePort();

            result.sampleStart();
            kontaktinfoPort.hentPrintSertifikat(new HentPrintSertifikatForespoersel());
            result.sampleEnd();

            result.setSuccessful(true);
        }catch(Exception e){
            e.printStackTrace();
            result.setSuccessful(false);
        }

        System.out.println("called sampler done!");
        return result;

    }
}
