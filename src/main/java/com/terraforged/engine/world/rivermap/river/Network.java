//
// Source code recreated from a .class file by Quiltflower
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

public class Network {
    public static final Lake[] LAKE = new Lake[0];
    public static final Network[] NETWORKS = new Network[0];
    public static final Wetland[] WETLAND = new Wetland[0];
    private final Boundsf bounds;
    private final Lake[] lakes;
    private final Wetland[] wetlands;
    private final Network[] children;
    private final RiverCarver riverCarver;

    public Network(RiverCarver riverCarver, Lake[] lakes, Wetland[] wetlands, Network[] children, Boundsf bounds) {
        this.riverCarver = riverCarver;
        this.lakes = lakes;
        this.wetlands = wetlands;
        this.children = children;
        this.bounds = bounds;
    }

    public boolean contains(float x, float z) {
        return this.bounds.contains(x, z);
    }

    public void carve(Cell cell, float x, float z, float nx, float nz) {
        River river = this.riverCarver.river;
        RiverWarp warp = this.riverCarver.warp;
        float t = Line.distanceOnLine(x, z, river.x1, river.z1, river.x2, river.z2);
        float px = x;
        float pz = z;
        float pt = t;
        if (warp.test(t)) {
            long offset = warp.getOffset(x, z, t, river);
            x += PosUtil.unpackLeftf(offset);
            z += PosUtil.unpackRightf(offset);
            t = Line.distanceOnLine(x, z, river.x1, river.z1, river.x2, river.z2);
        }

        this.carveRiver(cell, px, pz, pt, x, z, t);
        this.carveWetlands(cell, x, z, nx, nz);
        this.carveLakes(cell, x, z, nx, nz);

        for(Network network : this.children) {
            network.carve(cell, x, z, nx, nz);
        }
    }

    public boolean overlaps(River river, float extend) {
        return overlaps(river, this.riverCarver, extend) || overlaps(river, this.children, extend);
    }

    private void carveRiver(Cell cell, float px, float pz, float pt, float x, float z, float t) {
        this.riverCarver.carve(cell, px, pz, pt, x, z, t);
    }

    private void carveWetlands(Cell cell, float x, float z, float nx, float nz) {
        for(Wetland wetland : this.wetlands) {
            wetland.apply(cell, x + nx, z + nz, x, z);
        }
    }

    private void carveLakes(Cell cell, float x, float z, float nx, float nz) {
        float lx = x + nx;
        float lz = z + nz;

        for(Lake lake : this.lakes) {
            lake.apply(cell, lx, lz);
        }
    }

    private static boolean overlaps(River river, RiverCarver riverCarver, float extend) {
        return riverCarver.river.intersects(river, extend);
    }

    private static boolean overlaps(River river, Network[] networks, float extend) {
        for(Network network : networks) {
            if (network.overlaps(river, extend)) {
                return true;
            }
        }

        return false;
    }

    public static Network.Builder builder(RiverCarver carver) {
        return new Network.Builder(carver);
    }

    public static class Builder {
        public final RiverCarver carver;
        public final List<Lake> lakes = new ArrayList();
        public final List<Wetland> wetlands = new ArrayList();
        public final List<Network.Builder> children = new ArrayList();
        private float minX;
        private float minZ;
        private float maxX;
        private float maxZ;

        private Builder(RiverCarver carver) {
            this.carver = carver;
            this.addBounds(carver.river);
        }

        public void addBounds(River river) {
            this.minX = min(this.minX, river.x1, river.x2);
            this.minZ = min(this.minZ, river.z1, river.z2);
            this.maxX = max(this.maxX, river.x1, river.x2);
            this.maxZ = max(this.maxZ, river.z1, river.z2);
        }

        public boolean overlaps(River river, Network.Builder parent, float extend) {
            if (parent == this) {
                float x1 = river.x1 - river.ndx * extend;
                float z1 = river.z1 - river.ndz * extend;
                float x2 = river.x1 + river.dx * 0.5F;
                float z2 = river.z1 + river.dz * 0.5F;
                River other = this.carver.river;
                if (Line.intersect(x1, z1, x2, z2, other.x1, other.z1, other.x2, other.z2)) {
                    return true;
                }
            } else if (Network.overlaps(river, this.carver, extend)) {
                return true;
            }

            if (parent != null && parent != this && Network.overlaps(river, this.carver, extend)) {
                return true;
            } else {
                for(Network.Builder branch : this.children) {
                    if (branch.overlaps(river, parent, extend)) {
                        return true;
                    }
                }

                return false;
            }
        }

        public Network build() {
            return this.build(this.recordBounds(Boundsf.builder()).build());
        }

        private Network build(Boundsf bounds) {
            return new Network(
                    this.carver,
                    (Lake[])this.lakes.toArray(Network.LAKE),
                    (Wetland[])this.wetlands.toArray(Network.WETLAND),
                    (Network[])this.children.stream().map(child -> child.build(Boundsf.NONE)).toArray(x$0 -> new Network[x$0]),
                    bounds
            );
        }

        private com.terraforged.engine.util.Boundsf.Builder recordBounds(com.terraforged.engine.util.Boundsf.Builder builder) {
            builder.record(this.carver.river.minX, this.carver.river.minZ);
            builder.record(this.carver.river.maxX, this.carver.river.maxZ);

            for(Network.Builder child : this.children) {
                child.recordBounds(builder);
            }

            for(Lake lake : this.lakes) {
                lake.recordBounds(builder);
            }

            for(Wetland wetland : this.wetlands) {
                wetland.recordBounds(builder);
            }

            return builder;
        }

        private static float min(float min, float... values) {
            for(float v : values) {
                min = Math.min(min, v);
            }

            return min;
        }

        private static float max(float max, float... values) {
            for(float v : values) {
                max = Math.max(max, v);
            }

            return max;
        }
    }
}
