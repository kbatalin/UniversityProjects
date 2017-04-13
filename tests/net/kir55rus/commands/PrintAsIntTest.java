package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class PrintAsIntTest {

    @Test
    public void testExecute() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Stack<Integer> stack = new Stack<>();

        new PrintAsInt().execute(stack, new Field());
        assertEquals("Bad PrintAsInt from empty stack", "0", output.toString());

        output.reset();
        stack.push(1);
        stack.push(2);
        new PrintAsInt().execute(stack, new Field());

        assertEquals("Bad PrintAsInt", "2", output.toString());
    }
}