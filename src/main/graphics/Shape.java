package main.graphics;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Shape {

    private int size;
    private int x;
    private int y;
    private int z;

    private ShapeType type;

    private ArrayList<Triangle> tris;

    private Panel panel;

    public Shape(int x, int y, int z, ShapeType type, Panel panel) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.type = type;
        this.panel = panel;

        if (type == ShapeType.CUBE) {
            initCubeTris();
        }

    }

    public void draw(Graphics2D g2) {

        double heading = Math.toRadians(0);
        Matrix3 headingTransform = new Matrix3(new double[]{
                Math.cos(heading), 0, -Math.sin(heading),
                0, 1, 0,
                Math.sin(heading), 0, Math.cos(heading)
        });
        double pitch = Math.toRadians(0);
        Matrix3 pitchTransform = new Matrix3(new double[]{
                1, 0, 0,
                0, Math.cos(pitch), Math.sin(pitch),
                0, -Math.sin(pitch), Math.cos(pitch)
        });
        /* Matrix3 zoomTransform = new Matrix3(new double[]{
                3 / distToPlayer, 0, 0,
                0, 3 / distToPlayer, 0,
                0, 0, 3 / distToPlayer
        });

         */

        Matrix3 transform = headingTransform.multiply(pitchTransform);

        BufferedImage img = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);

        double[] zBuffer = new double[img.getWidth() * img.getHeight()];
        // initialize array with extremely far away depths
        for (int q = 0; q < zBuffer.length; q++) {
            zBuffer[q] = Double.NEGATIVE_INFINITY;
        }

        applyTransforms(transform, zBuffer, img);

        g2.drawImage(img, 0, 0, null);

    }

    public void applyTransforms(Matrix3 transform, double[] zBuffer, BufferedImage img) {

            for (Triangle t : tris) {

                Vertex v1 = transform.transform(t.v1);
                v1.x += panel.getWidth() / 2;
                v1.y += panel.getHeight() / 2;
                Vertex v2 = transform.transform(t.v2);
                v2.x += panel.getWidth() / 2;
                v2.y += panel.getHeight() / 2;
                Vertex v3 = transform.transform(t.v3);
                v3.x += panel.getWidth() / 2;
                v3.y += panel.getHeight() / 2;

                Vertex ab = new Vertex(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z);
                Vertex ac = new Vertex(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z);
                Vertex norm = new Vertex(
                        ab.y * ac.z - ab.z * ac.y,
                        ab.z * ac.x - ab.x * ac.z,
                        ab.x * ac.y - ab.y * ac.x
                );
                double normalLength = Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z);
                norm.x /= normalLength;
                norm.y /= normalLength;
                norm.z /= normalLength;

                double angleCos = Math.abs(norm.z);

                int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                int maxX = (int) Math.min(img.getWidth() - 1, Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                int maxY = (int) Math.min(img.getHeight() - 1, Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                double triangleArea = (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                for (int y = minY; y <= maxY; y++) {
                    for (int x = minX; x <= maxX; x++) {
                        double b1 = ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                        double b2 = ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                        double b3 = ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                        if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                            double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                            int zIndex = y * img.getWidth() + x;
                            if (zBuffer[zIndex] < depth) {
                                img.setRGB(x, y, getShade(t.color, angleCos).getRGB());
                                zBuffer[zIndex] = depth;
                            }
                        }
                    }

                }

            }

        }

    public Color getShade(Color color, double shade) {
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);

        return new Color(red, green, blue);
    }

    public void initCubeTris () {

            ArrayList<Triangle> tris = new ArrayList<>();

            // Back

            tris.add(new Triangle(new Vertex(-100, -100, 100),
                    new Vertex(100, -100, 100),
                    new Vertex(100, 100, 100),
                    Color.GRAY));
            tris.add(new Triangle(new Vertex(-100, -100, 100),
                    new Vertex(-100, 100, 100),
                    new Vertex(100, 100, 100),
                    Color.GRAY));

            // Front

            tris.add(new Triangle(new Vertex(-100, -100, -100),
                    new Vertex(100, -100, -100),
                    new Vertex(100, 100, -100),
                    Color.GRAY));
            tris.add(new Triangle(new Vertex(-100, -100, -100),
                    new Vertex(-100, 100, -100),
                    new Vertex(100, 100, -100),
                    Color.GRAY));

            // Left

            tris.add(new Triangle(new Vertex(-100, -100, -100),
                    new Vertex(-100, -100, 100),
                    new Vertex(-100, 100, 100),
                    Color.GRAY));
            tris.add(new Triangle(new Vertex(-100, -100, 100),
                    new Vertex(-100, 100, -100),
                    new Vertex(-100, 100, 100),
                    Color.GRAY));

            // Right

            tris.add(new Triangle(new Vertex(100, -100, -100),
                    new Vertex(100, -100, 100),
                    new Vertex(100, 100, 100),
                    Color.GRAY));
            tris.add(new Triangle(new Vertex(100, -100, 100),
                    new Vertex(100, 100, -100),
                    new Vertex(100, 100, 100),
                    Color.GRAY));

            // Top

            tris.add(new Triangle(new Vertex(-100, -100, -100),
                    new Vertex(100, -100, -100),
                    new Vertex(-100, -100, 100),
                    Color.GRAY));
            tris.add(new Triangle(new Vertex(-100, -100, -100),
                    new Vertex(-100, -100, 100),
                    new Vertex(100, -100, 100),
                    Color.GRAY));

            // Bottom

            tris.add(new Triangle(new Vertex(-100, 100, -100),
                    new Vertex(100, 100, -100),
                    new Vertex(-100, 100, 100),
                    Color.GRAY));
            tris.add(new Triangle(new Vertex(-100, 100, -100),
                    new Vertex(-100, 100, 100),
                    new Vertex(100, 100, 100),
                    Color.GRAY));

            this.tris = tris;

        }

        // SETTERS

        public void setSize ( int size){
            this.size = size;
        }

        // GETTERS

        public ArrayList<Triangle> getTris () {
            return tris;
        }
}
