package no.difi.kontaktregister.external.client.v4.sample;

import no.difi.kontaktinfo.external.client.cxf.JMeterSampler;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.junit.Test;

public class JmeterSampleTests {

    @Test
    public void parsingV4(){
        JMeterSampler jMeterSampler = new JMeterSampler();
        Arguments args = jMeterSampler.getDefaultParameters();
        JavaSamplerContext context = new JavaSamplerContext(args);
        jMeterSampler.runTest(context);
    }


    @Test
    public void parsingV3(){
        JMeterSampler jMeterSampler = new JMeterSampler();
        Arguments args = new Arguments();
        args.addArgument("Endpoint", "https://kontaktinfo-ws-yt2.difi.eon.no/kontaktinfo-external/ws-v3");
        args.addArgument("Data", "[[\"12121212345\",\"0\",\"0\"]]");
        args.addArgument("informasjonsbehov", "PERSON,SERTIFIKAT");
        args.addArgument("alias", "v3");
        args.addArgument("configureForV4", "true");
        JavaSamplerContext context = new JavaSamplerContext(args);
        jMeterSampler.runTest(context);
    }
}
