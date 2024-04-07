package com.github.hummel.cga.lab4b;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static final int width = 1400;
    public static final int height = 800;
    private static final String modelPath = "box.obj";
    private static final String texturePath = "box_texture.bmp";
    private static final String normalMapPath = "box_normal.bmp";
    private static final String mraoPath = "box_mrao.bmp";
    public static final BufferedImage textureImage;
    public static final BufferedImage normalMapImage;
    public static final BufferedImage mraoImage;
    public static final double dist = 3.0; // dist

    static {
        try {
            mraoImage = ImageIO.read(new File(mraoPath));
            normalMapImage = ImageIO.read(new File(normalMapPath));
            textureImage = ImageIO.read(new File(texturePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // teapot 8.0
    // destroyer 3000.0
    // doom 300.0

    public static void main(String[] args) throws Exception {
        List<Vector4> vertexList = new ArrayList<>();
        List<Vector4> normalList = new ArrayList<>();
        List<Vector4> textureList = new ArrayList<>();
        List<Triangle> triangleList = new ArrayList<>();

        try (var bufferedReader = new BufferedReader(new FileReader(modelPath))) {
            String line;
            while (Objects.nonNull(line = bufferedReader.readLine())) {
                if (line.startsWith("v ")) {
                    vertexList.add(ParsingHelper.extractVertex(line));
                } else if (line.startsWith("vn ")) {
                    normalList.add(ParsingHelper.extractNormal(line));
                } else if (line.startsWith("vt ")) {
                    textureList.add(ParsingHelper.extractTexture(line));
                } else if (line.startsWith("f ")) {
                    triangleList.add(ParsingHelper.extractTriangle(line, vertexList, textureList, normalList));
                }
            }
        }

        var canvas = new Canvas(triangleList);
        canvas.setVisible(true);

        while (true) {
            canvas.repaint();
        }
    }
}