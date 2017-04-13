package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class SwapTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();

        try {
            new Swap().execute(stack, new Field());
            fail("Swap empty stack");
        } catch (CommandException ex) {
        }

        stack.push(1);
        new Swap().execute(stack, new Field());
        assertEquals("Bad swap #1", 0, (int)stack.pop());
        assertEquals("Bad swap #1", 1, (int)stack.pop());

        stack.push(1);
        stack.push(4);

        new Swap().execute(stack, new Field());

        assertEquals("Bad swap", 2, stack.size());
        assertEquals("Bad swap", 1, (int)stack.pop());
        assertEquals("Bad swap", 4, (int)stack.pop());
    }
}