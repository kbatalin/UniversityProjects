package ru.nsu.fit.g14205.batalin.models.save;

import ru.nsu.fit.g14205.batalin.models.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Created by kir55rus on 05.03.17.
 */
public class FileLoader implements Loader {
    private ApplicationProperties applicationProperties;
    private File path;

    public FileLoader(File file, ApplicationProperties applicationProperties) {
        path = file;
        this.applicationProperties = applicationProperties;
    }

    @Override
    public void load() throws IOException {
        if (path == null) {
            throw new IOException("Bad path");
        }

        try (Scanner scanner = new Scanner(path, "UTF-8")) {
            String[] strData = nextData(scanner);
            int n = Integer.parseInt(strData[0]);
            int m = Integer.parseInt(strData[1]);
            int k = Integer.parseInt(strData[2]);
            int a = Integer.parseInt(strData[3]);
            int b = Integer.parseInt(strData[4]);
            int c = Integer.parseInt(strData[5]);
            int d = Integer.parseInt(strData[6]);
            strData = nextData(scanner);

            Grid grid = applicationProperties.getGrid();
            grid.setCols(n);
            grid.setRows(m);

            Area area = new Area(a, c, b, d);
            applicationProperties.setArea(area);

            int zf = Integer.parseInt(strData[0]);
            int zb = Integer.parseInt(strData[1]);
            int sw = Integer.parseInt(strData[2]);
            int sh = Integer.parseInt(strData[3]);
            strData = nextData(scanner);

            ViewPyramidProperties pyramidProperties = applicationProperties.getViewPyramidProperties();
            pyramidProperties.setFrontPlaneDistance(zf);
            pyramidProperties.setBackPlaneDistance(zb);
            pyramidProperties.setFrontPlaneWidth(sw);
            pyramidProperties.setFrontPlaneHeight(sh);

            int red = Integer.parseInt(strData[0]);
            int green = Integer.parseInt(strData[1]);
            int blue = Integer.parseInt(strData[2]);
            strData = nextData(scanner);

            applicationProperties.setBackgroundColor(new Color(red, green, blue));

            int figuresCount = Integer.parseInt(strData[0]);
            strData = nextData(scanner);

            applicationProperties.getScene().clear();
            applicationProperties.getFigureProperties().clear();
            for(int i = 0; i < figuresCount; ++i) {
                red = Integer.parseInt(strData[0]);
                green = Integer.parseInt(strData[1]);
                blue = Integer.parseInt(strData[2]);
                strData = nextData(scanner);

                int cX = Integer.parseInt(strData[0]);
                int cY = Integer.parseInt(strData[1]);
                int cZ = Integer.parseInt(strData[2]);
                strData = nextData(scanner);

                int r11 = Integer.parseInt(strData[0]);
                int r12 = Integer.parseInt(strData[1]);
                int r13 = Integer.parseInt(strData[2]);
                strData = nextData(scanner);

                int r21 = Integer.parseInt(strData[0]);
                int r22 = Integer.parseInt(strData[1]);
                int r23 = Integer.parseInt(strData[2]);
                strData = nextData(scanner);

                int r31 = Integer.parseInt(strData[0]);
                int r32 = Integer.parseInt(strData[1]);
                int r33 = Integer.parseInt(strData[2]);
                strData = nextData(scanner);

                n = Integer.parseInt(strData[0]);
                strData = nextData(scanner);

                LineProperties lineProperties = new BSplineProperties(applicationProperties);
                for(int j = 0; j < n; ++j) {
                    int x = Integer.parseInt(strData[0]);
                    int y = Integer.parseInt(strData[1]);
                    strData = nextData(scanner);

                    lineProperties.addControlPoint(new Point2D.Double(x, y));
                }
                lineProperties.setColor(new Color(red, green, blue));

                FigureProperties figureProperties = new FigurePropertiesDefault(lineProperties);
                figureProperties.getCoordinateSystem().setCenter(new Point3D(cX, cY, cZ));

                Matrix transformMatrix = new Matrix(4, 4, new double[]{
                        r11, r12, r13, 0,
                        r21, r22, r23, 0,
                        r31, r32, r33, 0,
                        0, 0, 0, 1
                });
                transformMatrix = new Matrix(4, 4, new double[]{
                        1, 0, 0, cX,
                        0, 1, 0, cY,
                        0, 0, 1, cZ,
                        0, 0, 0, 1,
                }).multiply(transformMatrix);
                figureProperties.getCoordinateSystem().setTransformMatrix(transformMatrix);

                applicationProperties.addFigureProperties(figureProperties);
            }

        } catch (Exception e) {
            throw new IOException("Bad file format");
        }
    }

    private static String removeComment(String line) {
        int index = line.indexOf("//");
        if (index == -1) {
            return line;
        }

        return line.substring(0, index);
    }

    private static String[] nextData(Scanner scanner) {
        String line = "";
        while (line.isEmpty()) {
            line = removeComment(scanner.nextLine()).trim();
        }

        return line.split(" ");
    }
}
