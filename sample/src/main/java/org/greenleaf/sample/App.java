package org.greenleaf.sample;

import org.greenleaf.sample.bean.BusManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hello world!
 */
public class App {

    private static Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        logger.info("Hello World");
        logger.info("return value is {} ", new BusManager().getNumber());
    }
}
