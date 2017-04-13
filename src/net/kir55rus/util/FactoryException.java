package net.kir55rus.util;

/**
 * Throws, if factory has error
 * @see Factory
 */
public class FactoryException extends Exception {
    public FactoryException() {
        super();
    }

    public FactoryException(String err) {
        super(err);
    }
}

