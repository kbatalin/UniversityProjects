package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;

/**
 * Created by kir55rus on 05.03.17.
 */
public class FileSaver implements ISaver {
    private IPropertiesModel propertiesModel;
    private IFieldModel fieldModel;

    public FileSaver(IPropertiesModel propertiesModel, IFieldModel fieldModel) {
        this.propertiesModel = propertiesModel;
        this.fieldModel = fieldModel;
    }

    @Override
    public void save(File file) throws IOException {
        if (!file.canWrite()) {
            throw new IOException("Can't write");
        }

        Charset charset = Charset.forName("UTF-8");
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), charset)) {
            //weight height
            Dimension fieldSize = propertiesModel.getFieldSize();
            writer.write(String.valueOf(fieldSize.getWidth()) + " " + String.valueOf(fieldSize.height) + "\n");

            //line thickness
            writer.write(String.valueOf(propertiesModel.getLineThickness()) + "\n");

            //hex size
            writer.write(String.valueOf(propertiesModel.getHexSize()));

            //count life cells
            IField activeField = fieldModel.getActiveField();
            writer.write(String.valueOf(activeField.getLivingCellsCount()) + "\n");

            //field
            saveField(writer);
        }
    }

    private void saveField(BufferedWriter writer) throws IOException {
        IField field = fieldModel.getActiveField();

        Dimension size = field.getSize();
        for(Point pos = new Point(0, 0); pos.y < size.height; ++pos.y) {
            for(pos.x = 0; pos.x < size.width; ++pos.x) {
                CellState state = field.get(pos);
                if (state == CellState.DEAD) {
                    continue;
                }

                writer.write(String.valueOf(pos.x) + " " + String.valueOf(pos.y) + "\n");
            }
        }
    }
}
