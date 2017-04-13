package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class IntegerToStackTest {

    @Test
    public void testExecute() throws Exception {

        Field field = new Field();
        field.addLine("a");

        try {
            new IntegerToStack().execute(new Stack<>(), field);
            fail();
        } catch (CommandException e) {
        }

        field.clear();
        field.addLine("0123456789");

        Stack<Integer> stack = new Stack<>();

        for(int i = 0; i < 10; ++i) {
            field.setX(i);
            new IntegerToStack().execute(stack, field);

            assertEquals("Bad IntegerToStack", i, (int)stack.pop());
        }
    }
}