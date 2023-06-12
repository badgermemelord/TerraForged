//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.set.BiomeSet;
import com.terraforged.engine.world.biome.map.set.BiomeTypeSet;
import com.terraforged.engine.world.biome.map.set.RiverSet;
import com.terraforged.engine.world.biome.map.set.TemperatureSet;
import com.terraforged.engine.world.biome.map.set.WetlandSet;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.heightmap.Levels;
import com.terraforged.engine.world.terrain.TerrainCategory;
import com.terraforged.noise.util.NoiseUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import java.util.function.BiConsumer;

public class SimpleBiomeMap<T> implements BiomeMap<T> {
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

    public SimpleBiomeMap(BiomeMapBuilder<T> builder) {
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
        this.terrainBiomes = new BiomeSet[TerrainCategory.values().length];
        this.terrainBiomes[TerrainCategory.SHALLOW_OCEAN.ordinal()] = this.shallowOcean;
        this.terrainBiomes[TerrainCategory.DEEP_OCEAN.ordinal()] = this.deepOcean;
        this.terrainBiomes[TerrainCategory.WETLAND.ordinal()] = this.wetland;
        this.terrainBiomes[TerrainCategory.RIVER.ordinal()] = this.river;
        this.terrainBiomes[TerrainCategory.LAKE.ordinal()] = this.lake;

        for(TerrainCategory type : TerrainCategory.values()) {
            if (this.terrainBiomes[type.ordinal()] == null) {
                this.terrainBiomes[type.ordinal()] = this.land;
            }
        }
    }

    public BiomeContext<T> getContext() {
        return this.context;
    }

    public int provideBiome(Cell cell, Levels levels) {
        TerrainCategory type = cell.terrain.getCategory();
        return type.isSubmerged() && cell.value > levels.water ? this.land.getBiome(cell) : this.terrainBiomes[type.ordinal()].getBiome(cell);
    }

    public int getDeepOcean(Cell cell) {
        return this.deepOcean.getBiome(cell);
    }

    public int getShallowOcean(Cell cell) {
        return this.shallowOcean.getBiome(cell);
    }

    public int getBeach(Cell cell) {
        return this.beach.getBiome(cell);
    }

    public int getCoast(Cell cell) {
        int[] inland = this.land.getSet(cell);
        int[] coastal = this.coast.getSet(cell);
        int maxIndex = inland.length + coastal.length - 1;
        int index = NoiseUtil.round((float)maxIndex * cell.biomeRegionId);
        if (index >= inland.length) {
            index -= inland.length;
            if (index < coastal.length) {
                return coastal[index];
            }
        }

        return Integer.MIN_VALUE;
    }

    public int getRiver(Cell cell) {
        return this.river.getBiome(cell);
    }

    public int getLake(Cell cell) {
        return this.lake.getBiome(cell);
    }

    public int getWetland(Cell cell) {
        return this.wetland.getBiome(cell);
    }

    public int getMountain(Cell cell) {
        return this.mountains.getBiome(cell);
    }

    public int getVolcano(Cell cell) {
        return this.volcanoes.getBiome(cell);
    }

    public int getLand(Cell cell) {
        return this.land.getBiome(cell);
    }

    public BiomeTypeSet getLandSet() {
        return this.land;
    }

    public IntList getAllBiomes(BiomeType type) {
        if (type == BiomeType.ALPINE) {
            return IntLists.EMPTY_LIST;
        } else {
            int size = this.land.getSize(type.ordinal());
            return (IntList)(size == 0 ? IntLists.EMPTY_LIST : IntArrayList.wrap(this.land.getSet(type.ordinal())));
        }
    }

    public void forEach(BiConsumer<String, BiomeSet> consumer) {
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
