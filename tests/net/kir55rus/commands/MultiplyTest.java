package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class MultiplyTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        try {
            new Multiply().execute(stack, field);
            fail();
        } catch (CommandException ex) {
        }

        stack.push(3);
        new Multiply().execute(stack, field);
        assertEquals("Bad answer", 0, (int)stack.pop());

        stack.push(2);
        stack.push(4);

        try {
            new Multiply().execute(stack, field);

            assertEquals("Bad stack size", 1, stack.size());
            assertEquals("Bad answer", 8, (int)stack.pop());
        } catch (Exception ex) {
            fail();
        }
    }
}