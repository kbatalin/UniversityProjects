package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class ModuloTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        try {
            new Modulo().execute(stack, field);
            Assert.fail();
        } catch (CommandException ex) {
        }

        stack.push(1);
        new Modulo().execute(stack, field);
        assertEquals("lost args #1", 1, stack.size());
        Assert.assertEquals("Bad stack size", 0, (int)stack.peek());

        stack.push(0);
        try {
            new Modulo().execute(stack, field);
            Assert.fail("Modulo by zero");
        } catch (CommandException ex) {
            assertEquals("lost args #2", 2, stack.size());
        }

        stack.push(4);
        stack.push(2);

        new Modulo().execute(stack, field);

        Assert.assertEquals("Bad stack size", 3, stack.size());
        Assert.assertEquals("Bad answer", 0, (int)stack.pop());

        stack.push(5);
        stack.push(3);
        new Modulo().execute(stack, field);
        Assert.assertEquals("Bad answer", 2, (int)stack.pop());
    }
}