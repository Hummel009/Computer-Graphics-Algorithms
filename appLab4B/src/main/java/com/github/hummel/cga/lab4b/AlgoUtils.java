package com.github.hummel.cga.lab4b;

import java.awt.image.BufferedImage;
import java.util.*;

public class AlgoUtils {
    private AlgoUtils() {
    }

    public static Vector4 getCenter(Vector4[] triangle) {
        Vector4 sum = new Vector4(0.0, 0.0, 0.0);
        for (int i = 0; i < 3; i++) {
            sum = sum.add(triangle[i]);
        }
        return sum.div(3.0);
    }

    public static void drawLine(BufferedImage image, int x1, int y1, int x2, int y2) {
        int x11 = x1;
        int y11 = y1;
        int width = image.getWidth();
        int height = image.getHeight();

        int dx = Math.abs(x2 - x11);
        int dy = Math.abs(y2 - y11);
        int sx = x11 < x2 ? 1 : -1;
        int sy = y11 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x11 >= 0 && x11 < width && y11 >= 0 && y11 < height) {
                image.setRGB(x11, y11, 0xff00ff00);
            }

            if (x11 == x2 && y11 == y2) {
                break;
            }

            int err2 = 2 * err;

            if (err2 > -dy) {
                err -= dy;
                x11 += sx;
            }

            if (err2 < dx) {
                err += dx;
                y11 += sy;
            }
        }
    }

    public static List<Triangle> applyMatrix(Collection<Triangle> triangles, Matrix4 matrix) {
        return triangles.parallelStream().map(triangle -> {
            Triangle newTriangle = new Triangle();
            newTriangle.normals = triangle.normals;
            newTriangle.textures = triangle.textures;
            List<Vector4> list = new ArrayList<>();
            for (Vector4 vertex : triangle.vertices) {
                list.add(matrix.mul(vertex));
            }
            newTriangle.vertices = list.toArray(new Vector4[0]);
            return newTriangle;
        }).toList();
    }

    private static Vector4 getNormal(Triangle t) {
        boolean seen = false;
        Vector4 acc = null;
        for (Vector4 normal : t.normals) {
            if (seen) {
                acc = acc.add(normal);
            } else {
                seen = true;
                acc = normal;
            }
        }
        return (seen ? Optional.of(acc) : Optional.<Vector4>empty()).map(vector4 -> vector4.div(3.0)).get();
    }

    public static List<Triangle> addNormals(Iterable<Triangle> triangles) {
        List<Triangle> list = new ArrayList<>();
        for (Triangle triangle : triangles) {
            Vector4[] vert = triangle.vertices;
            Vector4 vec1 = vert[1].subtract(vert[0]);
            Vector4 vec2 = vert[2].subtract(vert[1]);
            Vector4 normal = vec2.cross(vec1).normalize();
            Vector4[] newArr = Arrays.copyOf(vert, 4);
            newArr[3] = normal;
            triangle.vertices = newArr;
            list.add(triangle);
        }
        return list;
    }

    public static List<Triangle> filterTriangles(Collection<Triangle> triangles, Camera camera) {
        return triangles.parallelStream().filter(t -> {
            Vector4 viewDir = t.vertices[0].subtract(camera.eye).normalize();
            Vector4 normal = getNormal(t);

            double cos = normal.dot(viewDir);
            return cos > 0;
        }).toList();
    }

    private static Vector4 getCenteredVecForPoint(Vector4[] vertices, double alpha, double beta, double gamma) {
        return vertices[0].mul(alpha).add(vertices[1].mul(beta)).add(vertices[2].mul(gamma));
    }

    public static Vector4 getTextureCoordinate(Triangle drawTriangle, double alpha, double beta, double gamma, double zFragment) {
        Vector4[] textures = drawTriangle.textures;
        Vector4[] vertices = drawTriangle.vertices;

        Vector4 coords = textures[0].mul(alpha).add(textures[1].mul(beta)).add(textures[2].mul(gamma));

        return coords.div(zFragment);
    }

    public static boolean anyDoesNotFit(double[] cords) {
        for (double value : cords) {
            if (value <= 0 || value >= 1) {
                return true;
            }
        }
        return false;
    }

    private static double[] calculateBarycentricCoordinates(Triangle triangle, double x, double y) {
        double[] barycentricCoordinates = new double[3];

        double x1 = triangle.vertices[0].get(0);
        double y1 = triangle.vertices[0].get(1);
        double x2 = triangle.vertices[1].get(0);
        double y2 = triangle.vertices[1].get(1);
        double x3 = triangle.vertices[2].get(0);
        double y3 = triangle.vertices[2].get(1);

        double denominator = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);

        barycentricCoordinates[0] = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / denominator;
        barycentricCoordinates[1] = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / denominator;
        barycentricCoordinates[2] = 1 - barycentricCoordinates[0] - barycentricCoordinates[1];

        return barycentricCoordinates;
    }

    private static int applyBrightness(int color, double brightness) {
        int r = (color & 0x00ff0000) >> 16;
        int g = (color & 0x0000ff00) >> 8;
        int b = color & 0x000000ff;
        r *= brightness;
        g *= brightness;
        b *= brightness;
        return r << 16 | g << 8 | b;
    }

    public static void drawRasterTriangle(BufferedImage bufferedImage, Triangle worldTriangle, Triangle drawTriangle, double[] zBuffer, Camera camera) {
        int minY = Integer.MAX_VALUE;
        int maxY = Integer.MIN_VALUE;
        for (Vector4 vertex : drawTriangle.vertices) {
            int y = (int) vertex.get(1);
            if (y < minY) {
                minY = y;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        // Создать цикл по каждой строке изображения
        for (int y = minY; y <= maxY; y++) {
            // Найти пересечения текущей строки с каждой из сторон треугольника
            int[] xIntersections = new int[2];
            int intersectionCount = 0;
            for (int i = 0; i < 3; i++) {
                Vector4 v0 = drawTriangle.vertices[i];
                Vector4 v1 = drawTriangle.vertices[(i + 1) % 3];
                int y0 = (int) v0.get(1);
                int y1 = (int) v1.get(1);
                if (y0 <= y && y < y1 || y1 <= y && y < y0) {
                    double t = (y - y0) / (double) (y1 - y0);
                    int x = (int) (v0.get(0) + t * (v1.get(0) - v0.get(0)));
                    xIntersections[intersectionCount] = x;
                    intersectionCount++;
                }
            }

            // Отсортировать пересечения по возрастанию
            if (intersectionCount == 2 && xIntersections[0] > xIntersections[1]) {
                int temp = xIntersections[0];
                xIntersections[0] = xIntersections[1];
                xIntersections[1] = temp;
            }

            // Заполнить пиксели между пересечениями цветом треугольника
            if (intersectionCount == 2) {
                for (int x = xIntersections[0]; x <= xIntersections[1]; x++) {
                    int id = x * Main.height + y;
                    if (id < 0 || id >= Main.width * Main.height) {
                        continue;
                    }

                    // Вычисление z-фрагмента
                    double[] barycCords = calculateBarycentricCoordinates(drawTriangle, x, y);

                    Vector4 v0 = drawTriangle.vertices[0];
                    Vector4 v1 = drawTriangle.vertices[1];
                    Vector4 v2 = drawTriangle.vertices[2];
                    double alpha = barycCords[0];
                    double beta = barycCords[1];
                    double gamma = barycCords[2];
                    alpha /= drawTriangle.depthArr[0];
                    beta /= drawTriangle.depthArr[1];
                    gamma /= drawTriangle.depthArr[2];
                    double sum = alpha + beta + gamma;
                    alpha /= sum;
                    beta /= sum;
                    gamma /= sum;

                    double zFragment = alpha * v0.get(2) + beta * v1.get(2) + gamma * v2.get(2);

                    final double ambientCoeff = 0.0;
                    final double diffuseCoeff = 0.5;
                    final double specularCoeff = 0.6;
                    final double specularPower = 32.0;

                    // Проверка z-буфера
                    if (zBuffer[x * Main.height + y] > zFragment) {
                        // cчитаем diffuse
                        Vector4 texVec = getCenteredVecForPoint(worldTriangle.textures, alpha, beta, gamma);
                        texVec = new Vector4(texVec.get(0), 1.0 - texVec.get(1), 0.0);
                        int texX = (int) (texVec.get(0) * Main.textureImage.getWidth()) % Main.textureImage.getWidth();
                        int texY = (int) (texVec.get(1) * Main.textureImage.getHeight()) % Main.textureImage.getHeight();

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

                        int normalData = Main.normalMapImage.getRGB(texX, texY);
                        Vector4 normal = new Vector4((normalData >> 16 & 0x000000ff) / 256.0 * 2.0 - 1.0, (normalData >> 8 & 0x000000ff) / 256.0 * 2.0 - 1.0, (normalData & 0x000000ff) / 256.0 * 2.0 - 1.0).mul(-1.0);

                        int mraoData = Main.mraoImage.getRGB(texX, texY);
                        Vector4 mraoVec = new Vector4((mraoData >> 16 & 0x000000ff) / 256.0, (mraoData >> 8 & 0x000000ff) / 256.0, (mraoData & 0x000000ff) / 256.0);

                        Vector4 pos = getCenteredVecForPoint(worldTriangle.vertices, alpha, beta, gamma);
                        Vector4 view = camera.target.subtract(camera.eye).normalize();
                        Vector4 lightPos = new Vector4(5.0, 5.0, 5.0);
                        Vector4 ray = pos.subtract(lightPos).normalize();
                        double diffuse = Math.max(normal.dot(ray) * diffuseCoeff, 0.0);

                        // считаем specular
                        double specular = 0.0;
                        Vector4 l = lightPos.subtract(pos);
                        double s = 10;
                        double angle = normal.dot(l);

                        Vector4 r = normal.mul(angle).mul(2.0).subtract(l);
                        Vector4 v = camera.eye.subtract(pos);
                        double rDotV = Math.max(r.dot(v), 0.0);
                        if (rDotV > 0) {
                            specular = Math.pow(rDotV / (r.len() * v.len()), s);
                        }

                        zBuffer[x * Main.height + y] = zFragment;

                        double colorValCoeff = ambientCoeff + diffuse * diffuseCoeff + specular * mraoVec.get(0) * specularCoeff;

                        int texColor = Main.textureImage.getRGB(texX, texY);
                        texColor = applyBrightness(texColor, colorValCoeff);

                        bufferedImage.setRGB(x, y, texColor);
                    }
                }
            }
        }
    }
}
