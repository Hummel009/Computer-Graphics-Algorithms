package com.github.hummel.cga.lab4b;

import javax.swing.*;
import java.util.List;

public class Canvas extends JFrame {

    public Canvas(List<Triangle> triangles) {
        setTitle("My frame");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(Main.width, Main.height);

        JPanel drawingPanel = new RenderPanel(triangles);
        drawingPanel.setSize(Main.width, Main.height);

        add(drawingPanel);
    }
}
