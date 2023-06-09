// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.cell;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.SimpleResource;
import com.terraforged.engine.concurrent.pool.ThreadLocalPool;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.TerrainType;

public class Cell
{
    private static final Cell defaults;
    private static final Cell EMPTY;
    private static final ThreadLocalPool<Cell> POOL;
    private static final ThreadLocal<SimpleResource<Cell>> LOCAL;
    public float value;
    public float erosion;
    public float sediment;
    public float gradient;
    public float moisture;
    public float temperature;
    public float continentId;
    public float continentEdge;
    public float terrainRegionId;
    public float terrainRegionEdge;
    public long terrainRegionCenter;
    public float biomeRegionId;
    public float biomeRegionEdge;
    public float macroBiomeId;
    public float riverMask;
    public int continentX;
    public int continentZ;
    public boolean erosionMask;
    public Terrain terrain;
    public BiomeType biome;
    
    public Cell() {
        this.moisture = 0.5f;
        this.temperature = 0.5f;
        this.biomeRegionEdge = 1.0f;
        this.riverMask = 1.0f;
        this.erosionMask = false;
        this.terrain = TerrainType.NONE;
        this.biome = BiomeType.GRASSLAND;
    }
    
    public void copyFrom(final Cell other) {
        this.value = other.value;
        this.continentX = other.continentX;
        this.continentZ = other.continentZ;
        this.continentId = other.continentId;
        this.continentEdge = other.continentEdge;
        this.terrainRegionId = other.terrainRegionId;
        this.terrainRegionEdge = other.terrainRegionEdge;
        this.biomeRegionId = other.biomeRegionId;
        this.biomeRegionEdge = other.biomeRegionEdge;
        this.riverMask = other.riverMask;
        this.erosionMask = other.erosionMask;
        this.moisture = other.moisture;
        this.temperature = other.temperature;
        this.macroBiomeId = other.macroBiomeId;
        this.gradient = other.gradient;
        this.erosion = other.erosion;
        this.sediment = other.sediment;
        this.biome = other.biome;
        this.terrain = other.terrain;
    }
    
    public Cell reset() {
        this.copyFrom(Cell.defaults);
        return this;
    }
    
    public boolean isAbsent() {
        return false;
    }
    
    public static Cell empty() {
        return Cell.EMPTY;
    }
    
    public static Resource<Cell> getResource() {
        final SimpleResource<Cell> resource = Cell.LOCAL.get();
        if (resource.isOpen()) {
            return Cell.POOL.get();
        }
        return resource;
    }
    
    static {
        defaults = new Cell();
        EMPTY = new Cell() {
            @Override
            public boolean isAbsent() {
                return true;
            }
        };
        POOL = new ThreadLocalPool<Cell>(32, Cell::new, Cell::reset);
        final SimpleResource simpleResource;
        LOCAL = ThreadLocal.withInitial(() -> {
            new SimpleResource(new Cell(), Cell::reset);
            return simpleResource;
        });
    }
    
    public interface ContextVisitor<C>
    {
        void visit(final Cell p0, final int p1, final int p2, final C p3);
    }
    
    public interface Visitor
    {
        void visit(final Cell p0, final int p1, final int p2);
    }
}
