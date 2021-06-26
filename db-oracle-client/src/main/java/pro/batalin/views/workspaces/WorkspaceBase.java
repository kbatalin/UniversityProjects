package pro.batalin.views.workspaces;

import javax.swing.*;

/**
 * @author Kirill Batalin (kir55rus)
 */
public abstract class WorkspaceBase extends JPanel {
    private WorkspaceType workspaceType;

    public WorkspaceBase(WorkspaceType workspaceType) {
        this.workspaceType = workspaceType;
    }

    public WorkspaceType getWorkspaceType() {
        return workspaceType;
    }
}
