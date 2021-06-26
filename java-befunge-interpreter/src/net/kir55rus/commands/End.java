package net.kir55rus.commands;

import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * End program
 */
public class End implements Command {
    private static final Logger log = Logger.getLogger(End.class);

    /**
     * Execute operation. Set execution of program to stop
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("End");
        field.setExecution(false);
    }

}
