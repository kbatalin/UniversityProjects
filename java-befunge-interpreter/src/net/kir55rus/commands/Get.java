package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * A "get" call (a way to retrieve data in storage)
 */
public class Get implements Command {
    private static final Logger log = Logger.getLogger(Get.class);

    /**
     * Execute operation. Pop y and x, then push ASCII value of the character at that position in the program. If (x,y) is out of bounds, push 0.
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If stack has less 2 elements
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("Get");
        if(stack.size() < 2) {
            log.debug("Stack is empty");
            throw new CommandException();
        }

        Integer y = stack.pop();
        Integer x = stack.pop();

        if(y < 0 || y >= field.height() || x < 0 || x >= field.width()) {
            log.debug("Coordinates not from field");
            stack.push(0);
        } else {
            stack.push((int) field.getChar(x, y));
        }
    }
}
