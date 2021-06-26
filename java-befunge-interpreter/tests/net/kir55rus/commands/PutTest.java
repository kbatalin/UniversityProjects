package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Assert;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 04.03.16.
 */
public class PutTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        try {
            new Put().execute(stack, field);
            Assert.fail();
        } catch (CommandException ex) {
        }

        stack.push(3);
        stack.push(2);
        stack.push(1);
        try {
            new Put().execute(stack, field);
            fail();
        } catch (CommandException ex) {
            Assert.assertEquals("Bad stack size 1", 3, stack.size());
            Assert.assertEquals("Bad stack 1.1", 1, (int) stack.pop());
            Assert.assertEquals("Bad stack 1.2", 2, (int) stack.pop());
            Assert.assertEquals("Bad stack 1.3", 3, (int) stack.pop());
        }

        field.addLine("qwerty");
        field.addLine("12345");

        stack.push(0);
        stack.push(6);
        stack.push(0);

        try {
            new Put().execute(stack, field);
            fail();
        } catch (CommandException ex) {
            Assert.assertEquals("Bad stack size 2", 3, stack.size());
            Assert.assertEquals("Bad stack 2.1", 0, (int) stack.pop());
            Assert.assertEquals("Bad stack 2.2", 6, (int) stack.pop());
            Assert.assertEquals("Bad stack 2.3", 0, (int) stack.pop());
        }

        stack.push(5);
        stack.push(2);
        stack.push(1);
        new Put().execute(stack, field);

        Assert.assertEquals("Bad stack size 3", 0, stack.size());
        Assert.assertEquals("Bad stack 3", 5, (int)field.getChar(2, 1));
    }
}