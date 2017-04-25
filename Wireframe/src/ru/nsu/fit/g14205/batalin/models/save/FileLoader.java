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
            double a = Double.parseDouble(strData[3]);
            double b = Double.parseDouble(strData[4]);
            double c = Double.parseDouble(strData[5]);
            double d = Double.parseDouble(strData[6]);

            Grid grid = applicationProperties.getGrid();
            grid.setCols(n);
            grid.setRows(m);
            grid.setSegmentSplitting(k);

            Area area = new Area(a, c, b, d);
            applicationProperties.setArea(area);

            strData = nextData(scanner);
            double zf = Double.parseDouble(strData[0]);
            double zb = Double.parseDouble(strData[1]);
            double sw = Double.parseDouble(strData[2]);
            double sh = Double.parseDouble(strData[3]);

            ViewPyramidProperties pyramidProperties = applicationProperties.getViewPyramidProperties();
            pyramidProperties.setFrontPlaneDistance(zf);
            pyramidProperties.setBackPlaneDistance(zb);
            pyramidProperties.setFrontPlaneWidth(sw);
            pyramidProperties.setFrontPlaneHeight(sh);

            strData = nextData(scanner);
            double r11 = Double.parseDouble(strData[0]);
            double r12 = Double.parseDouble(strData[1]);
            double r13 = Double.parseDouble(strData[2]);

            strData = nextData(scanner);
            double r21 = Double.parseDouble(strData[0]);
            double r22 = Double.parseDouble(strData[1]);
            double r23 = Double.parseDouble(strData[2]);

            strData = nextData(scanner);
            double r31 = Double.parseDouble(strData[0]);
            double r32 = Double.parseDouble(strData[1]);
            double r33 = Double.parseDouble(strData[2]);

            Matrix sceneMatrix = new Matrix(4, 4, new double[]{
                    r11, r12, r13, 0,
                    r21, r22, r23, 0,
                    r31, r32, r33, 0,
                    0, 0, 0, 1
            });
            applicationProperties.getScene().getFigureProperties().getCoordinateSystem().setTransformMatrix(sceneMatrix);

            strData = nextData(scanner);
            int red = Integer.parseInt(strData[0]);
            int green = Integer.parseInt(strData[1]);
            int blue = Integer.parseInt(strData[2]);

            applicationProperties.setBackgroundColor(new Color(red, green, blue));

            strData = nextData(scanner);
            int figuresCount = Integer.parseInt(strData[0]);

            applicationProperties.getScene().clear();
            applicationProperties.getFigureProperties().clear();
            for(int i = 0; i < figuresCount; ++i) {
                strData = nextData(scanner);
                red = Integer.parseInt(strData[0]);
                green = Integer.parseInt(strData[1]);
                blue = Integer.parseInt(strData[2]);

                strData = nextData(scanner);
                double cX = Double.parseDouble(strData[0]);
                double cY = Double.parseDouble(strData[1]);
                double cZ = Double.parseDouble(strData[2]);

                strData = nextData(scanner);
                r11 = Double.parseDouble(strData[0]);
                r12 = Double.parseDouble(strData[1]);
                r13 = Double.parseDouble(strData[2]);

                strData = nextData(scanner);
                r21 = Double.parseDouble(strData[0]);
                r22 = Double.parseDouble(strData[1]);
                r23 = Double.parseDouble(strData[2]);

                strData = nextData(scanner);
                r31 = Double.parseDouble(strData[0]);
                r32 = Double.parseDouble(strData[1]);
                r33 = Double.parseDouble(strData[2]);

                strData = nextData(scanner);
                n = Integer.parseInt(strData[0]);

                LineProperties lineProperties = new BSplineProperties(applicationProperties);
                for(int j = 0; j < n; ++j) {
                    strData = nextData(scanner);
                    double x = Double.parseDouble(strData[0]);
                    double y = Double.parseDouble(strData[1]);

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
