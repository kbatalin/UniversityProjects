package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class SubtractTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        try {
            new Subtract().execute(stack, field);
            Assert.fail();
        } catch (CommandException ex) {
        }

        stack.push(1);
        new Subtract().execute(stack, field);
        Assert.assertEquals("Bad answer in Subtract", -1, (int)stack.pop());

        stack.push(1);
        stack.push(4);

        try {
            new Subtract().execute(stack, field);

            Assert.assertEquals("Bad stack size in Subtract", 1, stack.size());
            Assert.assertEquals("Bad answer in Subtract", -3, (int)stack.pop());
        } catch (Exception ex) {
            Assert.fail();
        }
    }
}