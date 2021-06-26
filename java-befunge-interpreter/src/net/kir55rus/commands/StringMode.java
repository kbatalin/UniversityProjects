package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Start string mode
 */
public class StringMode implements Command {
    private static final Logger log = Logger.getLogger(StringMode.class);

    /**
     * Execute operation. Push each character's ASCII value all the way up to the next "
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     * @throws CommandException If position hasn't symbol "
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) throws CommandException {
        log.trace("StringMode");
        if(field.getChar() != '"') {
            log.debug("Bad position");
            throw new CommandException();
        }

        field.step();
        int c;
        while ((c = field.getChar()) != '"') {
            stack.push(c);
            field.step();
        }
    }
}
