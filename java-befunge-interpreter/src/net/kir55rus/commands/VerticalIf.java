package net.kir55rus.commands;

import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Move top or down
 */
public class VerticalIf implements Command {
    private static final Logger log = Logger.getLogger(VerticalIf.class);

    /**
     * Execute operation. Pop a value; move down if value=0 (or stack is empty), up otherwise
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) {
//        try {
//            if (stack.pop() == 0) {
//                ((Command)Factory.instance().get(Character.toString('v'))).execute(stack, field);
//            } else {
//                ((Command)Factory.instance().get(Character.toString('^'))).execute(stack, field);
//            }
//        } catch (FactoryException ex) {
//            throw new CommandException();
//        }

//        if(stack.size() < 1) {
//            throw new CommandException("Stack is empty");
//        }

        log.trace("VerticalIf");
        if (stack.empty() || stack.pop() == 0) {
            new Down().execute(stack, field);
        } else {
            new Up().execute(stack, field);
        }
    }
}
