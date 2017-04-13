package net.kir55rus.util;

import org.apache.log4j.Logger;

import java.util.*;

public class Field {
    private static final Logger log = Logger.getLogger(Field.class);

    private ArrayList<ArrayList<Character>> field = new ArrayList<>();
    private boolean execution = true;
    private int currentX = 0;
    private int currentY = 0;
    private Direction direction = Direction.RIGHT;
    private int width = 0;
    private int height = 0;

    /**
     * Get char from select position
     * @param x abscissa
     * @param y ordinate
     * @return char from field
     */
    public char getChar(int x, int y) {
        return field.get(y).get(x);
    }

    /**
     * Get char from current position
     * @return char from field
     */
    public char getChar() {
        return field.get(currentY).get(currentX);
    }

    /**
     * Set char to select position
     * @param x abscissa
     * @param y ordinate
     * @param c char for set
     */
    public void setChar(int x, int y, char c) {
        field.get(y).set(x, c);
    }

    /**
     * Set char to current position
     * @param c char for set
     */
    public void setChar(char c) {
        field.get(currentY).set(currentX, c);
    }

    /**
     * Clear field
     */
    public void clear() {
        field.clear();
        execution = true;
        currentX = 0;
        currentY = 0;
        direction = Direction.RIGHT;
        width = 0;
        height = 0;
    }

    /**
     * Get current direction
     * @return current direction
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Set current direction
     * @param direction need direction
     */
    public void setDirection(Direction direction) {
        log.trace("Set new direction");
        this.direction = direction;
    }

    /**
     * Do step with current direction
     */
    public void step() { //todo: replace
        log.trace("Step");
        switch (direction) {
            case UP: setY(y() - 1); break;
            case RIGHT: setX(x() + 1); break;
            case DOWN: setY(y() + 1); break;
            case LEFT: setX(x() - 1); break;
        }
    }

    /**
     * Add new line to field. All lines will be increased to the maximum length
     * @param str line for add
     */
    public void addLine(String str) {
        log.debug("Add line: " + str);
        int length = str.length();
        if(length > width) {
            log.debug("Need to extend field. Actual: " + width + ", need: " + length);
            lengthenLines(length);
            width = length;
        }

        ArrayList<Character> newLine = new ArrayList<>(width);
        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length; ++i) {
            newLine.add(i, chars[i]);
        }

        for(int i = newLine.size(); i < width; ++i) {
            newLine.add(' ');
        }

        field.add(newLine); //new ArrayList<Character>(Arrays.asList(Character.str.toCharArray()))

        ++height;

        log.debug("New line was added");
    }

    private void lengthenLines(int length) {
        log.debug("Extend all lines in field");
        for(ArrayList<Character> line : field) {
            for(int i = width; i < length; ++i) {
                line.add(' ');
            }
        }
    }

    /**
     * Get current Y
     * @return current Y
     */
    public int y() {
        return currentY;
    }

    /**
     * Set current Y
     * @param y new Y
     */
    public void setY(int y) {
        log.trace("Set new Y: " + currentY);
        currentY = (y + height) % height;
    }

    /**
     * Get current X
     * @return current X
     */
    public int x() {
        return currentX;
    }

    /**
     * Set current X
     * @param x new X
     */
    public void setX(int x) {
        log.trace("Set new X: " + currentX);
        currentX = (x + width) % width;
    }

    /**
     * return false, if program stopped, else true
     * @return false, if program stopped, else true
     */
    public boolean isExecution() {
        return execution;
    }

    /**
     * Set status program
     * @param b false - stop, true - run
     */
    public void setExecution(boolean b) {
        execution = b;
    }

    /**
     * Get current width of field
     * @return current width
     */
    public int width() {
        return width;
    }

    /**
     * Get current height of field
     * @return current height
     */
    public int height() {
        return height;
    }

}
