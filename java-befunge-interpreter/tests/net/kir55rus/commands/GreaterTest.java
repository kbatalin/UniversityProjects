package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class GreaterTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        try {
            new Greater().execute(stack, field);
            Assert.fail();
        } catch (CommandException ex) {
        }

        stack.push(1);
        new Greater().execute(stack, field);
        Assert.assertEquals("Bad answer", 0, (int)stack.pop());

        stack.push(1);
        stack.push(0);

        new Greater().execute(stack, field);

        Assert.assertEquals("Bad stack size", 1, stack.size());
        Assert.assertEquals("Bad answer", 1, (int)stack.peek()); //1>0

        stack.push(2);
        new Greater().execute(stack, field);

        Assert.assertEquals("Bad stack size", 1, stack.size());
        Assert.assertEquals("Bad answer", 0, (int)stack.pop());
    }
}