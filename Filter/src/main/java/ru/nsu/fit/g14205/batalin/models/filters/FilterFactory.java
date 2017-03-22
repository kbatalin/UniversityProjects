package ru.nsu.fit.g14205.batalin.models.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by kir55rus on 22.03.17.
 */
public class FilterFactory {
    private Properties filters = new Properties();

    public FilterFactory() throws IOException {
        filters.load(getClass().getClassLoader().getResourceAsStream("configs/filters.cfg"));
    }

    public Filter initFilter(String filterName) throws IllegalArgumentException {
        try {
            String className = filters.getProperty(filterName);
            Class filterClass = Class.forName(className);
            return (Filter) filterClass.newInstance();
        } catch (Exception e) {
            throw new IllegalArgumentException("Bad filter");
        }
    }
}
