// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.fastpoisson;

import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;

import java.util.Random;
import java.util.function.Supplier;

public class FastPoisson
{
    public static final ThreadLocal<FastPoisson> LOCAL_POISSON;
    private final LongList chunk;
    private final LongIterSet region;
    
    public FastPoisson() {
        this.chunk = (LongList)new LongArrayList();
        this.region = new LongIterSet();
    }
    
    public <Ctx> void visit(final int seed, final int chunkX, final int chunkZ, final Random random, final FastPoissonContext context, final Ctx ctx, final Visitor<Ctx> visitor) {
        this.chunk.clear();
        this.region.clear();
        visit(seed, chunkX, chunkZ, random, context, this.region, this.chunk, ctx, visitor);
    }
    
    public static <Ctx> void visit(final int seed, final int chunkX, final int chunkZ, final Random random, final FastPoissonContext context, final LongIterSet region, final LongList chunk, final Ctx ctx, final Visitor<Ctx> visitor) {
        final int startX = chunkX << 4;
        final int startZ = chunkZ << 4;
        collectPoints(seed, startX, startZ, context, region, chunk);
        region.shuffle(random);
        LongLists.shuffle(chunk, random);
        visitPoints(startX, startZ, region, chunk, context, ctx, visitor);
    }
    
    private static void collectPoints(final int seed, final int startX, final int startZ, final FastPoissonContext context, final LongIterSet region, final LongList chunk) {
        final int halfRadius = context.radius / 2;
        final int quarterRadius = context.radius / 4;
        final int min = -halfRadius;
        final int max = 15 + halfRadius;
        final int cullX = startX - quarterRadius;
        final int cullZ = startZ - quarterRadius;
        for (int dz = min; dz <= max; ++dz) {
            for (int dx = min; dx <= max; ++dx) {
                final int x = startX + dx;
                final int z = startZ + dz;
                final long point = getPoint(seed, (float)x, (float)z, context);
                final int px = PosUtil.unpackLeft(point);
                final int pz = PosUtil.unpackRight(point);
                if (px >= cullX) {
                    if (pz >= cullZ) {
                        if (region.add(point) && inChunkBoundsLow(px, pz, startX, startZ, -1)) {
                            chunk.add(point);
                        }
                    }
                }
            }
        }
    }
    
    private static <Ctx> void visitPoints(final int startX, final int startZ, final LongIterSet region, final LongList chunk, final FastPoissonContext context, final Ctx ctx, final Visitor<Ctx> visitor) {
        final int radius2 = context.radius2;
        final int halfRadius = context.radius / 2;
        for (int i = 0; i < chunk.size(); ++i) {
            final long point = chunk.getLong(i);
            final int px = PosUtil.unpackLeft(point);
            final int pz = PosUtil.unpackRight(point);
            if (region.contains(point)) {
                final float noise = context.density.getValue((float)px, (float)pz);
                final float radius2f = radius2 * noise;
                if (checkNeighbours(startX, startZ, point, px, pz, halfRadius, radius2f, region)) {
                    visitor.visit(px, pz, ctx);
                }
            }
        }
    }
    
    private static boolean checkNeighbours(final int startX, final int startZ, final long point, final int x, final int z, final int halfRadius, final float radius2, final LongIterSet region) {
        region.reset();
        final int boundHigh = 16 + halfRadius;
        while (region.hasNext()) {
            final long neighbour = region.nextLong();
            if (neighbour == Long.MAX_VALUE) {
                return false;
            }
            if (point == neighbour) {
                continue;
            }
            final int px = PosUtil.unpackLeft(neighbour);
            final int pz = PosUtil.unpackRight(neighbour);
            if (dist2(x, z, px, pz) > radius2) {
                continue;
            }
            if (!inChunkBoundsHigh(px, pz, startX, startZ, boundHigh)) {
                return false;
            }
            region.remove();
        }
        return true;
    }
    
    private static long getPoint(final int seed, float x, float z, final FastPoissonContext context) {
        x *= context.frequency;
        z *= context.frequency;
        final int cellX = NoiseUtil.floor(x);
        final int cellZ = NoiseUtil.floor(z);
        final Vec2f vec = NoiseUtil.cell(seed, cellX, cellZ);
        final int px = NoiseUtil.floor((cellX + context.pad + vec.x * context.jitter) * context.scale);
        final int pz = NoiseUtil.floor((cellZ + context.pad + vec.y * context.jitter) * context.scale);
        return PosUtil.pack(px, pz);
    }
    
    private static boolean inChunkBoundsLow(final int px, final int pz, final int startX, final int startZ, final int min) {
        final int dx = px - startX;
        final int dz = pz - startZ;
        return dx > min && dx < 16 && dz > min && dz < 16;
    }
    
    private static boolean inChunkBoundsHigh(final int px, final int pz, final int startX, final int startZ, final int max) {
        final int dx = px - startX;
        final int dz = pz - startZ;
        return dx > -1 && dx < max && dz > -1 && dz < max;
    }
    
    private static int dist2(final int ax, final int az, final int bx, final int bz) {
        final int dx = ax - bx;
        final int dz = az - bz;
        return dx * dx + dz * dz;
    }
    
    static {
        LOCAL_POISSON = ThreadLocal.withInitial((Supplier<? extends FastPoisson>)FastPoisson::new);
    }
    
    public interface Visitor<Ctx>
    {
        void visit(final int p0, final int p1, final Ctx p2);
    }
}
