package org.greenleaf.sample.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BusManager {

    private static Logger logger = LoggerFactory.getLogger(BusManager.class);

    public int getNumber() { 
       return 2;
    }

    public int comparre() {
        return super.hashCode();
    }
}