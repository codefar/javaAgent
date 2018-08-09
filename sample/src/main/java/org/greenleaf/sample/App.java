package org.greenleaf.sample;

import org.greenleaf.sample.bean.TransClass;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) {
        System.out.println("Hello World1!");
        int um = new TransClass().getNumber();
        System.out.println("return value is " + um);
    }
}
