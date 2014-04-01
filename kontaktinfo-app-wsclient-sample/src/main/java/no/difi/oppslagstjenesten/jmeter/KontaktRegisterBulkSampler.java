package no.difi.oppslagstjenesten.jmeter;


import no.difi.kontaktinfo.external.client.cxf.WSS4JInterceptorHelper;
import no.difi.kontaktinfo.wsdl.oppslagstjeneste_14_05.Oppslagstjeneste1405;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPersonerForespoersel;
import no.difi.kontaktinfo.xsd.oppslagstjeneste._14_05.HentPersonerRespons;
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

/**
 *
 */
public class KontaktRegisterBulkSampler extends AbstractSampler implements TestBean {
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

    private String sendSoapMsg(String uri, String msg) {
        try{

            String[] data = msg.split(",");
            String serviceAddress =uri;


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
            for(String s : data){
                request.getPersonidentifikator().add(s);
            }


            HentPersonerRespons response = kontaktinfoPort.hentPersoner(request);

            if(response.getPerson().size() == data.length)
                return "ok";
            else
                return "error";

        }catch(Throwable e){
            e.printStackTrace();
            return "exception " + e.getMessage() + "\n" + e.toString();
        }
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
