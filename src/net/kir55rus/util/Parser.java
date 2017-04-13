package net.kir55rus.util;

import net.kir55rus.util.*;
import org.apache.log4j.Logger;

import java.util.*;
import java.io.*;

/**
 * Static class for parsing user input and files
 */
public final class Parser {
    private static final Logger log = Logger.getLogger(Parser.class);

    private Parser() {}

    /**
     * Parse befunge code to Field
     * @param file Filepath with befunge code
     * @return reference to Field, if parsing was successful or null else
     */
    public static Field parseFieldFromFile(File file) {
//        if(!file.exists() || !file.canRead()) {
//            return null;
//        }
        log.debug("Parse: " + file);
        Field field = new Field();
        try (Scanner scanner = new Scanner(file)) {
            while(scanner.hasNextLine()) {
                field.addLine(scanner.nextLine());
            }
        } catch (FileNotFoundException ex) {
            log.debug("Cannot read file");
            return null;
        }

        log.debug("Field was created");
        return field;
    }

    /**
     * Ask user for a integer and return it
     * @param prompt Text for help user
     * @return integer or 0, if user enter nothing
     */
    public static int parseIntFromConsole(String prompt) {
        log.trace("Parse int from console");

        System.out.println(prompt);
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNext()) {
            String input = scanner.nextLine();
            try {
                return Integer.parseInt(input);
            } catch (Exception ex) {
                log.debug("Bad input format. Return 0");
                return 0;
            }
        }

        return 0;
    }

    /**
     * Ask user for a character and return its ASCII value
     * @param prompt Text for help user
     * @return char or 0, if user enter nothing
     */
    public static int parseCharFromConsole(String prompt) {
        log.trace("Parse char from console");

        System.out.println(prompt);
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        return input.isEmpty() ? 0 : input.charAt(0);
    }
}
