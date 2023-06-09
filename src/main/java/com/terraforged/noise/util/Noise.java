// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.util;

import com.terraforged.noise.Module;
import com.terraforged.noise.func.CellFunc;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.func.Interpolation;

public class Noise
{
    private static final float F2 = 0.36602542f;
    private static final float G2 = 0.21132487f;
    private static final float LEGACY_SIMPLEX = 79.869484f;
    private static final float BETTER_SIMPLEX = 99.83685f;
    
    public static float singlePerlin(final float x, final float y, final int seed, final Interpolation interp) {
        final int x2 = NoiseUtil.floor(x);
        final int y2 = NoiseUtil.floor(y);
        final int x3 = x2 + 1;
        final int y3 = y2 + 1;
        final float xs = interp.apply(x - x2);
        final float ys = interp.apply(y - y2);
        final float xd0 = x - x2;
        final float yd0 = y - y2;
        final float xd2 = xd0 - 1.0f;
        final float yd2 = yd0 - 1.0f;
        final float xf0 = NoiseUtil.lerp(NoiseUtil.gradCoord2D(seed, x2, y2, xd0, yd0), NoiseUtil.gradCoord2D(seed, x3, y2, xd2, yd0), xs);
        final float xf2 = NoiseUtil.lerp(NoiseUtil.gradCoord2D(seed, x2, y3, xd0, yd2), NoiseUtil.gradCoord2D(seed, x3, y3, xd2, yd2), xs);
        return NoiseUtil.lerp(xf0, xf2, ys);
    }
    
    public static float singlePerlin2(final float x, final float y, final int seed, final Interpolation interp) {
        final int x2 = NoiseUtil.floor(x);
        final int y2 = NoiseUtil.floor(y);
        final int x3 = x2 + 1;
        final int y3 = y2 + 1;
        final float xs = interp.apply(x - x2);
        final float ys = interp.apply(y - y2);
        final float xd0 = x - x2;
        final float yd0 = y - y2;
        final float xd2 = xd0 - 1.0f;
        final float yd2 = yd0 - 1.0f;
        final float xf0 = NoiseUtil.lerp(NoiseUtil.gradCoord2D_24(seed, x2, y2, xd0, yd0), NoiseUtil.gradCoord2D_24(seed, x3, y2, xd2, yd0), xs);
        final float xf2 = NoiseUtil.lerp(NoiseUtil.gradCoord2D_24(seed, x2, y3, xd0, yd2), NoiseUtil.gradCoord2D_24(seed, x3, y3, xd2, yd2), xs);
        return NoiseUtil.lerp(xf0, xf2, ys);
    }
    
    public static float singleLegacySimplex(final float x, final float y, final int seed) {
        return singleSimplex(x, y, seed, 79.869484f);
    }
    
    public static float singleSimplex(final float x, final float y, final int seed) {
        return singleSimplex(x, y, seed, 99.83685f);
    }
    
    public static float singleSimplex(final float x, final float y, final int seed, final float scaler) {
        float t = (x + y) * 0.36602542f;
        final int i = NoiseUtil.floor(x + t);
        final int j = NoiseUtil.floor(y + t);
        t = (i + j) * 0.21132487f;
        final float X0 = i - t;
        final float Y0 = j - t;
        final float x2 = x - X0;
        final float y2 = y - Y0;
        int i2;
        int j2;
        if (x2 > y2) {
            i2 = 1;
            j2 = 0;
        }
        else {
            i2 = 0;
            j2 = 1;
        }
        final float x3 = x2 - i2 + 0.21132487f;
        final float y3 = y2 - j2 + 0.21132487f;
        final float x4 = x2 - 1.0f + 0.42264974f;
        final float y4 = y2 - 1.0f + 0.42264974f;
        t = 0.5f - x2 * x2 - y2 * y2;
        float n0;
        if (t < 0.0f) {
            n0 = 0.0f;
        }
        else {
            t *= t;
            n0 = t * t * NoiseUtil.gradCoord2D_24(seed, i, j, x2, y2);
        }
        t = 0.5f - x3 * x3 - y3 * y3;
        float n2;
        if (t < 0.0f) {
            n2 = 0.0f;
        }
        else {
            t *= t;
            n2 = t * t * NoiseUtil.gradCoord2D_24(seed, i + i2, j + j2, x3, y3);
        }
        t = 0.5f - x4 * x4 - y4 * y4;
        float n3;
        if (t < 0.0f) {
            n3 = 0.0f;
        }
        else {
            t *= t;
            n3 = t * t * NoiseUtil.gradCoord2D_24(seed, i + 1, j + 1, x4, y4);
        }
        return scaler * (n0 + n2 + n3);
    }
    
    public static float singleCubic(final float x, final float y, final int seed) {
        final int x2 = NoiseUtil.floor(x);
        final int y2 = NoiseUtil.floor(y);
        final int x3 = x2 - 1;
        final int y3 = y2 - 1;
        final int x4 = x2 + 1;
        final int y4 = y2 + 1;
        final int x5 = x2 + 2;
        final int y5 = y2 + 2;
        final float xs = x - x2;
        final float ys = y - y2;
        return NoiseUtil.cubicLerp(NoiseUtil.cubicLerp(NoiseUtil.valCoord2D(seed, x3, y3), NoiseUtil.valCoord2D(seed, x2, y3), NoiseUtil.valCoord2D(seed, x4, y3), NoiseUtil.valCoord2D(seed, x5, y3), xs), NoiseUtil.cubicLerp(NoiseUtil.valCoord2D(seed, x3, y2), NoiseUtil.valCoord2D(seed, x2, y2), NoiseUtil.valCoord2D(seed, x4, y2), NoiseUtil.valCoord2D(seed, x5, y2), xs), NoiseUtil.cubicLerp(NoiseUtil.valCoord2D(seed, x3, y4), NoiseUtil.valCoord2D(seed, x2, y4), NoiseUtil.valCoord2D(seed, x4, y4), NoiseUtil.valCoord2D(seed, x5, y4), xs), NoiseUtil.cubicLerp(NoiseUtil.valCoord2D(seed, x3, y5), NoiseUtil.valCoord2D(seed, x2, y5), NoiseUtil.valCoord2D(seed, x4, y5), NoiseUtil.valCoord2D(seed, x5, y5), xs), ys) * 0.44444445f;
    }
    
    public static float cell(final float x, final float y, final int seed, final float distance, final CellFunc cellFunc, final DistanceFunc distanceFunc, final Module lookup) {
        final int xi = NoiseUtil.floor(x);
        final int yi = NoiseUtil.floor(y);
        int cellX = xi;
        int cellY = yi;
        Vec2f vec2f = null;
        float nearest = Float.MAX_VALUE;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xi + dx;
                final int cy = yi + dy;
                final Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                final float deltaX = cx + vec.x * distance - x;
                final float deltaY = cy + vec.y * distance - y;
                final float dist = distanceFunc.apply(deltaX, deltaY);
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
    
    public static float cellEdge(final float x, final float y, final int seed, final float distance, final EdgeFunc edgeFunc, final DistanceFunc distanceFunc) {
        final int xi = NoiseUtil.floor(x);
        final int yi = NoiseUtil.floor(y);
        float nearest1 = Float.MAX_VALUE;
        float nearest2 = Float.MAX_VALUE;
        for (int dy = -1; dy <= 1; ++dy) {
            for (int dx = -1; dx <= 1; ++dx) {
                final int cx = xi + dx;
                final int cy = yi + dy;
                final Vec2f vec = NoiseUtil.cell(seed, cx, cy);
                final float deltaX = cx + vec.x * distance - x;
                final float deltaY = cy + vec.y * distance - y;
                final float dist = distanceFunc.apply(deltaX, deltaY);
                if (dist < nearest1) {
                    nearest2 = nearest1;
                    nearest1 = dist;
                }
                else if (dist < nearest2) {
                    nearest2 = dist;
                }
            }
        }
        return edgeFunc.apply(nearest1, nearest2);
    }
    
    public static float white(final float x, final float y, final int seed) {
        final int xi = NoiseUtil.round(x);
        final int yi = NoiseUtil.round(y);
        return NoiseUtil.valCoord2D(seed, xi, yi);
    }
}
