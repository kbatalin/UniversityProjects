package net.kir55rus.commands;

import net.kir55rus.util.Direction;
import net.kir55rus.util.Field;
import java.util.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class BridgeTest {
    @Test
    public void testExecute() throws Exception {
        Field field = new Field();
        Stack<Integer> stack = new Stack<>();

        field.setDirection(Direction.RIGHT);
        field.addLine("#+@");

        new Bridge().execute(stack, field);

        assertEquals("Jump don't work", 1, field.x());
    }
}