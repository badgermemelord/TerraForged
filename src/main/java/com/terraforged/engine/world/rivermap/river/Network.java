// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap.river;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.util.Boundsf;
import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.engine.world.rivermap.lake.Lake;
import com.terraforged.engine.world.rivermap.wetland.Wetland;
import com.terraforged.noise.source.Line;

import java.util.ArrayList;
import java.util.List;

public class Network
{
    public static final Lake[] LAKE;
    public static final Network[] NETWORKS;
    public static final Wetland[] WETLAND;
    private final Boundsf bounds;
    private final Lake[] lakes;
    private final Wetland[] wetlands;
    private final Network[] children;
    private final RiverCarver riverCarver;
    
    public Network(final RiverCarver riverCarver, final Lake[] lakes, final Wetland[] wetlands, final Network[] children, final Boundsf bounds) {
        this.riverCarver = riverCarver;
        this.lakes = lakes;
        this.wetlands = wetlands;
        this.children = children;
        this.bounds = bounds;
    }
    
    public boolean contains(final float x, final float z) {
        return this.bounds.contains(x, z);
    }
    
    public void carve(final Cell cell, float x, float z, final float nx, final float nz) {
        final River river = this.riverCarver.river;
        final RiverWarp warp = this.riverCarver.warp;
        float t = Line.distanceOnLine(x, z, river.x1, river.z1, river.x2, river.z2);
        final float px = x;
        final float pz = z;
        final float pt = t;
        if (warp.test(t)) {
            final long offset = warp.getOffset(x, z, pt, river);
            x += PosUtil.unpackLeftf(offset);
            z += PosUtil.unpackRightf(offset);
            t = Line.distanceOnLine(x, z, river.x1, river.z1, river.x2, river.z2);
        }
        this.carveRiver(cell, px, pz, pt, x, z, t);
        this.carveWetlands(cell, x, z, nx, nz);
        this.carveLakes(cell, x, z, nx, nz);
        for (final Network network : this.children) {
            network.carve(cell, x, z, nx, nz);
        }
    }
    
    public boolean overlaps(final River river, final float extend) {
        return overlaps(river, this.riverCarver, extend) || overlaps(river, this.children, extend);
    }
    
    private void carveRiver(final Cell cell, final float px, final float pz, final float pt, final float x, final float z, final float t) {
        this.riverCarver.carve(cell, px, pz, pt, x, z, t);
    }
    
    private void carveWetlands(final Cell cell, final float x, final float z, final float nx, final float nz) {
        for (final Wetland wetland : this.wetlands) {
            wetland.apply(cell, x + nx, z + nz, x, z);
        }
    }
    
    private void carveLakes(final Cell cell, final float x, final float z, final float nx, final float nz) {
        final float lx = x + nx;
        final float lz = z + nz;
        for (final Lake lake : this.lakes) {
            lake.apply(cell, lx, lz);
        }
    }
    
    private static boolean overlaps(final River river, final RiverCarver riverCarver, final float extend) {
        return riverCarver.river.intersects(river, extend);
    }
    
    private static boolean overlaps(final River river, final Network[] networks, final float extend) {
        for (final Network network : networks) {
            if (network.overlaps(river, extend)) {
                return true;
            }
        }
        return false;
    }
    
    public static Builder builder(final RiverCarver carver) {
        return new Builder(carver);
    }
    
    static {
        LAKE = new Lake[0];
        NETWORKS = new Network[0];
        WETLAND = new Wetland[0];
    }
    
    public static class Builder
    {
        public final RiverCarver carver;
        public final List<Lake> lakes;
        public final List<Wetland> wetlands;
        public final List<Builder> children;
        private float minX;
        private float minZ;
        private float maxX;
        private float maxZ;
        
        private Builder(final RiverCarver carver) {
            this.lakes = new ArrayList<Lake>();
            this.wetlands = new ArrayList<Wetland>();
            this.children = new ArrayList<Builder>();
            this.carver = carver;
            this.addBounds(carver.river);
        }
        
        public void addBounds(final River river) {
            this.minX = min(this.minX, river.x1, river.x2);
            this.minZ = min(this.minZ, river.z1, river.z2);
            this.maxX = max(this.maxX, river.x1, river.x2);
            this.maxZ = max(this.maxZ, river.z1, river.z2);
        }
        
        public boolean overlaps(final River river, final Builder parent, final float extend) {
            if (parent == this) {
                final float x1 = river.x1 - river.ndx * extend;
                final float z1 = river.z1 - river.ndz * extend;
                final float x2 = river.x1 + river.dx * 0.5f;
                final float z2 = river.z1 + river.dz * 0.5f;
                final River other = this.carver.river;
                if (Line.intersect(x1, z1, x2, z2, other.x1, other.z1, other.x2, other.z2)) {
                    return true;
                }
            }
            else if (overlaps(river, this.carver, extend)) {
                return true;
            }
            if (parent != null && parent != this && overlaps(river, this.carver, extend)) {
                return true;
            }
            for (final Builder branch : this.children) {
                if (branch.overlaps(river, parent, extend)) {
                    return true;
                }
            }
            return false;
        }
        
        public Network build() {
            return this.build(this.recordBounds(Boundsf.builder()).build());
        }
        
        private Network build(final Boundsf bounds) {
            return new Network(this.carver, this.lakes.toArray(Network.LAKE), this.wetlands.toArray(Network.WETLAND), this.children.stream().map(child -> child.build(Boundsf.NONE)).toArray(Network[]::new), bounds);
        }
        
        private Boundsf.Builder recordBounds(final Boundsf.Builder builder) {
            builder.record(this.carver.river.minX, this.carver.river.minZ);
            builder.record(this.carver.river.maxX, this.carver.river.maxZ);
            for (final Builder child : this.children) {
                child.recordBounds(builder);
            }
            for (final Lake lake : this.lakes) {
                lake.recordBounds(builder);
            }
            for (final Wetland wetland : this.wetlands) {
                wetland.recordBounds(builder);
            }
            return builder;
        }
        
        private static float min(float min, final float... values) {
            for (final float v : values) {
                min = Math.min(min, v);
            }
            return min;
        }
        
        private static float max(float max, final float... values) {
            for (final float v : values) {
                max = Math.max(max, v);
            }
            return max;
        }
    }
}
