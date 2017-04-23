package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class Matrix implements Cloneable {
    private int width;
    private int height;
    private double[] data;

    public Matrix(int width, int height) {
        this(width, height, null);
    }

    public Matrix(int width, int height, double[] data) {
        this.width = width;
        this.height = height;
        this.data = new double[width * height];

        if(data != null) {
            System.arraycopy(data, 0, this.data, 0, data.length);
        }
    }

    public Matrix clone() throws CloneNotSupportedException {
        Matrix matrix = (Matrix) super.clone();
        matrix.width = width;
        matrix.height = height;
        matrix.data = new double[data.length];
        System.arraycopy(data, 0, matrix.data, 0, data.length);
        return matrix;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void set(int x, int y, double val) {
        data[y * width + x] = val;
    }

    public double get(int x, int y) {
        return data[y * width + x];
    }

    public Matrix multiply(double val) {
        Matrix result = new Matrix(width, height);
        for(int i = 0; i < data.length; ++i) {
            result.data[i] = data[i] * val;
        }

        return result;
    }

    public Matrix add(double val) {
        Matrix result = new Matrix(width, height);
        for(int i = 0; i < data.length; ++i) {
            result.data[i] = data[i] + val;
        }

        return result;
    }

    public Matrix multiply(Matrix other) {
        if (width != other.height) {
            throw new IllegalArgumentException("Bad matrix size");
        }
        Matrix result = new Matrix(other.width, height);

        for(int x = 0; x < result.width; ++x) {
            for(int y = 0; y < result.height; ++y) {
                double val = 0.;
                for(int i = 0; i < width; ++i) {
                    val += get(i, y) * other.get(x, i);
                }
                result.set(x, y, val);
            }
        }

        return result;
    }

    public double toDouble() {
        if (width != 1 || height != 1) {
            throw new IllegalArgumentException("Bad matrix size");
        }

        return get(0, 0);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for(int y = 0; y < height; ++y) {
            for(int x = 0; x < width; ++x) {
                builder.append(get(x, y));
                builder.append(" ");
            }
            builder.append(String.format("%n"));
        }

        return builder.toString();
    }
}
