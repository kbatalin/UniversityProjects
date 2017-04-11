package ru.nsu.fit.g14205.batalin.controllers;

import ru.nsu.fit.g14205.batalin.models.ApplicationProperties;
import ru.nsu.fit.g14205.batalin.models.EditorModel;
import ru.nsu.fit.g14205.batalin.views.EditorDialog;

/**
 * Created by kir55rus on 12.04.17.
 */
public class EditorController {
    private WireframeController wireframeController;
    private ApplicationProperties applicationProperties;
    private EditorModel editorModel;

    public EditorController(WireframeController wireframeController) {
        this.wireframeController = wireframeController;
        this.applicationProperties = wireframeController.getApplicationProperties();
        this.editorModel = new EditorModel(applicationProperties);
    }

    public void run() {
        EditorDialog dialog = new EditorDialog(this);
        dialog.pack();
        dialog.setLocationRelativeTo(wireframeController.getWireframeView());
        dialog.setVisible(true);
    }

    public EditorModel getEditorModel() {
        return editorModel;
    }

    public ApplicationProperties getApplicationProperties() {
        return applicationProperties;
    }
}
