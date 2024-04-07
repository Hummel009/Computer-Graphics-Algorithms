package com.github.hummel.cga.lab4b;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ParsingHelper {
    private ParsingHelper() {
    }

    public static Vector4 extractVertex(String line) {
        List<Double> list = new ArrayList<>();
        for (String s : line.replace("v ", "").split(" ")) {
            Double parseDouble = Double.parseDouble(s);
            list.add(parseDouble);
        }
        Double[] dList = list.toArray(new Double[0]);
        return new Vector4(dList[0], dList[1], dList[2]);
    }

    public static Vector4 extractNormal(String line) {
        List<Double> list = new ArrayList<>();
        for (String s : line.replace("vn ", "").split(" ")) {
            Double parseDouble = Double.parseDouble(s);
            list.add(parseDouble);
        }
        Double[] dList = list.toArray(new Double[0]);
        return new Vector4(dList[0], dList[1], dList[2]);
    }

    public static Vector4 extractTexture(String line) {
        List<Double> list = new ArrayList<>();
        for (String s : line.replace("vt ", "").split(" ")) {
            Double parseDouble = Double.parseDouble(s);
            list.add(parseDouble);
        }
        Double[] dList = list.toArray(new Double[0]);
        return new Vector4(dList[0], dList[1], 0.0);
    }

    public static Triangle extractTriangle(String line, List<Vector4> vertices, List<Vector4> textures, List<Vector4> normals) {
        Triangle result = new Triangle();
        result.vertices = new Vector4[3];
        result.textures = new Vector4[3];
        result.normals = new Vector4[3];

		String line1 = line.replace("f ", "");
        AtomicInteger vIndex = new AtomicInteger(0);
        AtomicInteger nIndex = new AtomicInteger(0);
        AtomicInteger tIndex = new AtomicInteger(0);
        for (String group : line1.split(" ")) {
            List<Integer> list = new ArrayList<>();
            for (String s : group.split("/")) {
                Integer parseInt = Integer.parseInt(s);
                list.add(parseInt);
            }
            Integer[] idList = list.toArray(new Integer[0]);
            result.vertices[vIndex.getAndIncrement()] = vertices.get(idList[0] - 1);
            result.textures[tIndex.getAndIncrement()] = textures.get(idList[1] - 1);
            result.normals[nIndex.getAndIncrement()] = normals.get(idList[2] - 1).normalize().mul(-1.0);
        }
        return result;
    }
}
