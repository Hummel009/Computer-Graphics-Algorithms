package com.github.hummel.cga.lab4b;

import java.awt.image.BufferedImage;
import java.util.*;

public class AlgoUtils {
    private AlgoUtils() {
    }

    public static List<Triangle> applyMatrix(Collection<Triangle> triangles, Matrix4 matrix) {
        return triangles.parallelStream().map(triangle -> {
            var newTriangle = new Triangle();
            newTriangle.normals = triangle.normals;
            newTriangle.textures = triangle.textures;
            List<Vector4> list = new ArrayList<>();
            for (var vertex : triangle.vertices) {
                list.add(matrix.mul(vertex));
            }
            newTriangle.vertices = list.toArray(new Vector4[0]);
            return newTriangle;
        }).toList();
    }

    private static Vector4 getNormal(Triangle t) {
        var seen = false;
        Vector4 acc = null;
        for (var normal : t.normals) {
            if (seen) {
                acc = acc.add(normal);
            } else {
                seen = true;
                acc = normal;
            }
        }
        return (seen ? Optional.of(acc) : Optional.<Vector4>empty()).map(vector4 -> vector4.div(3.0)).get();
    }

    public static List<Triangle> filterTriangles(Collection<Triangle> triangles, Camera camera) {
        return triangles.parallelStream().filter(t -> {
            var viewDir = t.vertices[0].subtract(camera.eye).normalize();
            var normal = getNormal(t);

            var cos = normal.dot(viewDir);
            return cos > 0;
        }).toList();
    }

    private static Vector4 getCenteredVecForPoint(Vector4[] vertices, double alpha, double beta, double gamma) {
        return vertices[0].mul(alpha).add(vertices[1].mul(beta)).add(vertices[2].mul(gamma));
    }

    private static double[] calculateBarycentricCoordinates(Triangle triangle, double x, double y) {
        var barycentricCoordinates = new double[3];

        var x1 = triangle.vertices[0].get(0);
        var y1 = triangle.vertices[0].get(1);
        var x2 = triangle.vertices[1].get(0);
        var y2 = triangle.vertices[1].get(1);
        var x3 = triangle.vertices[2].get(0);
        var y3 = triangle.vertices[2].get(1);

        var denominator = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);

        barycentricCoordinates[0] = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / denominator;
        barycentricCoordinates[1] = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / denominator;
        barycentricCoordinates[2] = 1 - barycentricCoordinates[0] - barycentricCoordinates[1];

        return barycentricCoordinates;
    }

    @SuppressWarnings("lossy-conversions")
    private static int applyBrightness(int color, double brightness) {
        var r = (color & 0x00ff0000) >> 16;
        var g = (color & 0x0000ff00) >> 8;
        var b = color & 0x000000ff;
        r *= brightness;
        g *= brightness;
        b *= brightness;
        return r << 16 | g << 8 | b;
    }

    public static void drawRasterTriangle(BufferedImage bufferedImage, Triangle worldTriangle, Triangle drawTriangle, double[] zBuffer, Camera camera) {
        var minY = Integer.MAX_VALUE;
        var maxY = Integer.MIN_VALUE;
        for (var vertex : drawTriangle.vertices) {
            var y = (int) vertex.get(1);
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        // Создать цикл по каждой строке изображения
        for (var y = minY; y <= maxY; y++) {
            // Найти пересечения текущей строки с каждой из сторон треугольника
            var xIntersections = new int[2];
            var intersectionCount = 0;
            for (var i = 0; i < 3; i++) {
                var v0 = drawTriangle.vertices[i];
                var v1 = drawTriangle.vertices[(i + 1) % 3];
                var y0 = (int) v0.get(1);
                var y1 = (int) v1.get(1);
                if (y0 <= y && y < y1 || y1 <= y && y < y0) {
                    var t = (y - y0) / (double) (y1 - y0);
                    var x = (int) (v0.get(0) + t * (v1.get(0) - v0.get(0)));
                    xIntersections[intersectionCount] = x;
                    intersectionCount++;
                }
            }

            // Отсортировать пересечения по возрастанию
            if (intersectionCount == 2 && xIntersections[0] > xIntersections[1]) {
                var temp = xIntersections[0];
                xIntersections[0] = xIntersections[1];
                xIntersections[1] = temp;
            }

            // Заполнить пиксели между пересечениями цветом треугольника
            if (intersectionCount == 2) {
                for (var x = xIntersections[0]; x <= xIntersections[1]; x++) {
                    var id = x * Main.height + y;
                    if (id < 0 || id >= Main.width * Main.height) {
                        continue;
                    }

                    // Вычисление z-фрагмента
                    var barycCords = calculateBarycentricCoordinates(drawTriangle, x, y);

                    var v0 = drawTriangle.vertices[0];
                    var v1 = drawTriangle.vertices[1];
                    var v2 = drawTriangle.vertices[2];
                    var alpha = barycCords[0];
                    var beta = barycCords[1];
                    var gamma = barycCords[2];
                    alpha /= drawTriangle.depthArr[0];
                    beta /= drawTriangle.depthArr[1];
                    gamma /= drawTriangle.depthArr[2];
                    var sum = alpha + beta + gamma;
                    alpha /= sum;
                    beta /= sum;
                    gamma /= sum;

                    var zFragment = alpha * v0.get(2) + beta * v1.get(2) + gamma * v2.get(2);

                    final var ambientCoeff = 0.0;
                    final var diffuseCoeff = 0.5;
                    final var specularCoeff = 0.6;

                    // Проверка z-буфера
                    if (zBuffer[x * Main.height + y] > zFragment) {
                        // cчитаем diffuse
                        var texVec = getCenteredVecForPoint(worldTriangle.textures, alpha, beta, gamma);
                        texVec = new Vector4(texVec.get(0), 1.0 - texVec.get(1), 0.0);
                        var texX = (int) (texVec.get(0) * Main.textureImage.getWidth()) % Main.textureImage.getWidth();
                        var texY = (int) (texVec.get(1) * Main.textureImage.getHeight()) % Main.textureImage.getHeight();

                        if (texX > Main.textureImage.getWidth() - 1) {
                            texX = Main.textureImage.getWidth() - 1;
                        }
                        if (texX < 0) {
                            texX = 0;
                        }
                        if (texY > Main.textureImage.getWidth() - 1) {
                            texY = Main.textureImage.getWidth() - 1;
                        }
                        if (texY < 0) {
                            texY = 0;
                        }

                        var normalData = Main.normalMapImage.getRGB(texX, texY);
                        var normal = new Vector4((normalData >> 16 & 0x000000ff) / 256.0 * 2.0 - 1.0, (normalData >> 8 & 0x000000ff) / 256.0 * 2.0 - 1.0, (normalData & 0x000000ff) / 256.0 * 2.0 - 1.0).mul(-1.0);

                        var mraoData = Main.mraoImage.getRGB(texX, texY);
                        var mraoVec = new Vector4((mraoData >> 16 & 0x000000ff) / 256.0, (mraoData >> 8 & 0x000000ff) / 256.0, (mraoData & 0x000000ff) / 256.0);

                        var pos = getCenteredVecForPoint(worldTriangle.vertices, alpha, beta, gamma);
                        camera.target.subtract(camera.eye).normalize();
                        var lightPos = new Vector4(5.0, 5.0, 5.0);
                        var ray = pos.subtract(lightPos).normalize();
                        var diffuse = Math.max(normal.dot(ray) * diffuseCoeff, 0.0);

                        // считаем specular
                        var specular = 0.0;
                        var l = lightPos.subtract(pos);
                        double s = 10;
                        var angle = normal.dot(l);

                        var r = normal.mul(angle).mul(2.0).subtract(l);
                        var v = camera.eye.subtract(pos);
                        var rDotV = Math.max(r.dot(v), 0.0);
                        if (rDotV > 0) {
                            specular = Math.pow(rDotV / (r.len() * v.len()), s);
                        }

                        zBuffer[x * Main.height + y] = zFragment;

                        var colorValCoeff = ambientCoeff + diffuse * diffuseCoeff + specular * mraoVec.get(0) * specularCoeff;

                        var texColor = Main.textureImage.getRGB(texX, texY);
                        texColor = applyBrightness(texColor, colorValCoeff);

                        bufferedImage.setRGB(x, y, texColor);
                    }
                }
            }
        }
    }
}
