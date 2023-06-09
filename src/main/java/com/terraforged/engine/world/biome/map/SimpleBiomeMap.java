// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.set.*;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.terrain.TerrainCategory;
import com.terraforged.noise.util.NoiseUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;

import java.util.function.BiConsumer;

public class SimpleBiomeMap<T> implements BiomeMap<T>
{
    private final BiomeContext<T> context;
    private final BiomeSet deepOcean;
    private final BiomeSet shallowOcean;
    private final BiomeSet beach;
    private final BiomeSet coast;
    private final BiomeSet river;
    private final BiomeSet lake;
    private final BiomeSet wetland;
    private final BiomeTypeSet land;
    private final BiomeSet mountains;
    private final BiomeSet volcanoes;
    private final BiomeSet[] terrainBiomes;
    
    public SimpleBiomeMap(final BiomeMapBuilder<T> builder) {
        this.context = builder.context;
        this.deepOcean = new TemperatureSet(builder.deepOceans, builder.defaults.deepOcean, builder.context);
        this.shallowOcean = new TemperatureSet(builder.oceans, builder.defaults.ocean, builder.context);
        this.beach = new TemperatureSet(builder.beaches, builder.defaults.beach, builder.context);
        this.coast = new TemperatureSet(builder.coasts, builder.defaults.coast, builder.context);
        this.river = new RiverSet(builder.rivers, this, builder.defaults.river, builder.context);
        this.lake = new TemperatureSet(builder.lakes, builder.defaults.lake, builder.context);
        this.wetland = new WetlandSet(builder.wetlands, this, builder.defaults.wetland, builder.context);
        this.mountains = new TemperatureSet(builder.mountains, builder.defaults.mountain, builder.context);
        this.volcanoes = new TemperatureSet(builder.volcanoes, builder.defaults.volcanoes, builder.context);
        this.land = new BiomeTypeSet(builder.map, builder.defaults.land, builder.context);
        (this.terrainBiomes = new BiomeSet[TerrainCategory.values().length])[TerrainCategory.SHALLOW_OCEAN.ordinal()] = this.shallowOcean;
        this.terrainBiomes[TerrainCategory.DEEP_OCEAN.ordinal()] = this.deepOcean;
        this.terrainBiomes[TerrainCategory.WETLAND.ordinal()] = this.wetland;
        this.terrainBiomes[TerrainCategory.RIVER.ordinal()] = this.river;
        this.terrainBiomes[TerrainCategory.LAKE.ordinal()] = this.lake;
        for (final TerrainCategory type : TerrainCategory.values()) {
            if (this.terrainBiomes[type.ordinal()] == null) {
                this.terrainBiomes[type.ordinal()] = this.land;
            }
        }
    }
    
    @Override
    public BiomeContext<T> getContext() {
        return this.context;
    }
    
    @Override
    public int provideBiome(final Cell cell, final Levels levels) {
        final TerrainCategory type = cell.terrain.getCategory();
        if (type.isSubmerged() && cell.value > levels.water) {
            return this.land.getBiome(cell);
        }
        return this.terrainBiomes[type.ordinal()].getBiome(cell);
    }
    
    @Override
    public int getDeepOcean(final Cell cell) {
        return this.deepOcean.getBiome(cell);
    }
    
    @Override
    public int getShallowOcean(final Cell cell) {
        return this.shallowOcean.getBiome(cell);
    }
    
    @Override
    public int getBeach(final Cell cell) {
        return this.beach.getBiome(cell);
    }
    
    @Override
    public int getCoast(final Cell cell) {
        final int[] inland = this.land.getSet(cell);
        final int[] coastal = this.coast.getSet(cell);
        final int maxIndex = inland.length + coastal.length - 1;
        int index = NoiseUtil.round(maxIndex * cell.biomeRegionId);
        if (index >= inland.length) {
            index -= inland.length;
            if (index < coastal.length) {
                return coastal[index];
            }
        }
        return Integer.MIN_VALUE;
    }
    
    @Override
    public int getRiver(final Cell cell) {
        return this.river.getBiome(cell);
    }
    
    @Override
    public int getLake(final Cell cell) {
        return this.lake.getBiome(cell);
    }
    
    @Override
    public int getWetland(final Cell cell) {
        return this.wetland.getBiome(cell);
    }
    
    @Override
    public int getMountain(final Cell cell) {
        return this.mountains.getBiome(cell);
    }
    
    @Override
    public int getVolcano(final Cell cell) {
        return this.volcanoes.getBiome(cell);
    }
    
    @Override
    public int getLand(final Cell cell) {
        return this.land.getBiome(cell);
    }
    
    @Override
    public BiomeTypeSet getLandSet() {
        return this.land;
    }
    
    @Override
    public IntList getAllBiomes(final BiomeType type) {
        if (type == BiomeType.ALPINE) {
            return (IntList)IntLists.EMPTY_LIST;
        }
        final int size = this.land.getSize(type.ordinal());
        if (size == 0) {
            return (IntList)IntLists.EMPTY_LIST;
        }
        return (IntList)IntArrayList.wrap(this.land.getSet(type.ordinal()));
    }
    
    @Override
    public void forEach(final BiConsumer<String, BiomeSet> consumer) {
        consumer.accept("deep_ocean", this.deepOcean);
        consumer.accept("shallow_ocean", this.shallowOcean);
        consumer.accept("beach", this.beach);
        consumer.accept("coast", this.coast);
        consumer.accept("river", this.river);
        consumer.accept("lake", this.lake);
        consumer.accept("wetland", this.wetland);
        consumer.accept("land", this.land);
        consumer.accept("mountain", this.mountains);
        consumer.accept("volcano", this.volcanoes);
    }
}
