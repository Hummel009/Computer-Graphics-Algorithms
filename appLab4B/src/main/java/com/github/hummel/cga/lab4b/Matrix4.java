package com.github.hummel.cga.lab4b;

public class Matrix4 {
    private final double[][] data;

    public Matrix4() {
        data = new double[][]{
                {1.0, 0.0, 0.0, 0.0},
                {0.0, 1.0, 0.0, 0.0},
                {0.0, 0.0, 1.0, 0.0},
                {0.0, 0.0, 0.0, 1.0}
        };
    }

    public void set(int i, int j, double val) {
        data[i][j] = val;
    }

    public Matrix4 mul(Matrix4 arg) {
        var res = new Matrix4();
        for (var i = 0; i < 4; i++) {
            for (var j = 0; j < 4; j++) {
                res.data[i][j] = 0.0;
                for (var k = 0; k < 4; k++) {
                    res.data[i][j] += data[i][k] * arg.data[k][j];
                }
            }
        }
        return res;
    }

    public Vector4 mul(Vector4 arg) {
        var res = new Vector4();
        res.set(3, 0.0);
        for (var i = 0; i < 4; i++) {
            for (var j = 0; j < 4; j++) {
                res.set(i, res.get(i) + data[i][j] * arg.get(j));
            }
        }
        return res;
    }
}
