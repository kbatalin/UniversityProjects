package ru.nsu.fit.g14205.batalin.models;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by kir55rus on 05.03.17.
 */
public interface ILoader {
    void load() throws IOException;
    IPropertiesModel getPropertiesModel();
    IFieldModel getFieldModel();
}
