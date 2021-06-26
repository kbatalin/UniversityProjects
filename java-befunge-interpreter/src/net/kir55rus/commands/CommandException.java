package net.kir55rus.commands;

/**
 * If operation can't execute, it throws this exception
 */
public class CommandException extends Exception {
    public CommandException() {
        super();
    }

    public CommandException(String error) {
        super(error);
    }
}
