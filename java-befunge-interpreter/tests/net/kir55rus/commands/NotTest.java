package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class NotTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();

        new Not().execute(stack, new Field());
        assertEquals("Bad Not", 1, (int)stack.pop());

        stack.push(4);
        stack.push(3);

        new Not().execute(stack, new Field());

        assertEquals("Bad Not", 2, stack.size());
        assertEquals("Bad Not", 0, (int)stack.peek());

        new Not().execute(stack, new Field());

        assertEquals("Bad Not", 2, stack.size());
        assertEquals("Bad Not", 1, (int)stack.peek());
    }
}