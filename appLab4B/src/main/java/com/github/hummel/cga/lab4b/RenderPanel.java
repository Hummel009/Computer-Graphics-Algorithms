package com.github.hummel.cga.lab4b;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RenderPanel extends JPanel {
    private final List<Triangle> triangles;
    private final Matrix4 viewportMatrix;
    private final Matrix4 projectionMatrix;
    private final Camera camera;
    private final BufferedImage bufferedImage;
    private final double[] zBuffer;
    private final Graphics2D imgGraphics;
    private Matrix4 viewMatrix;
    private Matrix4 modelMatrix;
    private Matrix4 camMatrix;
    private int prevMouseX;
    private int prevMouseY;
    private double rotateY;
    private double rotateX;

    public RenderPanel(List<Triangle> triangles) {
        this.triangles = triangles;
        viewportMatrix = MatrixBuilder.buildViewport(Main.width, Main.height);
        projectionMatrix = MatrixBuilder.buildProjection(1.75, 90.0);
        double dist = 5.0;
        camera = new Camera();
        camera.eye = new Vector4(dist * Math.cos(rotateX) * Math.cos(rotateY), dist * Math.sin(rotateX), dist * Math.cos(rotateX) * Math.sin(rotateY));
        camera.target = new Vector4(0.0, 0.0, 0.0);
        camera.up = new Vector4(0.0, 1.0, 0.0);
        viewMatrix = MatrixBuilder.buildView(camera);
        bufferedImage = new BufferedImage(Main.width, Main.height, BufferedImage.TYPE_INT_RGB);
        imgGraphics = bufferedImage.createGraphics();
        zBuffer = new double[Main.width * Main.height];
        imgGraphics.setBackground(new Color(0, 0, 0, 0));

        setupCamMatrix();
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                if (rotateX > Math.PI / 2 - 0.1) {
                    rotateX = Math.PI / 2 - 0.05;
                } else if (rotateX < -Math.PI / 2 + 0.1) {
                    rotateX = -Math.PI / 2 - 0.05;
                }
                rotateY -= (e.getX() - prevMouseX) / 200.0;
                rotateX -= (e.getY() - prevMouseY) / 200.0;

                prevMouseX = e.getX();
                prevMouseY = e.getY();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                prevMouseX = e.getX();
                prevMouseY = e.getY();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g.setColor(Color.GREEN);

        double dist = Main.dist;
        camera.eye = new Vector4(dist * Math.cos(rotateX) * Math.cos(rotateY), dist * Math.sin(rotateX), dist * Math.cos(rotateX) * Math.sin(rotateY));
        viewMatrix = MatrixBuilder.buildView(camera);
        setupCamMatrix();

        Matrix4 finalMatrix = camMatrix;

        imgGraphics.clearRect(0, 0, Main.width, Main.height);
        var filteredList = AlgoUtils.filterTriangles(triangles, camera);
        var drawList = AlgoUtils.applyMatrix(filteredList, finalMatrix);
        for (Triangle triangle : drawList) {
            AtomicInteger i = new AtomicInteger(0);
            double[] depthArr = new double[3];
            for (Vector4 vector4 : triangle.vertices) {
                depthArr[i.getAndIncrement()] = vector4.get(3);
                vector4.divSelf(vector4.get(3));
            }
            triangle.depthArr = depthArr;
        }

        Collection<Triangle[]> newList = new ArrayList<>();
        for (int i = 0; i < filteredList.size(); i++) {
            newList.add(new Triangle[]{filteredList.get(i), drawList.get(i)});
        }
        Arrays.fill(zBuffer, Double.POSITIVE_INFINITY);

        newList.parallelStream().forEach(trianglePair -> {
            AlgoUtils.drawRasterTriangle(bufferedImage, trianglePair[0], trianglePair[1], zBuffer, camera);
        });

        g2d.drawImage(bufferedImage, 0, 0, Main.width, Main.height, null);
    }

    private void setupCamMatrix() {
        camMatrix = viewportMatrix.mul(projectionMatrix).mul(viewMatrix);
    }
}
