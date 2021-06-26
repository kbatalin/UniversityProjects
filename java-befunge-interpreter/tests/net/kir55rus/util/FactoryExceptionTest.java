package net.kir55rus.util;

import net.kir55rus.commands.CommandException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 04.03.16.
 */
public class FactoryExceptionTest {
    @Test
    public void testException() {
        //Test default constructor
        try {
            throw new FactoryException();
        } catch (FactoryException ex) {
        }

        String testStr = "test str";
        try {
            throw new FactoryException(testStr);
        } catch (FactoryException ex) {
            assertEquals("Bad text in exception", testStr, ex.getMessage());
        }
    }
}