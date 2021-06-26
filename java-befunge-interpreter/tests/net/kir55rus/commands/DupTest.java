package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class DupTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();

        new Dup().execute(stack, new Field());
        assertEquals("Bad empty dup", 0, (int)stack.pop());

        stack.push(4);

        new Dup().execute(stack, new Field());

        assertEquals("Bad dup", 2, stack.size());
        assertEquals("Bad dup", 4, (int)stack.pop());
        assertEquals("Bad dup", 4, (int)stack.pop());
    }
}