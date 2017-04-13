package net.kir55rus.commands;

import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Start moving in a random cardinal direction
 */
public class Random implements Command {
    private static final Logger log = Logger.getLogger(Random.class);

    /**
     * Execute operation
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("Random");

        java.util.Random rnd = new java.util.Random();
        switch (rnd.nextInt(4)) {
            case 0: new Up().execute(stack, field); break;
            case 1: new Right().execute(stack, field); break;
            case 2: new Down().execute(stack, field); break;
            case 3: new Left().execute(stack, field); break;
        }
    }
}
