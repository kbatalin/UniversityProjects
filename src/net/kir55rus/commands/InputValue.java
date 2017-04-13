package net.kir55rus.commands;

import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Ask user for a number and push it
 */
public class InputValue implements Command {
    private static final Logger log = Logger.getLogger(InputValue.class);

    /**
     * Execute operation. Ask user for a number and push it. If user enter nothing, 0 (zero) will be pushed to stack
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("InputValue");
        stack.push(Parser.parseIntFromConsole("\nEnter int: "));
    }
}
