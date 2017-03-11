package ru.nsu.fit.g14205.batalin.models;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Created by kir55rus on 05.03.17.
 */
public class FileLoader implements ILoader {
    private IPropertiesModel propertiesModel;
    private IFieldModel fieldModel;
    private Path path;

    public FileLoader(Path file) {
        path = file;
    }

    @Override
    public void load() throws IOException {
        if (path == null) {
            throw new IOException("Bad path");
        }


        StringBuilder builder = new StringBuilder();
        try(Scanner scanner = new Scanner(path, "UTF-8")) {
            loadProperties(scanner);
            loadField(scanner);
        }

    }

    private void loadProperties(Scanner scanner) throws IOException {
        propertiesModel = PropertiesModel.createDefault();

        propertiesModel.setSavePath(path);

        //width, height
        if (!scanner.hasNextInt()) {
            throw new IOException("Bad format");
        }
        int width = scanner.nextInt();

        if (!scanner.hasNextInt()) {
            throw new IOException("Bad format");
        }
        int height = scanner.nextInt();
        scanner.nextLine();

        propertiesModel.setFieldSize(new Dimension(width, height));

        //line thickness
        if (!scanner.hasNextInt()) {
            throw new IOException("Bad format");
        }
        propertiesModel.setLineThickness(scanner.nextInt());
        scanner.nextLine();

        //hex size
        if (!scanner.hasNextInt()) {
            throw new IOException("Bad format");
        }
        propertiesModel.setHexSize(scanner.nextInt());
        scanner.nextLine();
    }

    private void loadField(Scanner scanner) throws IOException {
        fieldModel = new FieldModel(propertiesModel);

        if (!scanner.hasNextInt()) {
            throw new IOException("Bad format");
        }
        int aliveCount = scanner.nextInt();
        scanner.nextLine();

        for(int i = 0; i < aliveCount; ++i) {
            if (!scanner.hasNextInt()) {
                throw new IOException("Bad format");
            }
            int x = scanner.nextInt();

            if (!scanner.hasNextInt()) {
                throw new IOException("Bad format");
            }
            int y = scanner.nextInt();
            scanner.nextLine();

            fieldModel.getActiveField().set(x, y, CellState.ALIVE);
        }
    }

    @Override
    public IPropertiesModel getPropertiesModel() {
        return propertiesModel;
    }

    @Override
    public IFieldModel getFieldModel() {
        return fieldModel;
    }
}
