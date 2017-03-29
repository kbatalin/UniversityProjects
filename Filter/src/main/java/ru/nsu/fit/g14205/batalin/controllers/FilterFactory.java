package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.filters.Filter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Created by kir55rus on 28.03.17.
 */
public class FilterFactory {
    private Map<String, Supplier<Filter>> creators = new HashMap<>();

    public void add(String name, Supplier<Filter> creator) {
        creators.put(name, creator);
    }

    public Filter get(String name) {
        Supplier<Filter> creator = creators.get(name);
        if (creator == null) {
            return null;
        }

        return creator.get();
    }
}
