package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class InputValueTest {

    @Test
    public void testExecute() throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream("32".getBytes());
        System.setIn(input);

        Stack<Integer> stack = new Stack<>();
        new InputValue().execute(stack, new Field());

        assertEquals("Bad input read (stack size)", 1, stack.size());
        assertEquals("Bad input read", 32, (int)stack.pop());
    }
}