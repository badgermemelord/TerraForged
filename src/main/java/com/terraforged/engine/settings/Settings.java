// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.settings;

import com.terraforged.engine.serialization.annotation.Serializable;

@Serializable
public class Settings
{
    public WorldSettings world;
    public ClimateSettings climate;
    public TerrainSettings terrain;
    public RiverSettings rivers;
    public FilterSettings filters;
    
    public Settings() {
        this.world = new WorldSettings();
        this.climate = new ClimateSettings();
        this.terrain = new TerrainSettings();
        this.rivers = new RiverSettings();
        this.filters = new FilterSettings();
    }
}
