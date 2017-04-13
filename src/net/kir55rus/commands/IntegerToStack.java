package net.kir55rus.commands;

import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Push int number on the stack
 */
public class IntegerToStack implements Command {
    private static final Logger log = Logger.getLogger(IntegerToStack.class);

    /**
     * Execute operation. Push int number on the stack. Actual character convert to int and push to stack
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If character isn't int
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("IntegerToStack");
        Character c = field.getChar();
        if(!Character.isDigit(c)) {
            log.debug("Character isn't int");
            throw new CommandException();
        }

        stack.push(Integer.parseInt(c.toString()));
    }
}
