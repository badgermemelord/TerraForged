// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.rivermap;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.cache.ExpiringEntry;
import com.terraforged.engine.world.heightmap.Heightmap;
import com.terraforged.engine.world.rivermap.gen.GenWarp;
import com.terraforged.engine.world.rivermap.river.Network;
import com.terraforged.noise.domain.Domain;

public class Rivermap implements ExpiringEntry
{
    private final int x;
    private final int z;
    private final Domain lakeWarp;
    private final Domain riverWarp;
    private final Network[] networks;
    private final long timestamp;
    
    public Rivermap(final int x, final int z, final Network[] networks, final GenWarp warp) {
        this.timestamp = System.currentTimeMillis();
        this.x = x;
        this.z = z;
        this.networks = networks;
        this.lakeWarp = warp.lake;
        this.riverWarp = warp.river;
    }
    
    public void apply(final Cell cell, final float x, final float z) {
        final float rx = this.riverWarp.getX(x, z);
        final float rz = this.riverWarp.getY(x, z);
        final float lx = this.lakeWarp.getOffsetX(rx, rz);
        final float lz = this.lakeWarp.getOffsetY(rx, rz);
        for (final Network network : this.networks) {
            if (network.contains(rx, rz)) {
                network.carve(cell, rx, rz, lx, lz);
            }
        }
    }
    
    @Override
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getZ() {
        return this.z;
    }
    
    public static Rivermap get(final Cell cell, final Rivermap instance, final Heightmap heightmap) {
        return get(cell.continentX, cell.continentZ, instance, heightmap);
    }
    
    public static Rivermap get(final int x, final int z, final Rivermap instance, final Heightmap heightmap) {
        if (instance != null && x == instance.getX() && z == instance.getZ()) {
            return instance;
        }
        return heightmap.getContinent().getRivermap(x, z);
    }
}
