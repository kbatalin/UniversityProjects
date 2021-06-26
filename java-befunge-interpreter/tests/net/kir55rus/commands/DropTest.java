package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class DropTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();

        new Drop().execute(stack, new Field());

        stack.push(4);
        stack.push(3);

        new Drop().execute(stack, new Field());

        assertEquals("Bad drop", 1, stack.size());
        assertEquals("Bad drop", 4, (int)stack.pop());
    }
}