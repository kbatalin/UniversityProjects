package net.kir55rus.util;

import org.junit.Test;

import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 04.03.16.
 */
public class FactoryTest {
    private final String configFileName = "factory.ini";

    @Test
    public void testGet() throws Exception {
        Factory factory = Factory.instance();

        String badClass = "#DFdfg03";
        try {
            factory.get(badClass);
            fail("Create bad class");
        } catch (FactoryException ex) {
        }

        InputStream stream = FactoryTest.class.getResourceAsStream(configFileName);
        Properties property = new Properties();
        property.load(stream);
        String goodClass = (String)property.stringPropertyNames().toArray()[0];

        Object obj = factory.get(goodClass);
        assertEquals("Bad type of object", property.get(goodClass), obj.getClass().getCanonicalName());
    }

    @Test
    public void testReloadConfig() throws Exception { //todo: add tests
        Factory factory = Factory.instance();
        factory.setConfigFile("/net/kir55rus/util/testConfig.ini");
        factory.reloadConfig();

        try {
            factory.get("testClass");
            fail("Create bad class");
        } catch (FactoryException ex) {
        }

        try {
            factory.setConfigFile("/net/kir55rus/util/tmpConfig.ini2");
            factory.reloadConfig();
            fail("Load bad config");
        } catch (FactoryException ex) {
        }

//        try { //todo: how?
//            factory.setConfigFile("/net/kir55rus/util/badConfig.ini");
//            factory.reloadConfig();
//            fail("Load bad config");
//        } catch (FactoryException ex) {
//        }
    }

    public static class testClass {
        private testClass() {
        }
    }
}