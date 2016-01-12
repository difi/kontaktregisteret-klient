package no.difi.oppslagstjenesten.performance.v5;

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

    private void assertResults(SampleResult sampleResult) {
        assertNotNull(sampleResult);
        assertTrue(sampleResult.isSuccessful());
    }

}
