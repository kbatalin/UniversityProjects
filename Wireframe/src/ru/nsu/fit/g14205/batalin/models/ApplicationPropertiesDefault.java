package ru.nsu.fit.g14205.batalin.models;

import ru.nsu.fit.g14205.batalin.models.observe.ObservableBase;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kir55rus on 12.04.17.
 */
public class ApplicationPropertiesDefault extends ObservableBase implements ApplicationProperties {
    private double controlPointRadius;
    private ArrayList<FigureProperties> figureProperties;
    private Area area;
    private CameraProperties cameraProperties;
    private ViewPyramidProperties viewPyramidProperties;
    private PaintedFigure scene;
    private Grid grid;

    public ApplicationPropertiesDefault() {
        controlPointRadius = .3;
        figureProperties = new ArrayList<>();
        area = new Area(0, 0, 1, 2 * Math.PI);
        cameraProperties = new Camera(new Point3D(-10, 0, 0), new Point3D(10, 0, 0), new Point3D(0, 1, 0));
        viewPyramidProperties = new ViewPyramid(15, 5, 10, 10);
        grid = new Grid(5, 6, 5);

        FigureProperties sceneProperties = new FigurePropertiesDefault();
        scene = new Figure(sceneProperties);

        grid.addObserver(Grid.Event.SIZE_CHANGED, this::updFigures);
        grid.addObserver(Grid.Event.SEGMENT_SPLITTING_CHANGED, this::updFigures);
    }

    @Override
    public ApplicationProperties clone() throws CloneNotSupportedException {
        ApplicationPropertiesDefault applicationProperties = (ApplicationPropertiesDefault) super.clone();
        applicationProperties.controlPointRadius = controlPointRadius;
        applicationProperties.cameraProperties = cameraProperties.clone();
        applicationProperties.area = area.clone();
        applicationProperties.viewPyramidProperties = viewPyramidProperties.clone();
        applicationProperties.scene = scene.clone();
        applicationProperties.figureProperties = new ArrayList<>();
        for (FigureProperties figure : figureProperties) {
            applicationProperties.figureProperties.add(figure.clone());
        }
        applicationProperties.grid = grid.clone();
        return applicationProperties;
    }

    @Override
    public void apply(ApplicationProperties applicationProperties) {
        controlPointRadius = applicationProperties.getControlPointRadius();

        figureProperties.clear();
        for (FigureProperties figure : applicationProperties.getFigureProperties()) {
            addFigureProperties(figure);
        }
        setArea(applicationProperties.getArea());
        setScene(applicationProperties.getScene());

        grid.setCols(applicationProperties.getGrid().getCols());
        grid.setRows(applicationProperties.getGrid().getRows());

        cameraProperties.setCameraPosition(applicationProperties.getCameraProperties().getCameraPosition());
        cameraProperties.setUpVector(applicationProperties.getCameraProperties().getUpVector());
        cameraProperties.setViewPoint(applicationProperties.getCameraProperties().getViewPoint());

        viewPyramidProperties.setFrontPlaneDistance(applicationProperties.getViewPyramidProperties().getFrontPlaneDistance());
        viewPyramidProperties.setBackPlaneDistance(applicationProperties.getViewPyramidProperties().getBackPlaneDistance());
        viewPyramidProperties.setFrontPlaneHeight(applicationProperties.getViewPyramidProperties().getFrontPlaneHeight());
        viewPyramidProperties.setFrontPlaneWidth(applicationProperties.getViewPyramidProperties().getFrontPlaneWidth());
    }

    @Override
    public Grid getGrid() {
        return grid;
    }

    @Override
    public PaintedFigure getScene() {
        return scene;
    }

    @Override
    public void setScene(PaintedFigure scene) {
        this.scene = scene;
        notifyObservers(Event.SCENE_CHANGED);
    }

    @Override
    public ViewPyramidProperties getViewPyramidProperties() {
        return viewPyramidProperties;
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public void setArea(Area area) {
        this.area = area;
        updFigures();
        notifyObservers(Event.AREA_CHANGED);
    }

    @Override
    public double getControlPointRadius() {
        return controlPointRadius;
    }

    @Override
    public List<FigureProperties> getFigureProperties() {
        return figureProperties;
    }

    @Override
    public int getFigurePropertiesCount() {
        return figureProperties.size();
    }

    @Override
    public void addFigureProperties(FigureProperties properties) {
        figureProperties.add(properties);
        addFigure(properties);
        notifyObservers(Event.FIGURE_PROPERTIES_ADDED);
    }

    private void updFigures() {
        scene.clear();
        for (FigureProperties figure : figureProperties) {
            addFigure(figure);
        }
    }

    private void addFigure(FigureProperties figureProperties) {
        LineProperties lineProperties = figureProperties.getLineProperties();
        if (lineProperties == null) {
            return;
        }

        Point2D[] funcValues = new Point2D[grid.getCols() * grid.getSegmentSplitting() + 1];
        double u = area.first.getX();
        double dU = area.getWidth() / (funcValues.length - 1);
        for(int i = 0; i < funcValues.length; ++i, u += dU) {
            if(Double.compare(u, area.second.getX()) > 0) {
                u = area.second.getX();
            }
            funcValues[i] = lineProperties.getPoint(u);
        }

        double[] sinuses = new double[grid.getRows() * grid.getSegmentSplitting() + 1];
        double[] cosines = new double[grid.getRows() * grid.getSegmentSplitting() + 1];
        double v = area.first.getY();
        double dV = area.getHeight() / (sinuses.length - 1);
        for(int i = 0; i < sinuses.length; ++i, v += dV) {
            sinuses[i] = Math.sin(v);
            cosines[i] = Math.cos(v);
        }

        PaintedFigure figure = new Figure(figureProperties);
        for(int i = 1; i < funcValues.length; i += grid.getSegmentSplitting()) {
            for(int j = 0; j < sinuses.length; j += grid.getSegmentSplitting()) {
                for(int q = 0; q < grid.getSegmentSplitting(); ++q) {
                    Point3D first = new Point3D(
                            funcValues[i - 1 + q].getY() * cosines[j],
                            funcValues[i - 1 + q].getY() * sinuses[j],
                            funcValues[i - 1 + q].getX()
                    );

                    Point3D second = new Point3D(
                            funcValues[i + q].getY() * cosines[j],
                            funcValues[i + q].getY() * sinuses[j],
                            funcValues[i + q].getX()
                    );

                    figure.addSegment(new Segment(first, second));
                }

            }
        }

        for(int j = 1; j < sinuses.length; j += grid.getSegmentSplitting()) {
            for(int i = 0; i < funcValues.length; i += grid.getSegmentSplitting()) {
                for(int q = 0; q < grid.getSegmentSplitting(); ++q) {
                    Point3D first = new Point3D(
                            funcValues[i].getY() * cosines[j - 1 + q],
                            funcValues[i].getY() * sinuses[j - 1 + q],
                            funcValues[i].getX()
                    );

                    Point3D second = new Point3D(
                            funcValues[i].getY() * cosines[j + q],
                            funcValues[i].getY() * sinuses[j + q],
                            funcValues[i].getX()
                    );

                    figure.addSegment(new Segment(first, second));
                }

            }
        }

        scene.addFigure(figure);
    }

    @Override
    public void delFigureProperties(int index) {
        figureProperties.remove(index);
        notifyObservers(Event.FIGURE_PROPERTIES_REMOVED);
    }

    @Override
    public CameraProperties getCameraProperties() {
        return cameraProperties;
    }
}
