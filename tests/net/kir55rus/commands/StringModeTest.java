package net.kir55rus.commands;

import net.kir55rus.util.Direction;
import net.kir55rus.util.Field;
import org.junit.Test;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class StringModeTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        field.addLine("44");
        field.setDirection(Direction.RIGHT);

        try {
            new StringMode().execute(stack, field);
            fail("Bad StringMode exception");
        } catch (CommandException ex) {
        }

        field.addLine("\"abcd\"ef");
        field.setY(1);

        new StringMode().execute(stack, field);

        assertEquals("Bad StringMode", 4, stack.size());

        assertEquals("Bad StringMode", 'd', (int)stack.pop());
        assertEquals("Bad StringMode", 'c', (int)stack.pop());
        assertEquals("Bad StringMode", 'b', (int)stack.pop());
        assertEquals("Bad StringMode", 'a', (int)stack.pop());
    }
}