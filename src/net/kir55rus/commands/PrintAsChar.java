package net.kir55rus.commands;

import net.kir55rus.util.Field;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Pop value and output as ASCII character
 */
public class PrintAsChar implements Command {
    private static final Logger log = Logger.getLogger(PrintAsChar.class);

    /**
     * Execute operation. Print char from top of stack. If stack is empty, operation does nothing
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("PrintAsChar");
        if(!stack.isEmpty()) {
            System.out.print((char)stack.pop().intValue());
        }
    }
}
