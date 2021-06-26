package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;
import java.io.*;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class PrintAsCharTest {

    @Test
    public void testExecute() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));

        Stack<Integer> stack = new Stack<>();

        new PrintAsChar().execute(stack, new Field());

        stack.push((int)'a');
        stack.push((int)'b');
        new PrintAsChar().execute(stack, new Field());

        assertEquals("Bad printAsChar", "b", output.toString());
    }
}