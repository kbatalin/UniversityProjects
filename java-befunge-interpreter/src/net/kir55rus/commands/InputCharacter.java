package net.kir55rus.commands;

import net.kir55rus.util.Field;
import net.kir55rus.util.Parser;
import org.apache.log4j.Logger;

import java.util.Stack;

/**
 * Ask user for a character and push its ASCII value
 */
public class InputCharacter implements Command {
    private static final Logger log = Logger.getLogger(InputCharacter.class);

    /**
     * Execute operation. Ask user for a character and push its ASCII value. If user enter nothing, 0 (zero) will be pushed to stack
     * @param stack Operands for operation execute
     * @param field Field with befunge commands
     */
    @Override
    public void execute(Stack<Integer> stack, Field field) { //Если ничего не ввели, то надо вернуть 0?
        log.trace("InputCharacter");
        stack.push(Parser.parseCharFromConsole("\nEnter char: "));
    }
}
