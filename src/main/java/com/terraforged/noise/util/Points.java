// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Points
{
    public static List<Vec2f> poisson(final int seedX, final int seedZ, final int width, final int height, final float radius, final int samples) {
        return poisson(new Random(NoiseUtil.seed(seedX, seedZ)), width, height, radius, samples);
    }
    
    public static List<Vec2f> poisson(final Random random, final int width, final int height, final float radius, final int samples) {
        final float cellSize = radius / NoiseUtil.SQRT2;
        final float maxDistance = radius * 2.0f;
        final int w = (int)Math.ceil(width / cellSize);
        final int h = (int)Math.ceil(height / cellSize);
        final int[][] grid = new int[w][h];
        final List<Vec2f> points = new ArrayList<Vec2f>();
        final List<Vec2f> spawns = new ArrayList<Vec2f>();
        spawns.add(new Vec2f((float)random.nextInt(width), (float)random.nextInt(height)));
        while (spawns.size() > 0) {
            final int index = random.nextInt(spawns.size());
            final Vec2f spawn = spawns.get(index);
            boolean valid = false;
            for (int i = 0; i < samples; ++i) {
                final float angle = random.nextFloat() * 6.2831855f;
                final float distance = radius + random.nextFloat() * maxDistance;
                final float x = spawn.x + NoiseUtil.sin(angle) * distance;
                final float z = spawn.y + NoiseUtil.cos(angle) * distance;
                if (valid(x, z, width, height, cellSize, radius, points, grid)) {
                    valid = true;
                    final Vec2f vec = new Vec2f(x, z);
                    points.add(vec);
                    spawns.add(vec);
                    final int cellX = (int)(x / cellSize);
                    final int cellZ = (int)(z / cellSize);
                    grid[cellZ][cellX] = points.size();
                    break;
                }
            }
            if (!valid) {
                spawns.remove(index);
            }
        }
        return points;
    }
    
    private static boolean valid(final float x, final float z, final int width, final int height, final float cellSize, final float radius, final List<Vec2f> points, final int[][] grid) {
        if (x < 0.0f || x >= width || z < 0.0f || z >= height) {
            return false;
        }
        final int cellX = (int)(x / cellSize);
        final int cellZ = (int)(z / cellSize);
        final int searchRadius = 2;
        final float radius2 = radius * radius;
        final int minX = Math.max(0, cellX - searchRadius);
        final int maxX = Math.min(grid[0].length - 1, cellX + searchRadius);
        final int minZ = Math.max(0, cellZ - searchRadius);
        for (int maxZ = Math.min(grid.length - 1, cellZ + searchRadius), dz = minZ; dz <= maxZ; ++dz) {
            for (int dx = minX; dx <= maxX; ++dx) {
                final int index = grid[dz][dx] - 1;
                if (index != -1) {
                    final Vec2f point = points.get(index);
                    if (point.dist2(x, z) < radius2) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}
