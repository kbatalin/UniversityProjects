package net.kir55rus.commands;

import net.kir55rus.util.*;
import java.util.*;

/**
 * Base command interface
 */
public interface Command {

    /**
     * Execute operation.
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException Dividers maybe throw it
     */
    public void execute(Stack<Integer> stack, Field field) throws CommandException;
}
