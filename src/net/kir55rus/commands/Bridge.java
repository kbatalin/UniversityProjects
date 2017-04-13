package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Trampoline: Skip next cell
 */
public class Bridge implements Command {
    private static final Logger log = Logger.getLogger(Bridge.class);

    /**
     * Execute operation.
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("Bridge");
        field.step();
    }
}
