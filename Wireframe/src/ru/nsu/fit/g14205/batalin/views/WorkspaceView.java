package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.WireframeController;
import ru.nsu.fit.g14205.batalin.models.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kir55rus on 11.04.17.
 */
public class WorkspaceView extends JComponent {
    private WireframeController wireframeController;
    private int margins = 10;

    public WorkspaceView(WireframeController wireframeController) {
        this.wireframeController = wireframeController;
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Dimension componentSize = getSize();
        graphics.setColor(Color.BLACK);
        graphics.fillRect(0, 0, componentSize.width, componentSize.height);

        ApplicationProperties applicationProperties = wireframeController.getApplicationProperties();
        ViewPyramidProperties viewPyramid = applicationProperties.getViewPyramidProperties();

        double viewPortSizeRatio = Math.min((componentSize.getWidth() - 2*margins) / viewPyramid.getFrontPlaneWidth(),
                (componentSize.getHeight() - 2* margins) / viewPyramid.getFrontPlaneHeight());
        int viewPortWidth = (int)(viewPyramid.getFrontPlaneWidth() * viewPortSizeRatio);
        int viewPortHeight = (int)(viewPyramid.getFrontPlaneHeight() * viewPortSizeRatio);
        Rectangle viewPort = new Rectangle((componentSize.width - viewPortWidth) / 2, (componentSize.height - viewPortHeight) / 2, viewPortWidth, viewPortHeight);

        graphics.setColor(Color.BLUE);
        graphics.drawRect(viewPort.x, viewPort.y, viewPort.width, viewPort.height);

        Scene scene = getTestScene();

        Matrix worldToCamMatrix = getWorldToCamMatrix();

        Matrix pos = scene.getOutboardBox().getPos().toMatrix4();
        pos = worldToCamMatrix.multiply(pos);

        Matrix projectionMatrix = getProjectionMatrix();
        pos = projectionMatrix.multiply(pos);
        pos = pos.multiply(1. / pos.get(0, 3));

        System.out.println(pos);
    }

    private Scene getTestScene() {
        List<Segment> segments = new ArrayList<>();
        segments.add(new Segment(new Point3D(0, 0, 0), new Point3D(3, 0, 0)));
        segments.add(new Segment(new Point3D(0, 0, 0), new Point3D(0, 3, 0)));
        segments.add(new Segment(new Point3D(0, 0, 0), new Point3D(0, 0, 3)));
        segments.add(new Segment(new Point3D(0, 3, 0), new Point3D(0, 0, 3)));
        segments.add(new Segment(new Point3D(3, 0, 0), new Point3D(0, 0, 3)));
        segments.add(new Segment(new Point3D(3, 0, 0), new Point3D(0, 3, 0)));

        return new Scene(segments);
    }

    private Matrix getWorldToCamMatrix() {
        ApplicationProperties applicationProperties = wireframeController.getApplicationProperties();
        CameraProperties camera = applicationProperties.getCameraProperties();

        Point3D cam = camera.getCameraPosition();
        Point3D camZ = new Point3D(cam.toMatrix3().deduct(camera.getViewPoint().toMatrix3()).normalize());
        Point3D camY = new Point3D(camera.getUpVector().toMatrix3().normalize());
        Point3D camX = new Point3D(new Matrix(3, 3, new double[]{
                0, -camY.getZ(), camY.getY(),
                camY.getZ(), 0, -camY.getX(),
                -camY.getY(), camY.getX(), 0,
        }).multiply(camZ.toMatrix3()).normalize());
        Matrix MRotateCam = new Matrix(3,3, new double[]{
                camX.getX(), camX.getY(), camX.getZ(),
                camY.getX(), camY.getY(), camY.getZ(),
                camZ.getX(), camZ.getY(), camZ.getZ(),
        });
        Matrix MOffsetCam = MRotateCam.multiply(cam.toMatrix3());

        Matrix Mcam = new Matrix(4,4, new double[]{
                MRotateCam.get(0,0), MRotateCam.get(1,0), MRotateCam.get(2,0), -MOffsetCam.get(0,0),
                MRotateCam.get(0,1), MRotateCam.get(1,1), MRotateCam.get(2,1), -MOffsetCam.get(0,1),
                MRotateCam.get(0,2), MRotateCam.get(1,2), MRotateCam.get(2,2), -MOffsetCam.get(0,2),
                0, 0, 0, 1
        });

        return Mcam;
    }

    private Matrix getProjectionMatrix() {
        ApplicationProperties applicationProperties = wireframeController.getApplicationProperties();
        ViewPyramidProperties viewPyramid = applicationProperties.getViewPyramidProperties();

        double zf = viewPyramid.getFrontPlaneDistance();
        double zb = viewPyramid.getBackPlaneDistance();
        double sw = viewPyramid.getFrontPlaneWidth();
        double sh = viewPyramid.getFrontPlaneHeight();
        Matrix MProj = new Matrix(4, 4, new double[]{
                2 * -zf / sw, 0, 0, 0,
                0, 2 * -zf / sh, 0, 0,
                0, 0, zf / (zb - zf), zf * zb / (zb - zf),
                0, 0, 1, 0
        });

        return MProj;
    }
}
