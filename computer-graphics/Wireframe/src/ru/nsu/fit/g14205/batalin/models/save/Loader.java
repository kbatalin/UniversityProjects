package ru.nsu.fit.g14205.batalin.models.save;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by kir55rus on 05.03.17.
 */
public interface Loader {
    void load() throws IOException;
}
