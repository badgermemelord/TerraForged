// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.type;

import com.terraforged.engine.cell.Cell;
import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

import java.awt.*;

public enum BiomeType
{
    TROPICAL_RAINFOREST(7, 83, 48, new Color(7, 83, 48)), 
    SAVANNA(151, 165, 39, new Color(151, 165, 39)), 
    DESERT(200, 113, 55, new Color(200, 113, 55)), 
    TEMPERATE_RAINFOREST(10, 84, 109, new Color(10, 160, 65)), 
    TEMPERATE_FOREST(44, 137, 160, new Color(50, 200, 80)), 
    GRASSLAND(179, 124, 6, new Color(100, 220, 60)), 
    COLD_STEPPE(131, 112, 71, new Color(175, 180, 150)), 
    STEPPE(199, 155, 60, new Color(200, 200, 120)), 
    TAIGA(91, 143, 82, new Color(91, 143, 82)), 
    TUNDRA(147, 167, 172, new Color(147, 167, 172)), 
    ALPINE(0, 0, 0, new Color(160, 120, 170));
    
    public static final int RESOLUTION = 256;
    public static final int MAX = 255;
    private static final BiomeType[] BIOMES;
    private final Color lookup;
    private final Color color;
    private float minTemp;
    private float maxTemp;
    private float minMoist;
    private float maxMoist;
    
    private BiomeType(final int r, final int g, final int b, final Color color) {
        this(new Color(r, g, b), color);
    }
    
    private BiomeType(final Color lookup, final Color color) {
        this.lookup = lookup;
        this.color = BiomeTypeColors.getInstance().getColor(this.name(), color);
    }
    
    Color getLookup() {
        return this.lookup;
    }
    
    public float mapTemperature(final float value) {
        return (value - this.minTemp) / (this.maxTemp - this.minTemp);
    }
    
    public float mapMoisture(final float value) {
        return (value - this.minMoist) / (this.maxMoist - this.minMoist);
    }
    
    public int getId() {
        return this.ordinal();
    }
    
    public float getMinMoisture() {
        return this.minMoist;
    }
    
    public float getMaxMoisture() {
        return this.maxMoist;
    }
    
    public float getMinTemperature() {
        return this.minTemp;
    }
    
    public float getMaxTemperature() {
        return this.maxTemp;
    }
    
    public Color getColor() {
        return this.color;
    }
    
    public boolean isExtreme() {
        return this == BiomeType.TUNDRA || this == BiomeType.DESERT;
    }
    
    public static BiomeType get(final int id) {
        return BiomeType.BIOMES[id];
    }
    
    public static BiomeType get(final float temperature, final float moisture) {
        return getCurve(temperature, moisture);
    }
    
    public static BiomeType getLinear(final float temperature, final float moisture) {
        final int x = NoiseUtil.round(255.0f * temperature);
        final int y = getYLinear(x, temperature, moisture);
        return getType(x, y);
    }
    
    public static BiomeType getCurve(final float temperature, final float moisture) {
        final int x = NoiseUtil.round(255.0f * temperature);
        final int y = getYCurve(x, temperature, moisture);
        return getType(x, y);
    }
    
    public static void apply(final Cell cell) {
        applyCurve(cell);
    }
    
    public static void applyLinear(final Cell cell) {
        cell.biome = get(cell.temperature, cell.moisture);
    }
    
    public static void applyCurve(final Cell cell) {
        cell.biome = get(cell.temperature, cell.moisture);
    }
    
    private static BiomeType getType(final int x, final int y) {
        return BiomeTypeLoader.getInstance().getTypeMap()[y][x];
    }
    
    private static int getYLinear(final int x, final float temperature, final float moisture) {
        if (moisture > temperature) {
            return x;
        }
        return NoiseUtil.round(255.0f * moisture);
    }
    
    private static int getYCurve(final int x, final float temperature, final float moisture) {
        final int max = x + (255 - x) / 2;
        final int y = NoiseUtil.round(max * moisture);
        return Math.min(x, y);
    }
    
    private static void init() {
        for (final BiomeType type : values()) {
            final Vec2f[] ranges = BiomeTypeLoader.getInstance().getRanges(type);
            type.minTemp = ranges[0].x;
            type.maxTemp = ranges[0].y;
            type.minMoist = ranges[1].x;
            type.maxMoist = ranges[1].y;
        }
    }
    
    static {
        BIOMES = values();
        init();
    }
}
