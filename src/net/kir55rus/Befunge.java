package net.kir55rus;

import net.kir55rus.commands.*;
import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.*;
import java.io.*;

/**
 * Main class of Befunge interpreter.
 *
 * Syntax:
 * <pre>{@code
 * 0-9	Push this number on the stack
 * +	Addition: Pop a and b, then push a+b
 * -	Subtraction: Pop a and b, then push b-a
 * *	Multiplication: Pop a and b, then push a*b
 * /	Integer division: Pop a and b, then push b/a, rounded down. If a is zero, ask the user what result they want.
 * %	Modulo: Pop a and b, then push the remainder of the integer division of b/a.
 * !	Logical NOT: Pop a value. If the value is zero, push 1; otherwise, push zero.
 * `	Greater than: Pop a and b, then push 1 if b>a, otherwise zero.
 * >	Start moving right
 * <	Start moving left
 * ^	Start moving up
 * v	Start moving down
 * ?	Start moving in a random cardinal direction
 * _	Pop a value; move right if value=0, left otherwise
 * |	Pop a value; move down if value=0, up otherwise
 * "	Start string mode: push each character's ASCII value all the way up to the next "
 * :	Duplicate value on top of the stack
 * \	Swap two values on top of the stack
 * $	Pop value from the stack and discard it
 * .	Pop value and output as an integer
 * ,	Pop value and output as ASCII character
 * #	Trampoline: Skip next cell
 * p	A "put" call (a way to store a value for later use). Pop y, x, and v, then change the character at (x,y) in the program to the character with ASCII value v
 * g	A "get" call (a way to retrieve data in storage). Pop y and x, then push ASCII value of the character at that position in the program. If (x,y) is out of bounds, push 0.
 * &	Ask user for a number and push it
 * ~	Ask user for a character and push its ASCII value
 * \@	End program
 * }</pre>
 */
public class Befunge {
    private static final Logger log = Logger.getLogger(Befunge.class);

    /**
     * Method for starting interpreter
     * @param args First element of array should be name of file with Befunge-code
     */
    public static void main(String[] args) {

        log.debug("Start programm");
        new Befunge().go((args != null && args.length > 0) ? new File(args[0]) : null);
    }

    /**
     * Interpreter of Befunge
     * @param file Path of file with befunge source code
     */
    public void go(File file) {
        if(file == null) {
            log.debug("File reference is null");
            System.err.println("Need file!");
            return;
        }

        Field field = Parser.parseFieldFromFile(file);
        if(field == null) {
            log.debug("Field reference is null");
            System.err.println("File with befunge not found");
            return;
        }

        Stack<Integer> stack = new Stack<>();

        try {
            log.debug("Instance factory");
            Factory factory = Factory.instance();

            while (field.isExecution()) {
                char currentCommand = field.getChar();
                log.trace("Execute: '" + currentCommand + "'");
                Command cmd = (Command) factory.get(Character.toString(currentCommand)); //Character.toString(field.getChar())
                cmd.execute(stack, field);
                field.step();
            }
        } catch (Exception ex) { //FactoryException | CommandException
            log.debug("Bad command in field: '" + field.getChar() + "'");
            System.err.println("Bad command");
        }
    }
}
