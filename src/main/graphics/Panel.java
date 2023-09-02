package main.graphics;

import javax.swing.*;
import java.awt.*;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Panel extends JPanel {

    private ArrayList<Shape> shapes;

    public int width = getWidth();
    public int height = getHeight();

    public Panel() {

        shapes = new ArrayList<>();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        Runnable refresh = new Runnable() {
            @Override
            public void run() {
                repaint();
                moveMouse();
            }
        };
        executor.scheduleAtFixedRate(refresh, 10, 10,  TimeUnit.MILLISECONDS);

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.setColor(Color.WHITE);

        Shape s = new Shape(10, 1, 10, ShapeType.CUBE, this);
        shapes.add(s);

        for (Shape shape : shapes) {

            shape.draw(g2);

        }

    }


    public void mouseDragged() {

    }

    public void moveMouse() {

        try {

            new Robot().mouseMove((int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2, (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2);

        } catch (AWTException ex) {
            ex.printStackTrace();
        }

    }
}
