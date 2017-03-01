package ru.nsu.fit.g14205.batalin.models;

import java.util.Observable;

/**
 * Created by kir55rus on 20.02.17.
 */
public class FieldModel extends Observable implements IFieldModel {
    private IField[] fields;
    private int activeField;

    public FieldModel(IPropertiesModel propertiesModel) {
        activeField = 0;
        fields = new IField[]{new Field(propertiesModel.getFieldSize()), new Field(propertiesModel.getFieldSize())};
    }

    @Override
    public IField getActiveField() {
        return fields[activeField];
    }
}
