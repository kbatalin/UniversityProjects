package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class DivideTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        try {
            new Divide().execute(stack, field);
            Assert.fail();
        } catch (CommandException ex) {
        }

        stack.push(1);
        new Divide().execute(stack, field);
        Assert.assertEquals("Bad stack size #1", 1, stack.size());
        Assert.assertEquals("Bad answer #1", 0, (int)stack.pop());

        stack.push(1);
        stack.push(0);
        ByteArrayInputStream input = new ByteArrayInputStream("5".getBytes());
        System.setIn(input);

        new Divide().execute(stack, field);
        assertEquals("lost args #2", 2, stack.size());
        Assert.assertEquals("Bad answer #2", 5, (int)stack.peek());

        stack.push(4);
        stack.push(2);

        new Divide().execute(stack, field);

        Assert.assertEquals("Bad stack size", 3, stack.size());
        Assert.assertEquals("Bad answer", 2, (int)stack.pop());
    }
}