package no.difi.kontaktinfo.external.client.cxf;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.message.Message;
import org.apache.log4j.Logger;

import java.util.logging.Level;

/**
 * Logging inbound messages only when response code is not 200.
 */
public class ErrorLoggingInInterceptor extends LoggingInInterceptor {

    final static Logger LOGGER = Logger.getLogger(ErrorLoggingInInterceptor.class);


    public ErrorLoggingInInterceptor() {
        super();
    }

    /**
     * Only log errors. Ignore successful messages with response code 200.
     *
     * @param message to log
     * @throws Fault
     */
    public void handleMessage(Message message) throws Fault {

        if (isSuccessfulResponse(message)) {
            return;
        }
        super.handleMessage(message);
    }

    private boolean isSuccessfulResponse(Message message) {
        Integer responseCode = (Integer) message.get(Message.RESPONSE_CODE);
        return responseCode != null && responseCode.equals(200);
    }


    /**
     * Since only non-successful messages are logged, these are all errors and thus logged by error-logger.
     *
     * @param logger
     * @param message
     */
    @Override
    protected void log(java.util.logging.Logger logger, String message) {
        super.log(logger, message);
        if (logger != null && logger.isLoggable(Level.INFO)) {
            LOGGER.error(message);
        }
    }


}
