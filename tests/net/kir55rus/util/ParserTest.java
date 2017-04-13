package net.kir55rus.util;

import net.kir55rus.commands.InputCharacter;
import net.kir55rus.commands.StringMode;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 04.03.16.
 */
public class ParserTest {

    @Test
    public void testParseFieldFromFile() throws Exception {
        File tmpField = File.createTempFile("tmpField", null, null);

        String testString = "dfgnq34un3qg dgd f";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(tmpField))) {
            writer.write(testString);
        } catch (Exception ex) {
            fail("Can't make file");
        }

        Field field = Parser.parseFieldFromFile(tmpField);

        if(field == null) {
            fail("Field can't parse");
        }

        for(int i = 0, count = field.width(); i < count; ++i) {
            assertEquals("Different chars", testString.charAt(i), field.getChar(i, 0));
        }
    }

    @Test
    public void testParseIntFromConsole() throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream("12\n4a".getBytes());
        System.setIn(input);

        assertEquals("Bad int 1", 12, Parser.parseIntFromConsole(""));
        assertEquals("Bad int 2", 0, Parser.parseIntFromConsole(""));

        ByteArrayInputStream input2 = new ByteArrayInputStream("4a".getBytes());
        System.setIn(input2);

        assertEquals("Bad int 2", 0, Parser.parseIntFromConsole(""));
    }

    @Test
    public void testParseCharFromConsole() throws Exception {
        ByteArrayInputStream input = new ByteArrayInputStream("ag".getBytes());
        System.setIn(input);

        assertEquals("Bad char", 'a', Parser.parseCharFromConsole(""));
    }
}