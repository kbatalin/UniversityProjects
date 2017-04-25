package ru.nsu.fit.g14205.batalin.models.save;

import ru.nsu.fit.g14205.batalin.models.*;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Iterator;

/**
 * Created by kir55rus on 05.03.17.
 */
public class FileSaver implements Saver {
    private ApplicationProperties applicationProperties;
    private File path;

    public FileSaver(ApplicationProperties applicationProperties, File path) {
        this.applicationProperties = applicationProperties;
        this.path = path;
    }

    @Override
    public void save() throws IOException {
        if (path == null) {
            throw new IOException("Bad path");
        }

        String lineSeparator = System.lineSeparator();
        Charset charset = Charset.forName("UTF-8");
        try (BufferedWriter writer = Files.newBufferedWriter(path.toPath(), charset)) {
            //n m k a b c d
            Grid grid = applicationProperties.getGrid();
            writer.write(String.valueOf(grid.getCols()) + " " + grid.getRows() + " " + grid.getSegmentSplitting() + " ");
            Area area = applicationProperties.getArea();
            writer.write(String.valueOf(area.first.getX()) + " " + area.second.getX() + " ");
            writer.write(String.valueOf(area.first.getY()) + " " + area.second.getY() + lineSeparator);

            //zn zf sw sh
            ViewPyramidProperties pyramid = applicationProperties.getViewPyramidProperties();
            writer.write(String.valueOf(pyramid.getFrontPlaneDistance()) + " " + pyramid.getBackPlaneDistance() + " ");
            writer.write(String.valueOf(pyramid.getFrontPlaneWidth()) + " " + pyramid.getFrontPlaneHeight() + lineSeparator);

            //scene matrix
            Matrix sceneMatrix = applicationProperties.getScene().getFigureProperties().getCoordinateSystem().getTransformMatrix();
            writer.write(String.valueOf(sceneMatrix.get(0,0)) + " " + sceneMatrix.get(1,0) + " " + sceneMatrix.get(2,0) + lineSeparator);
            writer.write(String.valueOf(sceneMatrix.get(0,1)) + " " + sceneMatrix.get(1,1) + " " + sceneMatrix.get(2,1) + lineSeparator);
            writer.write(String.valueOf(sceneMatrix.get(0,2)) + " " + sceneMatrix.get(1,2) + " " + sceneMatrix.get(2,2) + lineSeparator);

            //background color
            Color backgroundColor = applicationProperties.getBackgroundColor();
            writer.write(String.valueOf(backgroundColor.getRed()) + " " + backgroundColor.getGreen() + " " + backgroundColor.getBlue() + lineSeparator);

            //figures
            saveFigures(writer);
        }
    }

    private void saveFigures(BufferedWriter writer) throws IOException {
        String lineSeparator = System.lineSeparator();
        writer.write(String.valueOf(applicationProperties.getFigurePropertiesCount()) + lineSeparator);

        Iterator<PaintedFigure> figureIterator = applicationProperties.getScene().figures();
        while (figureIterator.hasNext()) {
            PaintedFigure figure = figureIterator.next();

            FigureProperties figureProperties = figure.getFigureProperties();
            LineProperties lineProperties = figureProperties.getLineProperties();
            CoordinateSystem coordinateSystem = figureProperties.getCoordinateSystem();

            //color
            Color color = lineProperties.getColor();
            writer.write(String.valueOf(color.getRed()) + " " + color.getGreen() + " " + color.getBlue() + lineSeparator);

            //center
            Point3D center = coordinateSystem.getCenter();
            writer.write(String.valueOf(center.getX()) + " " + center.getY() + " " + center.getZ() + lineSeparator);

            Matrix rotationMatrix = coordinateSystem.getRotationMatrix();
            writer.write(String.valueOf(rotationMatrix.get(0,0)) + " " + rotationMatrix.get(1,0) + " " + rotationMatrix.get(2,0) + lineSeparator);
            writer.write(String.valueOf(rotationMatrix.get(0,1)) + " " + rotationMatrix.get(1,1) + " " + rotationMatrix.get(2,1) + lineSeparator);
            writer.write(String.valueOf(rotationMatrix.get(0,2)) + " " + rotationMatrix.get(1,2) + " " + rotationMatrix.get(2,2) + lineSeparator);

            writer.write(String.valueOf(lineProperties.getControlPointsCount()) + lineSeparator);

            Iterator<Point2D> point2DIterator = lineProperties.getControlPointsIterator();
            while (point2DIterator.hasNext()) {
                Point2D point2D = point2DIterator.next();
                writer.write(String.valueOf(point2D.getX()) + " " + point2D.getY() + lineSeparator);
            }
        }
    }
}
