package net.kir55rus.util;

import org.apache.log4j.Logger;

import java.io.*;
import java.util.*;

/**
 * Factory with Singleton and dynamic class load.
 * <pre>{@code
 * Factory factory = Factory.instance();
 * ClassName obj = (ClassName)factory.get("ClassName");
 * obj.method();
 * }</pre>
 */
public class Factory {
    private static final Logger log = Logger.getLogger(Factory.class);

    private static Factory factory = new Factory();
    private Properties property = new Properties();
    private String configFile = "/net/kir55rus/util/factory.ini";

    /**
     * Instance factory
     * @return reference to Factory
     */
    public static Factory instance() {
        return factory;
    }

    /**
     * Find class in config file (default: factory.ini), instance it and return object
     * @param key keyword for search class in config file
     * @return reference to Object
     * @throws FactoryException if class not found in config or cannot instance it
     */
    public Object get(String key) throws FactoryException {
        String className = property.getProperty(key);
        if (className == null) {
            log.debug("Class not found. Key: " + key);
            throw new FactoryException("Class not found");
        }

        try {
            log.trace("Try to instance class " + className);
            Class c = Class.forName(className);
            return c.newInstance();
        } catch (Exception ex) {
            log.debug("Cannot instance class " + className);
            throw new FactoryException(ex.getMessage());
        }
    }

    /**
     * Update config file in cache of factory
     * @throws FactoryException if file cannot be read or has syntax error
     */
    //todo: будет ли работать в многопоточной среде?
    public void reloadConfig() throws FactoryException {
        InputStream stream = Factory.class.getResourceAsStream(configFile);
        if (stream == null) {
            log.debug("Cannot find config file: " + configFile);
            throw new FactoryException(configFile + " not found");
        }

        try {
            log.debug("Load " + configFile);
            property.load(stream);
        } catch (Exception ex) {
            log.error("Cannot load config");
            throw new FactoryException(ex.getMessage());
        }
    }

    /**
     * Set new name of config file (default: factory.ini). File should be resource. eg: /net/kir55rus/util/factory.ini
     * @param file new config file
     */
    public void setConfigFile(String file) {
        configFile = file;
    }

    private Factory() {
        try {
            log.debug("Construct factory");
            reloadConfig();
        } catch (Exception e) {
            log.error("Factory cannot be construct");
        }
    }
}
