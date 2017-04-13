package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Integer division: Pop a and b, then push b/a, rounded down.
 */
public class Divide implements Command {
    private static final Logger log = Logger.getLogger(Divide.class);

    /**
     * Execute operation. If stack has only 1 argument, one more operand will be 0 (zero)
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If stack is empty or if divider is 0 (zero)
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("Divide");
        if(stack.empty()) {
            log.debug("Stack is empty");
            throw new CommandException();
        }

        Integer a = stack.pop();

        if(a == 0) {
            log.trace("Division by zero");
            new InputValue().execute(stack, field);
            return;
        }

        Integer b = stack.empty() ? 0 : stack.pop();
        stack.push(b / a);

        if(log.isTraceEnabled()) {
            log.trace(b + " / " + a + " = " + stack.peek());
        }
    }
}
