package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;
import java.util.*;

import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class InputCharacterTest {

    @Test
    public void testExecute() throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream("ag".getBytes());
        System.setIn(input);

        Stack<Integer> stack = new Stack<>();
        new InputCharacter().execute(stack, new Field());

        assertEquals("Bad input read (stack size)", 1, stack.size());
        assertEquals("Bad input read", (int)'a', (int)stack.pop());
    }
}