package no.difi.oppslagstjenesten.jmeter;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.difi.begrep.Kontaktinformasjon;
import no.difi.begrep.Person;
import no.difi.kontaktinfo.external.client.cxf.WSS4JInterceptorHelper;
import no.difi.kontaktinfo.wsdl.oppslagstjeneste_14_05.Oppslagstjeneste1405;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPersonerForespoersel;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPersonerRespons;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.Informasjonsbehov;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testbeans.TestBean;
import org.apache.jorphan.logging.LoggingManager;
import org.apache.log.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class KontaktRegisterSampler extends AbstractSampler implements TestBean {
    private static final Logger log = LoggingManager.getLoggerForClass();

    private static final long serialVersionUID = 2323L;

    private transient String protocol;

    private transient String host;

    private transient String port;

    private transient String path;

    private transient String user;

    private transient String password;

    private transient String data;

    private static Oppslagstjeneste1405 kontaktinfoPort;
    private static final String TEST_SSN_1 = "02018090573";

    private String sendSoapMsg(String uri, String msg) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode list = mapper.readTree(msg);

            String serviceAddress =uri;

            HashMap<String, List<Boolean>> map = new HashMap<String, List<Boolean>>();
            Iterator<JsonNode> it = list.iterator();

            while(it.hasNext())
            {
                JsonNode line = it.next();
                String ssn = line.get(0).asText();
                boolean skip = intToBool(line.get(1).asInt());
                boolean hasPostbox = intToBool(line.get(2).asInt());

                map.put(ssn, Arrays.asList(skip, hasPostbox));
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

            HentPersonerForespoersel request = new HentPersonerForespoersel();
            request.getPersonidentifikator().addAll(map.keySet());
            request.getInformasjonsbehov().add(Informasjonsbehov.SIKKER_DIGITAL_POST);



            HentPersonerRespons response = kontaktinfoPort.hentPersoner(request);

            for(Person person : response.getPerson()){

                List<Boolean> booleans = map.get(person.getPersonidentifikator());
                boolean skip = booleans.get(0);
                boolean hasPostbox = booleans.get(1);

                if(skip){
                    Kontaktinformasjon personsKontaktinfo = person.getKontaktinformasjon();
                    if(personsKontaktinfo == null)
                        ;
                    else
                        return "error user should be nonexistent email is " + personsKontaktinfo.getEpostadresse();
                }else{
                    Kontaktinformasjon kontaktinformasjon = person.getKontaktinformasjon();
                    if(kontaktinformasjon != null && kontaktinformasjon.getEpostadresse() != null && kontaktinformasjon.getEpostadresse().getValue().startsWith(person.getPersonidentifikator())){
                        if(hasPostbox){
                            if(person.getSikkerDigitalPostAdresse() != null && person.getSikkerDigitalPostAdresse().getPostkasseadresse() != null)
                                ;
                            else
                                return "error could not get SDP " + person.getPersonidentifikator();
                        }else
                            ;

                    }else
                        return "error wrong or missing email " + person.getPersonidentifikator();
                }
            }

        }catch(Throwable e){
            e.printStackTrace();
            return "exception " + e.getMessage() + "\n" + e.toString();
        }

        return "ok";
    }

    private boolean intToBool(int i) {
        if(i == 0)
            return false;
        else
            return true;
    }


    public SampleResult sample(Entry e) {


        log.debug("sampling");

        SampleResult res = new SampleResult();
        res.setSampleLabel(getName());
        res.setSamplerData(toString());
        res.setDataType(SampleResult.TEXT);
        // Bug 31184 - make sure encoding is specified
        res.setDataEncoding("utf-8");

        res.sampleStart();

        String uri = getProtocol() + "://" + getHost() + ":" + getPort() + getPath();
        System.out.println("uri: " + uri);
        System.out.println("request" + getData());

        res.setSuccessful(true);
        res.setResponseCode("200");
        res.setResponseMessage("OK");
        String result;
        try {
            result = sendSoapMsg(uri, getData());
            if(result != null)
                res.setResponseData(result);
        } catch (Exception e1) {
            res.setSuccessful(false);
            res.setResponseCode("400");
            res.setResponseMessage("Bad request");
            e1.printStackTrace();
            System.out.println("Exception while sending Soap request:\n" + e1);
            res.setResponseData("Exception while sending Soap request:\n" + e1);
        }

        res.sampleEnd();
        return res;
    }


    public String toString() {
        return getData();
    }


    // getters & setters
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
