package net.kir55rus.commands;

import net.kir55rus.util.Direction;
import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Duplicate value on top of the stack
 */
public class Dup implements Command {
    private static final Logger log = Logger.getLogger(Dup.class);

    /**
     * Execute operation. If stack is empty, operation just push 0 in stack
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("Dup");
        stack.push(stack.empty() ? 0 : stack.peek()); //0 or 0,0 ?
    }

}
