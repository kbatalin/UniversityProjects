package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Addition: Pop a and b, then push a+b
 */
public class Add implements Command {
    private static final Logger log = Logger.getLogger(Add.class);

    /**
     * Execute operation. If stack has only 1 argument, second operand will be 0 (zero)
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If stack is empty
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("Add");
        if(stack.empty()) {
            log.debug("Stack is empty");
            throw new CommandException();
        }

        Integer a = stack.pop();
        Integer b = stack.empty() ? 0 : stack.pop();

        stack.push(a + b);

        if(log.isTraceEnabled()) {
            log.trace(a + " + " + b + " = " + stack.peek());
        }
    }

}
