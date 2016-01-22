package no.difi.oppslagstjenesten.performance.v5;

import no.difi.kontaktinfo.xsd.oppslagstjeneste._16_02.Informasjonsbehov;
import no.difi.oppslagstjenesten.client.performance.v5.JMeterSampler;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JmeterSampleTest {

    @Test
    public void parsingV5(){
        JMeterSampler jMeterSampler = new JMeterSampler();
        Arguments args = jMeterSampler.getDefaultParameters();
        JavaSamplerContext context = new JavaSamplerContext(args);
        final SampleResult sampleResult = jMeterSampler.runTest(context);
        assertResults(sampleResult);
    }

  @Test
    public void parsingV5WithVarslingsstatus(){
        JMeterSampler jMeterSampler = new JMeterSampler();
        JavaSamplerContext context = new JavaSamplerContext(getArgumentsWithVarslingsstatus());
        final SampleResult sampleResult = jMeterSampler.runTest(context);
        assertResults(sampleResult);
    }

    private Arguments getArgumentsWithVarslingsstatus() {
        Arguments arguments = new Arguments();
//        arguments.addArgument("Endpoint", "http://eid-vag-admin.difi.local:10002/kontaktinfo-external/ws-v5");
        arguments.addArgument("Endpoint", "https://kontaktinfo-ws-yt2.difi.eon.no/kontaktinfo-external/ws-v5");
//        arguments.addArgument("Data", "[[\"12121212345\",\"0\",\"0\"]]");
        arguments.addArgument("Data", "[[\"01010069277\",\"0\",\"0\"]]");
        arguments.addArgument("informasjonsbehov", Informasjonsbehov.PERSON + "," + Informasjonsbehov.KONTAKTINFO + "," + Informasjonsbehov.SERTIFIKAT +","+Informasjonsbehov.VARSLINGS_STATUS);
        arguments.addArgument("alias", "client_alias");
        arguments.addArgument("paaVegneAv", "false");
        return arguments;
    }

    private void assertResults(SampleResult sampleResult) {
        assertNotNull(sampleResult);
        assertTrue(sampleResult.isSuccessful());
    }

}
