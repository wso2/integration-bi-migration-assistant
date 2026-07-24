package javasamples;

import org.apache.synapse.MessageContext;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Sample class mediator matching the {@code <class name="javasamples.GreetMediator">} in the
 * HelloClass API. Reads the {@code lang} and {@code greeting} properties off the message context and
 * writes a composed greeting back.
 */
public class GreetMediator extends AbstractMediator {

    private static final Log log = LogFactory.getLog(GreetMediator.class);

    public boolean mediate(MessageContext mc) {
        String lang = (String) mc.getProperty("lang");
        String greeting = (String) mc.getProperty("greeting");

        String prefix = "en".equals(lang) ? "Hello" : "Hi";
        String message = prefix + ", " + greeting;
        log.info("Composed greeting: " + message);

        mc.setProperty("greetingMessage", message);
        return true;
    }
}
