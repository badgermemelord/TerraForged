//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.util;

import com.terraforged.noise.Module;
import com.terraforged.noise.func.CellFunc;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.func.Interpolation;

public class Noise {
    private static final float F2 = 0.36602542F;
    private static final float G2 = 0.21132487F;
    private static final float LEGACY_SIMPLEX = 79.869484F;
    private static final float BETTER_SIMPLEX = 99.83685F;

    public Noise() {
    }

    public static float singlePerlin(float x, float y, int seed, Interpolation interp) {
        int x0 = NoiseUtil.floor(x);
        int y0 = NoiseUtil.floor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        float xs = interp.apply(x - (float)x0);
        float ys = interp.apply(y - (float)y0);
        float xd0 = x - (float)x0;
        float yd0 = y - (float)y0;
        float xd1 = xd0 - 1.0F;
        float yd1 = yd0 - 1.0F;
        float xf0 = NoiseUtil.lerp(NoiseUtil.gradCoord2D(seed, x0, y0, xd0, yd0), NoiseUtil.gradCoord2D(seed, x1, y0, xd1, yd0), xs);
        float xf1 = NoiseUtil.lerp(NoiseUtil.gradCoord2D(seed, x0, y1, xd0, yd1), NoiseUtil.gradCoord2D(seed, x1, y1, xd1, yd1), xs);
        return NoiseUtil.lerp(xf0, xf1, ys);
    }

    public static float singlePerlin2(float x, float y, int seed, Interpolation interp) {
        int x0 = NoiseUtil.floor(x);
        int y0 = NoiseUtil.floor(y);
        int x1 = x0 + 1;
        int y1 = y0 + 1;
        float xs = interp.apply(x - (float)x0);
        float ys = interp.apply(y - (float)y0);
        float xd0 = x - (float)x0;
        float yd0 = y - (float)y0;
        float xd1 = xd0 - 1.0F;
        float yd1 = yd0 - 1.0F;
        float xf0 = NoiseUtil.lerp(NoiseUtil.gradCoord2D_24(seed, x0, y0, xd0, yd0), NoiseUtil.gradCoord2D_24(seed, x1, y0, xd1, yd0), xs);
        float xf1 = NoiseUtil.lerp(NoiseUtil.gradCoord2D_24(seed, x0, y1, xd0, yd1), NoiseUtil.gradCoord2D_24(seed, x1, y1, xd1, yd1), xs);
        return NoiseUtil.lerp(xf0, xf1, ys);
    }

    public static float singleLegacySimplex(float x, float y, int seed) {
        return singleSimplex(x, y, seed, 79.869484F);
    }

    public static float singleSimplex(float x, float y, int seed) {
        return singleSimplex(x, y, seed, 99.83685F);
    }

    public static float singleSimplex(float x, float y, int seed, float scaler) {
        float t = (x + y) * 0.36602542F;
        int i = NoiseUtil.floor(x + t);
        int j = NoiseUtil.floor(y + t);
        t = (float)(i + j) * 0.21132487F;
        float X0 = (float)i - t;
        float Y0 = (float)j - t;
        float x0 = x - X0;
        float y0 = y - Y0;
        int i1;
        int j1;
        if (x0 > y0) {
            i1 = 1;
            j1 = 0;
        } else {
            i1 = 0;
            j1 = 1;
        }

        float x1 = x0 - (float)i1 + 0.21132487F;
        float y1 = y0 - (float)j1 + 0.21132487F;
        float x2 = x0 - 1.0F + 0.42264974F;
        float y2 = y0 - 1.0F + 0.42264974F;
        t = 0.5F - x0 * x0 - y0 * y0;
        float n0;
        if (t < 0.0F) {
            n0 = 0.0F;
        } else {
            t *= t;
            n0 = t * t * NoiseUtil.gradCoord2D_24(seed, i, j, x0, y0);
        }

        t = 0.5F - x1 * x1 - y1 * y1;
        float n1;
        if (t < 0.0F) {
            n1 = 0.0F;
        } else {
            t *= t;
            n1 = t * t * NoiseUtil.gradCoord2D_24(seed, i + i1, j + j1, x1, y1);
        }

        t = 0.5F - x2 * x2 - y2 * y2;
        float n2;
        if (t < 0.0F) {
            n2 = 0.0F;
        } else {
            t *= t;
            n2 = t * t * NoiseUtil.gradCoord2D_24(seed, i + 1, j + 1, x2, y2);
        }

        return scaler * (n0 + n1 + n2);
    }

    public static float singleCubic(float x, float y, int seed) {
        int x1 = NoiseUtil.floor(x);
        int y1 = NoiseUtil.floor(y);
        int x0 = x1 - 1;
        int y0 = y1 - 1;
        int x2 = x1 + 1;
        int y2 = y1 + 1;
        int x3 = x1 + 2;
        int y3 = y1 + 2;
        float xs = x - (float)x1;
        float ys = y - (float)y1;
        return NoiseUtil.cubicLerp(
                NoiseUtil.cubicLerp(
                        NoiseUtil.valCoord2D(seed, x0, y0),
                        NoiseUtil.valCoord2D(seed, x1, y0),
                        NoiseUtil.valCoord2D(seed, x2, y0),
                        NoiseUtil.valCoord2D(seed, x3, y0),
                        xs
                ),
                NoiseUtil.cubicLerp(
                        NoiseUtil.valCoord2D(seed, x0, y1),
                        NoiseUtil.valCoord2D(seed, x1, y1),
                        NoiseUtil.valCoord2D(seed, x2, y1),
                        NoiseUtil.valCoord2D(seed, x3, y1),
                        xs
                ),
                NoiseUtil.cubicLerp(
                        NoiseUtil.valCoord2D(seed, x0, y2),
                        NoiseUtil.valCoord2D(seed, x1, y2),
                        NoiseUtil.valCoord2D(seed, x2, y2),
                        NoiseUtil.valCoord2D(seed, x3, y2),
                        xs
                ),
                NoiseUtil.cubicLerp(
                        NoiseUtil.valCoord2D(seed, x0, y3),
                        NoiseUtil.valCoord2D(seed, x1, y3),
                        NoiseUtil.valCoord2D(seed, x2, y3),
                        NoiseUtil.valCoord2D(seed, x3, y3),
                        xs
                ),
                ys
        )
                * 0.44444445F;
    }

    public static float cell(float x, float y, int seed, float distance, CellFunc cellFunc, DistanceFunc distanceFunc, Module lookup) {
        int xi = NoiseUtil.floor(x);
        int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        Vec2f vec2f = null;
        float nearest = Float.MAX_VALUE;

        for(int dy = -1; dy <= 1; ++dy) {
            for(int dx = -1; dx <= 1; ++dx) {
                int cx = xi + dx;
                int cy = yi + dy;
                Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                float deltaX = (float)cx + vec.x * distance - x;
                float deltaY = (float)cy + vec.y * distance - y;
                float dist = distanceFunc.apply(deltaX, deltaY);
                if (dist < nearest) {
                    nearest = dist;
                    vec2f = vec;
                    cellX = cx;
                    cellY = cy;
                }
            }
        }

        return cellFunc.apply(cellX, cellY, nearest, seed, vec2f, lookup);
    }

    public static float cellEdge(float x, float y, int seed, float distance, EdgeFunc edgeFunc, DistanceFunc distanceFunc) {
        int xi = NoiseUtil.floor(x);
        int yi = NoiseUtil.floor(y);
        float nearest1 = Float.MAX_VALUE;
        float nearest2 = Float.MAX_VALUE;

        for(int dy = -1; dy <= 1; ++dy) {
            for(int dx = -1; dx <= 1; ++dx) {
                int cx = xi + dx;
                int cy = yi + dy;
                Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                float deltaX = (float)cx + vec.x * distance - x;
                float deltaY = (float)cy + vec.y * distance - y;
                float dist = distanceFunc.apply(deltaX, deltaY);
                if (dist < nearest1) {
                    nearest2 = nearest1;
                    nearest1 = dist;
                } else if (dist < nearest2) {
                    nearest2 = dist;
                }
            }
        }

        return edgeFunc.apply(nearest1, nearest2);
    }

    public static float white(float x, float y, int seed) {
        int xi = NoiseUtil.round(x);
        int yi = NoiseUtil.round(y);
        return NoiseUtil.valCoord2D(seed, xi, yi);
    }
}
