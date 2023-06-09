// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.cell;

import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.TerrainType;

public class Reorder
{
    public float value;
    public float erosion;
    public float sediment;
    public float gradient;
    public float moisture;
    public float temperature;
    public float continentEdge;
    public float continentIdentity;
    public float terrainRegionEdge;
    public float terrainRegionIdentity;
    public float biomeEdge;
    public float biomeIdentity;
    public float macroNoise;
    public float riverMask;
    public int continentX;
    public int continentZ;
    public boolean erosionMask;
    public Terrain terrain;
    public BiomeType biomeType;
    
    public Reorder() {
        this.moisture = 0.5f;
        this.temperature = 0.5f;
        this.biomeEdge = 1.0f;
        this.riverMask = 1.0f;
        this.erosionMask = false;
        this.terrain = TerrainType.NONE;
        this.biomeType = BiomeType.GRASSLAND;
    }
}
