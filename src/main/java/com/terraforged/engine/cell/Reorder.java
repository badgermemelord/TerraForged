//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.cell;

import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.TerrainType;

public class Reorder {
    public float value;
    public float erosion;
    public float sediment;
    public float gradient;
    public float moisture = 0.5F;
    public float temperature = 0.5F;
    public float continentEdge;
    public float continentIdentity;
    public float terrainRegionEdge;
    public float terrainRegionIdentity;
    public float biomeEdge = 1.0F;
    public float biomeIdentity;
    public float macroNoise;
    public float riverMask = 1.0F;
    public int continentX;
    public int continentZ;
    public boolean erosionMask = false;
    public Terrain terrain;
    public BiomeType biomeType;

    public Reorder() {
        this.terrain = TerrainType.NONE;
        this.biomeType = BiomeType.GRASSLAND;
    }
}
