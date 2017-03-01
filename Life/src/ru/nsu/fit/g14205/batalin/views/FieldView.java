package ru.nsu.fit.g14205.batalin.views;

import ru.nsu.fit.g14205.batalin.controllers.LifeController;
import ru.nsu.fit.g14205.batalin.models.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;
import java.util.Stack;
import java.util.function.Supplier;

/**
 * Created by kir55rus on 14.02.17.
 */
public class FieldView extends JLabel implements Observer {
    private LifeController lifeController;
    private IFieldModel fieldModel;
    private IPropertiesModel propertiesModel;

    private Dimension preferredSize;
    private int hexSize;
    private int hexIncircle;
    private int backgroundOffset;
    private Color aliveColor = Color.GREEN;


    public FieldView(LifeController lifeController, IFieldModel fieldModel, IPropertiesModel propertiesModel) {
        this.lifeController = lifeController;
        this.fieldModel = fieldModel;
        this.propertiesModel = propertiesModel;

        setSize(propertiesModel);

        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                lifeController.onFieldClick(e.getPoint());
            }
        });

        addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent mouseWheelEvent) {
                lifeController.onMouseWheelMoved(mouseWheelEvent);
            }
        });

        propertiesModel.addObserver(PropertiesModelEvent.SIZE_CHANGED, () -> {
            setSize(propertiesModel);
            repaint();
        });

        fieldModel.addObserver(FieldModelEvent.FIELD_UPDATED, this::repaint);
    }

    @Override
    public void update(Observable observable, Object o) {
    }

    @Override
    public void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        Dimension fieldSize = fieldModel.getActiveField().getSize();
        Rectangle clipBounds = graphics.getClipBounds();

        BufferedImage background = new BufferedImage(clipBounds.width + backgroundOffset+10, clipBounds.height + backgroundOffset+10, BufferedImage.TYPE_INT_RGB);
        Graphics2D backgroundGraphics = background.createGraphics();
        backgroundGraphics.setPaint(Color.WHITE);
        backgroundGraphics.fillRect(0, 0, background.getWidth(), background.getHeight());

//        System.out.println(clipBounds);

        int x0 = Math.max(0, clipBounds.x / (2 * hexIncircle) - 1);
        int x1 = Math.min(fieldSize.width, (clipBounds.x + clipBounds.width) / (2 * hexIncircle) + 1);
        int y0 = Math.max(0, clipBounds.y / (3 * hexSize / 2) - 1);
        int y1 = Math.min(fieldSize.height, (clipBounds.y + clipBounds.height) / (3 * hexSize / 2) + 1);

//        System.out.println("x0: " + x0 + ", x1: " + x1 + ", y0: " + y0 + ", y1: " + y1);

        drawField(background, x0, y0, x1, y1);

        int offsetX = Math.max(0, clipBounds.x - (clipBounds.x % (2 * hexIncircle)) - (2 * hexIncircle));
        int offsetY = Math.max(0, clipBounds.y - (clipBounds.y % (3 * hexSize / 2)) - (3 * hexSize / 2));

        graphics.drawImage(background, offsetX, offsetY, null);
    }

    private void setSize(IPropertiesModel propertiesModel) {
        Dimension fieldSize = fieldModel.getActiveField().getSize();

        this.hexSize = propertiesModel.getHexSize();
        this.hexIncircle = propertiesModel.getHexIncircle();
        this.backgroundOffset = 8 * this.hexSize;

        int preferredWidth = fieldSize.width * 2 * this.hexIncircle + 1;
        int preferredHeight = (fieldSize.height * 3  + 1) * this.hexSize / 2 + 1;
        this.preferredSize = new Dimension(preferredWidth, preferredHeight);
    }

    private void spanFill(BufferedImage image, Point seed, Color color) {
        Color oldColor = new Color(image.getRGB(seed.x, seed.y));
        if (color == null || color.equals(oldColor)) {
            return;
        }

        Stack<Rectangle> spans = new Stack<>();
        spans.push(getSpan(image, seed));
        while (!spans.empty()) {
            Rectangle span = spans.pop();

            for(int i = 0; i < span.width; ++i) {
                image.setRGB(span.x + i, span.y, color.getRGB());
            }

            findNearSpans(spans, image, span, oldColor);
        }
    }

    private void findNearSpans(Stack<Rectangle> spans, BufferedImage image, Rectangle span, Color oldColor) {
        for (int offset : new int[]{-1, 1}) {
            int y = span.y + offset;
            if (y < 0 || y >= image.getHeight()) {
                continue;
            }

            for (int i = 0; i < span.width;) {
                int x = i + span.x;
                if (image.getRGB(x, y) != oldColor.getRGB()) {
                    ++i;
                    continue;
                }
                Rectangle newSpan = getSpan(image, new Point(x, y));
                spans.push(newSpan);
                i += newSpan.width;
            }
        }
    }

    private Rectangle getSpan(BufferedImage image, Point crds) {
        int color = image.getRGB(crds.x, crds.y);

        int x0 = crds.x;
        int y = crds.y;

        while(x0 > 0 && color == image.getRGB(x0 - 1, y)) {
            --x0;
        }

        int x1 = crds.x;
        int width = image.getWidth() - 1;
        while (x1 < width && color == image.getRGB(x1 + 1, y)) {
            ++x1;
        }

        return new Rectangle(x0, y, x1 - x0 + 1, 1);
    }

    private void drawField(BufferedImage background, int x0, int y0, int x1, int y1) {
        IField field = fieldModel.getActiveField();
        Dimension fieldSize = field.getSize();
        Graphics2D backgroundGraphics = background.createGraphics();

        for(int y = y0; y < y1; ++y) {

            int currentLineCount = Math.min(fieldSize.width + (y % 2 == 0 ? 0 : -1), x1);

            for(int x = x0; x < currentLineCount; ++x) {

                int shownX = x - x0;
                int shownY = y - y0;

                int xCrd = hexIncircle + shownX * 2 * hexIncircle;
                int yCrd = hexSize + shownY * 3 * hexSize / 2;
                int offsetX = (y % 2) == 0 ? 0 : hexIncircle;
                drawHexagon(background, xCrd + offsetX, yCrd);

                if(field.get(x, y) == CellState.ALIVE) {
//                    System.out.println("Alive: " + new Point(x, y));
                    spanFill(background, new Point(xCrd + offsetX, yCrd), aliveColor);
                }
//                backgroundGraphics.setPaint(Color.BLACK);
//                backgroundGraphics.setFont(new Font("Dialog", Font.PLAIN, 12));
//                backgroundGraphics.drawString(x + ", " + y, xCrd + offsetX - hexIncircle / 2, yCrd);
            }
        }
    }

    private void drawHexagon(BufferedImage image, int x, int y) {
        Point[] points = new Point[6];
        points[0] = new Point(x,y + hexSize);
        points[1] = new Point(x + hexIncircle, y + hexSize / 2);
        points[2] = new Point(x + hexIncircle, y - hexSize / 2);
        points[3] = new Point(x, y - hexSize);
        points[4] = new Point(x - hexIncircle, y - hexSize / 2);
        points[5] = new Point(x - hexIncircle, y + hexSize / 2);

        drawPolygon(image, points);
    }

    private void drawPolygon(BufferedImage image, Point[] points) {
        for(int i = 0; i < points.length; ++i) {
            int next = (i + 1) % points.length;
            drawLine(image, points[i].x, points[i].y, points[next].x, points[next].y);
        }
    }

    private void drawLine(BufferedImage field, int x0, int y0, int x1, int y1) {
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);

        int dirX = dx == 0 ? 0 : (x1 - x0) / dx;
        int dirY = dy == 0 ? 0 : (y1 - y0) / dy;

        int maxLength;
        int minLength;
        int pdx;
        int pdy;
        if (dx > dy) {
            pdx = dirX;
            pdy = 0;
            minLength = dy;
            maxLength = dx;
        } else {
            pdx = 0;
            pdy = dirY;
            minLength = dx;
            maxLength = dy;
        }

        int x = x0;
        int y = y0;
        int err = maxLength / 2;
        field.setRGB(x, y, Color.BLACK.getRGB());

        for (int i = 0; i < maxLength; i++) {
            err -= minLength;
            if (err < 0) {
                err += maxLength;
                x += dirX;
                y += dirY;
            } else {
                x += pdx;
                y += pdy;
            }

            field.setRGB(x, y, Color.BLACK.getRGB());
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    @Override
    public Dimension getMinimumSize() {
        return preferredSize;
    }
}
