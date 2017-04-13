package net.kir55rus;

import net.kir55rus.commands.CommandException;
import net.kir55rus.commands.PrintAsChar;
import net.kir55rus.util.Field;
import org.junit.Test;

import java.io.*;
import java.util.Stack;

import static org.junit.Assert.*;

/**
 * Created by kir55rus on 04.03.16.
 */
public class BefungeTest {

    @Test
    public void testMain() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setErr(new PrintStream(output));

        Befunge.main(new String[0]);
        assertEquals("Main empty args 1", "Need file!\n", output.toString());

        output.reset();
        Befunge.main(null);
        assertEquals("Main empty args 2", "Need file!\n", output.toString());

        output.reset();
        String[] args = new String[1];
        args[0] = "dd";
        Befunge.main(args);
        assertEquals("Main with bad args", "File with befunge not found\n", output.toString());
    }

    @Test
    public void testGo() throws Exception {
        Befunge befunge = new Befunge();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setErr(new PrintStream(output));

        befunge.go(null);
        assertEquals("With empty arg", "Need file!\n", output.toString());

        output.reset();
        befunge.go(new File("dd"));
        assertEquals("Main with bad args", "File with befunge not found\n", output.toString());

        File tmpField = File.createTempFile("tmpField", null, null);

        String testString = ">  hzxc>@";
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(tmpField))) {
            writer.write(testString);
        } catch (Exception ex) {
            fail("Can't make file");
        }

        befunge.go(tmpField);
    }
}