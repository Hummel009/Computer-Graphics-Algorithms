package com.github.hummel.cga.lab4b;

public class Vector4 {
    private final double[] data;

    public Vector4() {
        data = new double[]{0.0, 0.0, 0.0, 1.0};
    }

    public Vector4(double x, double y, double z) {
        data = new double[]{x, y, z, 1.0};
    }

    public double get(int i) {
        return data[i];
    }

    public void set(int i, double val) {
        data[i] = val;
    }

    public void divSelf(double val) {
        for (var i = 0; i < 3; i++) {
            data[i] /= val;
        }
    }

    public double len() {
        return Math.sqrt(data[0] * data[0] + data[1] * data[1] + data[2] * data[2]);
    }

    public Vector4 normalize() {
        var len = len();
        return new Vector4(data[0] / len, data[1] / len, data[2] / len);
    }

    public Vector4 add(Vector4 arg) {
        return new Vector4(data[0] + arg.data[0], data[1] + arg.data[1], data[2] + arg.data[2]);
    }

    public Vector4 subtract(Vector4 arg) {
        return new Vector4(data[0] - arg.data[0], data[1] - arg.data[1], data[2] - arg.data[2]);
    }

    public double dot(Vector4 arg) {
        return data[0] * arg.data[0] + data[1] * arg.data[1] + data[2] * arg.data[2];
    }

    public Vector4 mul(double arg) {
        return new Vector4(data[0] * arg, data[1] * arg, data[2] * arg);
    }

    public Vector4 div(double arg) {
        return new Vector4(data[0] / arg, data[1] / arg, data[2] / arg);
    }

    public Vector4 cross(Vector4 arg) {
        return new Vector4(data[1] * arg.data[2] - data[2] * arg.data[1], data[2] * arg.data[0] - data[0] * arg.data[2], data[0] * arg.data[1] - data[1] * arg.data[0]);
    }
}
