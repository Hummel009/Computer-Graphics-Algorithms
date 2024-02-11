package com.github.hummel.cga.lab1

import kotlin.math.cos
import kotlin.math.sin

private const int MAX_SIZE_MATRIX = 4;

private static float[,] MultiplyMatrices(float[,] matrix1, float[,] matrix2)
{
	var result = new float[MAX_SIZE_MATRIX, MAX_SIZE_MATRIX];

	for (var i = 0; i < MAX_SIZE_MATRIX; i++)
	{
		for (var j = 0; j < MAX_SIZE_MATRIX; j++)
		{
			result[i, j] = matrix1[i, 0] * matrix2[0, j] + matrix1[i, 1] * matrix2[1, j] + matrix1[i, 2] * matrix2[2, j] + matrix1[i, 3] * matrix2[3, j];
		}
	}

	return result;
}

private static GeometricVertex MultiplyVectorAndMatrixAsMatrices(GeometricVertex vector, float[,] matrix1)
{
	var result = new GeometricVertex();

	var matrix2 = new float[,]
	{
		{    vector.X,    vector.Y,    vector.Z,           0},
		{    vector.X,    vector.Y,    vector.Z,           0},
		{    vector.X,    vector.Y,    vector.Z,           0},
		{           0,           0,           0,           1},
	};

	var matrixResult = MultiplyMatrices(matrix2, matrix1);

	result.X = matrixResult[0, 0];
	result.Y = matrixResult[1, 1];
	result.Z = matrixResult[2, 2];
	result.W = vector.W;

	return result;
}

public static void TranslateVectors(GeometricVertex[] vectors, CoordinateVector translation)
{
	Parallel.For(0, vectors.Length, i =>
	{
		vectors[i].TranslateX += translation.X;
		vectors[i].TranslateY += translation.Y;
		vectors[i].TranslateZ += translation.Z;
	});
}

public static void ScaleVectors(GeometricVertex[] vectors, CoordinateVector scale)
{
	Parallel.For(0, vectors.Length, i =>
	{
		vectors[i].X *= scale.X;
		vectors[i].Y *= scale.Y;
		vectors[i].Z *= scale.Z;
	});
}

public static void RotateVectorsAroundX(GeometricVertex[] vectors, double angle)
{
	var cos = (float)Math.Cos(angle);
	var sin = (float)Math.Sin(angle);

	Parallel.For(0, vectors.Length, i =>
	{
		var y = vectors[i].Y;

		vectors[i].Y = y * cos + vectors[i].Z * sin;
		vectors[i].Z = y * -sin + vectors[i].Z * cos;
	});
}

public static void RotateVectorsAroundY(GeometricVertex[] vectors, double angle)
{
	var cos = (float)Math.Cos(angle);
	var sin = (float)Math.Sin(angle);

	Parallel.For(0, vectors.Length, i =>
	{
		var x = vectors[i].X;

		vectors[i].X = x * cos + vectors[i].Z * -sin;
		vectors[i].Z = x * sin + vectors[i].Z * cos;
	});
}

public static void RotateVectorsAroundZ(GeometricVertex[] vectors, double angle)
{
	var cos = (float)Math.Cos(angle);
	var sin = (float)Math.Sin(angle);

	Parallel.For(0, vectors.Length, i =>
	{
		var x = vectors[i].X;

		vectors[i].X = x * cos + vectors[i].Y * sin;
		vectors[i].Y = x * -sin + vectors[i].Y * cos;
	});
}

fun rotateModelZ() {
	for (vertex in vertices) {
		val x = vertex.x
		val y = vertex.y
		vertex.x = x * cos(rotationSpeedZ) - y * sin(rotationSpeedZ)
		vertex.y = x * sin(rotationSpeedZ) + y * cos(rotationSpeedZ)
	}
	rotationAngleZ += rotationSpeedZ
}