package net.kir55rus.commands;

import net.kir55rus.util.Direction;
import net.kir55rus.util.Field;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class RightTest {

    @Test
    public void testExecute() throws Exception {
        Field field = new Field();
        field.setDirection(Direction.LEFT);

        new Right().execute(new Stack<>(), field);

        assertEquals("Bad right", Direction.RIGHT, field.getDirection());
    }
}