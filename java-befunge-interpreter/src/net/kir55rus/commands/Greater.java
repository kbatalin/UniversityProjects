package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Greater than
 */
public class Greater implements Command {
    private static final Logger log = Logger.getLogger(Greater.class);

    /**
     * Execute operation. Pop a and b, then push 1 if b&#62;a, otherwise zero. If stack has only one element, second will be 0 (zero)
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If stack is empty
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("Greater");
        if(stack.empty()) {
            log.debug("Stack is empty");
            throw new CommandException();
        }

        Integer a = stack.pop();
        Integer b = stack.empty() ? 0 : stack.pop();

        stack.push(b > a ? 1 : 0);

        if(log.isTraceEnabled()) {
            log.trace(b + " > " + a + " = " + stack.peek());
        }
    }
}
