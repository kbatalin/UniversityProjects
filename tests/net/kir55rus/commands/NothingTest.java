package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class NothingTest {

    @Test
    public void testExecute() throws Exception {
        new Nothing().execute(new Stack<>(), new Field());
    }
}