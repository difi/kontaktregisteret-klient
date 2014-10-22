package no.difi.kontaktregister.external.client.v4.sample;

import no.difi.kontaktinfo.external.client.cxf.JMeterSampler;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.junit.Test;

public class JmeterSampleTests {

    @Test
    public void parseing(){
        JMeterSampler jMeterSampler = new JMeterSampler();
        Arguments args = jMeterSampler.getDefaultParameters();
        JavaSamplerContext context = new JavaSamplerContext(args);
        jMeterSampler.runTest(context);
    }
}
