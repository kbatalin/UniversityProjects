package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 04.03.16.
 */
public class GetTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        try {
            new Get().execute(stack, field);
            Assert.fail();
        } catch (CommandException ex) {
        }

        stack.push(1);
        stack.push(2);
        new Get().execute(stack, field);
        Assert.assertEquals("Bad stack size 1", 1, stack.size());
        Assert.assertEquals("Bad stack 1", 0, (int)stack.pop());

        field.addLine("qwerty");
        field.addLine("12345");

        stack.push(2);
        stack.push(1);

        new Get().execute(stack, field);

        Assert.assertEquals("Bad stack size 2", 1, stack.size());
        Assert.assertEquals("Bad stack 2", (int)'3', (int)stack.pop());
    }
}