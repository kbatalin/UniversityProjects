package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.junit.Test;
import java.util.*;

/**
 * Created by kir55rus on 26.02.16.
 */
public class RandomTest {

    @Test
    public void testExecute() throws Exception {
        Stack<Integer> stack = new Stack<>();
        Field field = new Field();
        boolean[] arr = new boolean[]{false, false, false, false};

        while (hasFalse(arr)) {
            new Random().execute(stack, field);

            switch (field.getDirection()) {
                case UP: arr[0] = true; break;
                case RIGHT: arr[1] = true; break;
                case DOWN: arr[2] = true; break;
                case LEFT: arr[3] = true; break;
            }
        }
    }

    private boolean hasFalse(boolean[] arr) {
        for(boolean v : arr) {
            if(!v) {
                return true;
            }
        }

        return false;
    }
}