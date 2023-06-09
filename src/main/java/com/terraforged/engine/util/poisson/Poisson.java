// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.poisson;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.pool.ObjectPool;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

import java.util.Arrays;

public class Poisson
{
    private static final int SAMPLES = 50;
    private final int radius;
    private final int radius2;
    private final float halfRadius;
    private final int maxDistance;
    private final int regionSize;
    private final int gridSize;
    private final float cellSize;
    private final ObjectPool<Vec2f[][]> pool;
    
    public Poisson(final int radius) {
        final int size = 48;
        this.radius = radius;
        this.radius2 = radius * radius;
        this.halfRadius = radius / 2.0f;
        this.maxDistance = radius * 2;
        this.regionSize = size - radius;
        this.cellSize = radius / NoiseUtil.SQRT2;
        this.gridSize = (int)Math.ceil(this.regionSize / this.cellSize);
        this.pool = new ObjectPool<Vec2f[][]>(3, () -> new Vec2f[this.gridSize][this.gridSize]);
    }
    
    public int getRadius() {
        return this.radius;
    }
    
    public Visitor visit(final int chunkX, final int chunkZ, final PoissonContext context, final Visitor visitor) {
        try (final Resource<Vec2f[][]> grid = this.pool.get()) {
            clear(grid.get());
            context.startX = chunkX << 4;
            context.startZ = chunkZ << 4;
            context.endX = context.startX + 16;
            context.endZ = context.startZ + 16;
            final int regionX = context.startX >> 5;
            final int regionZ = context.startZ >> 5;
            context.offsetX = regionX << 5;
            context.offsetZ = regionZ << 5;
            context.random.setSeed(NoiseUtil.hash2D(context.seed, regionX, regionZ));
            final int x = context.random.nextInt(this.regionSize);
            final int z = context.random.nextInt(this.regionSize);
            this.visit((float)x, (float)z, grid.get(), 50, context, visitor);
            return visitor;
        }
    }
    
    private void visit(final float px, final float pz, final Vec2f[][] grid, final int samples, final PoissonContext context, final Visitor visitor) {
        for (int i = 0; i < samples; ++i) {
            final float angle = context.random.nextFloat() * 6.2831855f;
            final float distance = this.radius + context.random.nextFloat() * this.maxDistance;
            final float x = this.halfRadius + px + NoiseUtil.sin(angle) * distance;
            final float z = this.halfRadius + pz + NoiseUtil.cos(angle) * distance;
            if (this.valid(x, z, grid, context)) {
                final Vec2f vec = new Vec2f(x, z);
                this.visit(vec, context, visitor);
                final int cellX = (int)(x / this.cellSize);
                final int cellZ = (int)(z / this.cellSize);
                grid[cellZ][cellX] = vec;
                this.visit(vec.x, vec.y, grid, samples, context, visitor);
            }
        }
    }
    
    private void visit(final Vec2f pos, final PoissonContext context, final Visitor visitor) {
        final int x = context.offsetX + (int)pos.x;
        final int z = context.offsetZ + (int)pos.y;
        if (x >= context.startX && x < context.endX && z >= context.startZ && z < context.endZ) {
            visitor.visit(x, z);
        }
    }
    
    private boolean valid(final float x, final float z, final Vec2f[][] grid, final PoissonContext context) {
        if (x < 0.0f || x >= this.regionSize || z < 0.0f || z >= this.regionSize) {
            return false;
        }
        final int cellX = (int)(x / this.cellSize);
        final int cellZ = (int)(z / this.cellSize);
        if (grid[cellZ][cellX] != null) {
            return false;
        }
        final float noise = context.density.getValue(context.offsetX + x, context.offsetZ + z);
        final float radius2 = noise * this.radius2;
        final int searchRadius = 2;
        final int minX = Math.max(0, cellX - searchRadius);
        final int maxX = Math.min(grid[0].length - 1, cellX + searchRadius);
        final int minZ = Math.max(0, cellZ - searchRadius);
        for (int maxZ = Math.min(grid.length - 1, cellZ + searchRadius), dz = minZ; dz <= maxZ; ++dz) {
            for (int dx = minX; dx <= maxX; ++dx) {
                final Vec2f vec = grid[dz][dx];
                if (vec != null) {
                    final float dist2 = vec.dist2(x, z);
                    if (dist2 < radius2) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    private static void clear(final Vec2f[][] grid) {
        for (final Vec2f[] row : grid) {
            Arrays.fill(row, null);
        }
    }
    
    public interface Visitor
    {
        void visit(final int p0, final int p1);
    }
}
