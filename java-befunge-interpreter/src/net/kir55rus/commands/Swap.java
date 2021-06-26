package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Swap two values on top of the stack
 */
public class Swap implements Command {
    private static final Logger log = Logger.getLogger(Swap.class);

    /**
     * Execute operation. If stack is empty, operation just push 0 in stack
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If stack is empty
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("Swap");
        if(stack.empty()) { //todo: or 0 swap 0?
            log.debug("Stack is empty");
            throw new CommandException();
        }

        Integer a = stack.pop();
        Integer b = stack.empty() ? 0 : stack.pop();

        stack.push(a);
        stack.push(b);
    }
}
