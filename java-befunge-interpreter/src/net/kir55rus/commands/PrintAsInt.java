package net.kir55rus.commands;

import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Pop value and output as an integer
 */
public class PrintAsInt implements Command {
    private static final Logger log = Logger.getLogger(PrintAsInt.class);

    /**
     * Execute operation. Print int from top of stack. If stack is empty, operation print 0 (zero)
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
        log.trace("PrintAsInt");
        System.out.print(stack.empty() ? 0 : stack.pop());
    }
}
