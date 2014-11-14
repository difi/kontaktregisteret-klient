package no.difi.kontaktregister.external.client.v4.sample;

import no.difi.begrep.Person;
import no.difi.kontaktinfo.external.client.cxf.JMeterSampler;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JmeterSampleTests {

    @Test
    public void parsingV4(){
        JMeterSampler jMeterSampler = new JMeterSampler();
        Arguments args = jMeterSampler.getDefaultParameters();
        JavaSamplerContext context = new JavaSamplerContext(args);
        final SampleResult sampleResult = jMeterSampler.runTest(context);
        assertResults(sampleResult);
    }

    private void assertResults(SampleResult sampleResult) {
        assertNotNull(sampleResult);
        assertTrue(sampleResult.isSuccessful());
    }


    @Test
    public void parsingV3(){
        JavaSamplerContext context = new JavaSamplerContext(setupV3Arguments());
        final SampleResult sampleResult = new JMeterSampler().runTest(context);
        assertResults(sampleResult);
    }

    private Arguments setupV3Arguments() {
        Arguments args = new Arguments();
        args.addArgument("Endpoint", "https://kontaktinfo-ws-yt2.difi.eon.no/kontaktinfo-external/ws-v3");
        args.addArgument("Data", "[[\"01010001419\",\"0\",\"1\"]]");
        args.addArgument("informasjonsbehov", "PERSON,SERTIFIKAT,SIKKER_DIGITAL_POST,KONTAKTINFO");
        args.addArgument("alias", "yt2-v3");
        args.addArgument("configureForV4", "false");
        return args;
    }

}
