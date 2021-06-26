package net.kir55rus.commands;

import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Start moving up
 */
public class Up implements Command {
    private static final Logger log = Logger.getLogger(Up.class);

    /**
     * Execute operation
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("Up");
        field.setDirection(Direction.UP);
    }
}
