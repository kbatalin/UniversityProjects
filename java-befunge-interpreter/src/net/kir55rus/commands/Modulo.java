package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Modulo: Pop a and b, then push the remainder of the integer division of b/a.
 */
public class Modulo implements Command {
    private static final Logger log = Logger.getLogger(Modulo.class);

    /**
     * Execute operation. If stack has only 1 argument, one more operand will be 0 (zero)
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If stack is empty or if divider is 0 (zero)
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("Modulo");

        if(stack.empty()) {
            log.debug("Stack is empty");
            throw new CommandException();
        }

        Integer a = stack.pop();

        if(a == 0) {
            log.debug("Division by zero");
            stack.push(a);
            throw  new CommandException("Division by zero");
        }

        Integer b = stack.empty() ? 0 : stack.pop();

        stack.push(b % a);

        if(log.isTraceEnabled()) {
            log.trace(b + " % " + a + " = " + stack.peek());
        }
    }
}
