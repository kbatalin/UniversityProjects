package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Subtraction
 */
public class Subtract implements Command {
    private static final Logger log = Logger.getLogger(Subtract.class);

    /**
     * Execute operation. Pop a and b, then push b-a. If stack has only 1 argument, one more operand will be 0 (zero)
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If stack is empty
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("Subtract");
        if(stack.empty()) {
            log.debug("Stack is small");
            throw new CommandException();
        }

        Integer a = stack.pop();
        Integer b = stack.empty() ? 0 : stack.pop();
        stack.push(b - a);

        if(log.isTraceEnabled()) {
            log.trace(b + " - " + a + " = " + stack.peek());
        }
    }
}
