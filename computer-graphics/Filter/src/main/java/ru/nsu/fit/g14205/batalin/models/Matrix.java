package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by kir55rus on 28.03.17.
 */
public class Matrix {
    private int width;
    private int height;
    private int[] data;

    public Matrix(int width, int height) {
        this(width, height, null);
    }

    public Matrix(int width, int height, int[] data) {
        this.width = width;
        this.height = height;
        this.data = new int[width * height];

        if(data != null) {
            System.arraycopy(data, 0, this.data, 0, data.length);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void set(int x, int y, int val) {
        data[y * width + x] = val;
    }

    public int get(int x, int y) {
        return data[y * width + x];
    }

    public Matrix multiply(int val) {
        Matrix result = new Matrix(width, height);
        for(int i = 0; i < data.length; ++i) {
            result.data[i] = data[i] * val;
        }

        return result;
    }

    public Matrix add(int val) {
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
                int val = 0;
                for(int i = 0; i < width; ++i) {
                    val += get(x + i, y) * other.get(x, y + i);
                }
                result.set(x, y, val);
            }
        }

        return result;
    }

    public int[] convolution(BufferedImage image, int x, int y) {
        int[] results = new int[] {0, 0, 0};
        for(int i = 0; i < width; ++i) {
            for(int j = 0; j < height; ++j) {
                Color color = new Color(image.getRGB(x + i, y + j));
                int matrixVal = get(i, j);
                results[0] += matrixVal * color.getRed();
                results[1] += matrixVal * color.getGreen();
                results[2] += matrixVal * color.getBlue();
            }
        }

        return results;
    }
}
