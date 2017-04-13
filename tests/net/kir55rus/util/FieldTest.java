package net.kir55rus.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 04.03.16.
 */
public class FieldTest {

    @Test
    public void testGetChar() throws Exception {
        Field field = new Field();
        field.addLine("abc");
        assertEquals("Without args", 'a', field.getChar());

        field.addLine("gldt");
        assertEquals("With args 1", 't', field.getChar(3, 1));
        assertEquals("With args 2", ' ', field.getChar(3, 0));
    }

    @Test
    public void testSetChar() throws Exception {
        Field field = new Field();
        field.addLine("12345");
        field.addLine("abc");

        field.setChar('P');
        assertEquals("Without args", 'P', field.getChar());

        field.setChar(4, 1, 'N');
        assertEquals("With args", 'N', field.getChar(4, 1));
    }

    @Test
    public void testClear() throws Exception {
        Field field = new Field();
        field.addLine("sdfdsgdf");
        field.addLine("wf4ef");
        field.addLine("dfgdfgdfgeh4");

        field.clear();
        assertEquals("Height after clear", 0, field.height());
        assertEquals("Width after clear", 0, field.width());

        try {
            field.getChar(0, 0);
            fail("Get char after clear");
        } catch (Exception ex) {
        }

        field.addLine("fdfd4");
        assertEquals("Height after clear 2", 1, field.height());
        assertEquals("Width after clear 2", 5, field.width());
        assertEquals("Get char after clear 2", 'f', field.getChar());
    }

    @Test
    public void testDirection() throws Exception {
        Field field = new Field();
        assertEquals("Get default direction", Direction.RIGHT, field.getDirection());

        field.setDirection(Direction.UP);
        assertEquals("Up direction", Direction.UP, field.getDirection());
    }
    @Test
    public void testStep() throws Exception {
        Field field = new Field();
        field.addLine("sdfsd");
        field.addLine("weegr");

        field.setDirection(Direction.RIGHT);
        field.step();
        assertEquals("Step 1: X", 1, field.x());
        assertEquals("Step 1: Y", 0, field.y());

        field.setDirection(Direction.DOWN);
        field.step();
        assertEquals("Step 2: X", 1, field.x());
        assertEquals("Step 2: Y", 1, field.y());

        field.setDirection(Direction.LEFT);
        field.step();
        assertEquals("Step 3: X", 0, field.x());
        assertEquals("Step 3: Y", 1, field.y());

        field.setDirection(Direction.UP);
        field.step();
        assertEquals("Step 4: X", 0, field.x());
        assertEquals("Step 4: Y", 0, field.y());

        field.setDirection(Direction.UP);
        field.step();
        assertEquals("Step 5: X", 0, field.x());
        assertEquals("Step 5: Y", 1, field.y());

        field.setDirection(Direction.LEFT);
        field.step();
        assertEquals("Step 6: X", 4, field.x());
        assertEquals("Step 6: Y", 1, field.y());

        field.setDirection(Direction.DOWN);
        field.step();
        assertEquals("Step 7: X", 4, field.x());
        assertEquals("Step 7: Y", 0, field.y());

        field.setDirection(Direction.RIGHT);
        field.step();
        assertEquals("Step 8: X", 0, field.x());
        assertEquals("Step 8: Y", 0, field.y());
    }

    @Test
    public void testAddLine() throws Exception {
        Field field = new Field();
        field.addLine("123");
        assertEquals("Add 1: width", 3, field.width());
        assertEquals("Add 1: height", 1, field.height());

        field.addLine("1234");
        assertEquals("Add 1: width", 4, field.width());
        assertEquals("Add 1: height", 2, field.height());

        field.addLine("12");
        assertEquals("Add 1: width", 4, field.width());
        assertEquals("Add 1: height", 3, field.height());
    }

    @Test
    public void testCrds() throws Exception {
        Field field = new Field();
        assertEquals("Default x", 0, field.x());
        assertEquals("Default y", 0, field.y());

        field.addLine("1234");
        field.addLine("44445");
        field.addLine("4drgd");
        field.setX(2);
        field.setY(1);
        assertEquals("Set x", 2, field.x());
        assertEquals("Set y", 1, field.y());
    }

    @Test
    public void testExecution() throws Exception {
        Field field = new Field();
        assertTrue("Default execution", field.isExecution());

        field.setExecution(false);
        assertFalse("False execution", field.isExecution());

        field.setExecution(true);
        assertTrue("True execution", field.isExecution());
    }

    @Test
    public void testSize() throws Exception {
        Field field = new Field();
        assertEquals("Default width", 0, field.width());
        assertEquals("Default height", 0, field.height());

        field.addLine("123");
        assertEquals("Add 1: width", 3, field.width());
        assertEquals("Add 1: height", 1, field.height());

        field.addLine("1234");
        assertEquals("Add 1: width", 4, field.width());
        assertEquals("Add 1: height", 2, field.height());

        field.addLine("12");
        assertEquals("Add 1: width", 4, field.width());
        assertEquals("Add 1: height", 3, field.height());
    }
}