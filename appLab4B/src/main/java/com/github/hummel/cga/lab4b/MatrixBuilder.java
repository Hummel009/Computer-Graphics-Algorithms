package com.github.hummel.cga.lab4b;

public class MatrixBuilder {
    private MatrixBuilder() {
    }

    public static Matrix4 buildViewport(int width, int height) {
        Matrix4 viewport = new Matrix4();
        viewport.set(0, 0, width / 2.0);
        viewport.set(1, 1, -height / 2.0);
        viewport.set(0, 3, width / 2.0);
        viewport.set(1, 3, height / 2.0);
        return viewport;
    }

    public static Matrix4 buildProjection(double aspect, double FOV) {
        double zNear = 0.01;
        double zFar = 1.0;

        Matrix4 projectionMatrix = new Matrix4();
        projectionMatrix.set(0, 0, 1.0 / (aspect * Math.tan(FOV / 2.0 * 0.0174533)));
        projectionMatrix.set(1, 1, 1.0 / Math.tan(FOV / 2.0 * 0.0174533));
        projectionMatrix.set(2, 2, zFar / (zNear - zFar));
        projectionMatrix.set(2, 3, zFar * zNear / (zNear - zFar));
        projectionMatrix.set(3, 2, -1.0);
        projectionMatrix.set(3, 3, 0.0);
        return projectionMatrix;
    }

    public static Matrix4 buildView(Camera camera) {
        return buildView(camera.eye, camera.target, camera.up);
    }

    private static Matrix4 buildView(Vector4 eye, Vector4 target, Vector4 up) {
        Matrix4 viewMatrix = new Matrix4();
        Vector4 ZAxis = eye.subtract(target).normalize();
        Vector4 XAxis = up.cross(ZAxis).normalize();
        Vector4 YAxis = ZAxis.cross(XAxis);

        viewMatrix.set(0, 0, XAxis.get(0));
        viewMatrix.set(0, 1, XAxis.get(1));
        viewMatrix.set(0, 2, XAxis.get(2));
        viewMatrix.set(0, 3, -XAxis.dot(eye));

        viewMatrix.set(1, 0, YAxis.get(0));
        viewMatrix.set(1, 1, YAxis.get(1));
        viewMatrix.set(1, 2, YAxis.get(2));
        viewMatrix.set(1, 3, -YAxis.dot(eye));

        viewMatrix.set(2, 0, ZAxis.get(0));
        viewMatrix.set(2, 1, ZAxis.get(1));
        viewMatrix.set(2, 2, ZAxis.get(2));
        viewMatrix.set(2, 3, -ZAxis.dot(eye));

        return viewMatrix;
    }

    public static Matrix4 buildRotationY(double theta) {
        Matrix4 result = new Matrix4();
        result.set(0, 0, Math.cos(theta));
        result.set(0, 2, Math.sin(theta));
        result.set(2, 0, -Math.sin(theta));
        result.set(2, 2, Math.cos(theta));
        return result;
    }

    public static Matrix4 buildRotationX(double theta) {
        Matrix4 result = new Matrix4();
        result.set(1, 1, Math.cos(theta));
        result.set(2, 1, Math.sin(theta));
        result.set(1, 2, -Math.sin(theta));
        result.set(2, 2, Math.cos(theta));
        return result;
    }
}
