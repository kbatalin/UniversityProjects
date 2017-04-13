package net.kir55rus.commands;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 04.03.16.
 */
public class CommandExceptionTest {
    @Test
    public void testException() {
        //Test default constructor
        try {
            throw new CommandException();
        } catch (CommandException ex) {
        }

        String testStr = "test str";
        try {
            throw new CommandException(testStr);
        } catch (CommandException ex) {
            assertEquals("Bad text in exception", testStr, ex.getMessage());
        }
    }
}