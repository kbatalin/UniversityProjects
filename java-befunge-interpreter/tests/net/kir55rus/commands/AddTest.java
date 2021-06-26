package net.kir55rus.commands;

import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;
import net.kir55rus.util.*;

public class AddTest {
    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        try {
            new Add().execute(stack, field);
            fail();
        } catch (CommandException ex) {
        }

        stack.push(2);

        new Add().execute(stack, field);
        assertEquals("Bad answer", 2, (int)stack.peek());

        stack.push(4);
        new Add().execute(stack, field);

        assertEquals("Bad stack size", 1, stack.size());
        assertEquals("Bad answer", 6, (int)stack.pop());
    }
}
