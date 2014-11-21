package no.difi.kontaktinfo.external.client.cxf;

import org.apache.commons.io.IOUtils;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.message.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

/**
 * Created by kons-las on 14.11.2014.
 */
public class SOAPMessageInterceptor extends LoggingInInterceptor {
    List<String> list;

    public SOAPMessageInterceptor(List<String> list){

        this.list = list;
    }

    @Override
    public void handleMessage(Message message) throws Fault {

        if (list != null) {

            InputStream is = message.getContent(InputStream.class);

            if (is.markSupported()) {
                is.mark(1000);

                StringWriter writer = new StringWriter();
                try {
                    IOUtils.copy(is, writer, "UTF-8");
                    is.reset();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String theString = writer.toString();
                list.add(theString);
            } else {
                System.out.println("++++++++++++++++mark not supported +++++++++++");
            }
        }
    }


}
