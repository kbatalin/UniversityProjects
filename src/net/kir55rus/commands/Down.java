package net.kir55rus.commands;

import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Start moving down
 */
public class Down implements Command {
    private static final Logger log = Logger.getLogger(Down.class);

    /**
     * Execute operation
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("Down");
        field.setDirection(Direction.DOWN);
    }
}
