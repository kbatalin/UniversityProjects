package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Logical NOT: Pop a value. If the value is zero, push 1; otherwise, push zero.
 */
public class Not implements Command {
    private static final Logger log = Logger.getLogger(Not.class);

    /**
     * Execute operation. If stack is empty, operation just push 1 in stack
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("Not");
        stack.push((stack.empty() || stack.pop() == 0) ? 1 : 0);
    }
}
