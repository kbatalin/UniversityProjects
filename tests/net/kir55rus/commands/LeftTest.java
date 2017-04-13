package net.kir55rus.commands;

import net.kir55rus.util.Direction;
import net.kir55rus.util.Field;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class LeftTest {

    @Test
    public void testExecute() throws Exception {
        Field field = new Field();
        field.setDirection(Direction.RIGHT);

        new Left().execute(new Stack<>(), field);

        assertEquals("Bad left", Direction.LEFT, field.getDirection());
    }
}