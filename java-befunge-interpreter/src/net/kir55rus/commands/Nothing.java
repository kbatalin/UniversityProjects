package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * No-op. Does nothing
 */
public class Nothing implements Command {
    private static final Logger log = Logger.getLogger(Nothing.class);

    /**
     * Execute operation. Operation does nothing
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("Nothing");
    }
}
