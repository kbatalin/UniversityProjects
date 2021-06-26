package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * A "put" call (a way to store a value for later use)
 */
public class Put implements Command {
    private static final Logger log = Logger.getLogger(Put.class);

    /**
     * Execute operation. Pop y, x, and v, then change the character at (x,y) in the program to the character with ASCII value v
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If stack has less 3 elements or coordinates are bad
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("Put");
        if(stack.size() < 3) {
            log.debug("Stack is small");
            throw new CommandException();
        }

        Integer y = stack.pop();

        if(y < 0 || y >= field.height()) {
            log.debug("Bad Y: " + y);
            stack.push(y);
            throw  new CommandException("Bad crds");
        }

        Integer x = stack.pop();

        if(x < 0 || x >= field.width()) {
            log.debug("Bad X: " + x);
            stack.push(x);
            stack.push(y);
            throw  new CommandException("Bad crds");
        }

        char c =  (char)stack.pop().intValue();

        field.setChar(x, y, c);

        log.trace("Put '" + c + "'");
    }
}
