package net.kir55rus.commands;

import net.kir55rus.util.Direction;
import net.kir55rus.util.Field;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class HorizontalIfTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        field.setDirection(Direction.UP);
        new HorizontalIf().execute(stack, field);
        assertEquals("Bad HorizontalIf", Direction.RIGHT, field.getDirection());

        stack.push(4);
        new HorizontalIf().execute(stack, field);
        assertEquals("Bad HorizontalIf", Direction.LEFT, field.getDirection());

        stack.push(0);
        new HorizontalIf().execute(stack, field);
        assertEquals("Bad HorizontalIf", Direction.RIGHT, field.getDirection());
    }
}