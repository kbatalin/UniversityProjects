package ru.nsu.fit.g14205.batalin.models;

/**
 * Created by Kirill Batalin (kir55rus) on 26.04.17.
 */
public class FigurePropertiesDefault implements FigureProperties {
    private LineProperties lineProperties;
    private CoordinateSystem coordinateSystem;

    public FigurePropertiesDefault() {
        this(null, new CoordinateSystem());
    }

    public FigurePropertiesDefault(LineProperties lineProperties) {
        this(lineProperties, new CoordinateSystem());
    }

    public FigurePropertiesDefault(LineProperties lineProperties, CoordinateSystem coordinateSystem) {
        this.lineProperties = lineProperties;
        this.coordinateSystem = coordinateSystem;
    }

    @Override
    public LineProperties getLineProperties() {
        return lineProperties;
    }

    @Override
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    @Override
    public FigureProperties clone() throws CloneNotSupportedException {
        FigurePropertiesDefault figureProperties = (FigurePropertiesDefault) super.clone();
        figureProperties.coordinateSystem = coordinateSystem.clone();
        if(lineProperties == null) {
            figureProperties.lineProperties = null;
        } else {
            figureProperties.lineProperties = lineProperties.clone();
        }
        return figureProperties;
    }
}
