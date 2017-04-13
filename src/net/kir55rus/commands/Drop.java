package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Pop value from the stack and discard it
 */
public class Drop implements Command {
    private static final Logger log = Logger.getLogger(Drop.class);

    /**
     * Execute operation. If stack is empty, operation do nothing
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("Drop");

        if(!stack.empty()) {
            log.debug("Stack is empty");
            stack.pop();
        }
    }

}
