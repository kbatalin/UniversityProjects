package net.kir55rus.commands;

import net.kir55rus.util.Direction;
import net.kir55rus.util.Field;
import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class VerticalIfTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();

        field.setDirection(Direction.RIGHT);
        new VerticalIf().execute(stack, field);
        assertEquals("Bad VerticalIf", Direction.DOWN, field.getDirection());

        stack.push(4);
        new VerticalIf().execute(stack, field);
        assertEquals("Bad VerticalIf", Direction.UP, field.getDirection());

        stack.push(0);
        new VerticalIf().execute(stack, field);
        assertEquals("Bad VerticalIf", Direction.DOWN, field.getDirection());
    }
}