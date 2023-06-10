//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.set.BiomeSet;
import com.terraforged.engine.world.biome.map.set.BiomeTypeSet;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.heightmap.Levels;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.function.BiConsumer;

public interface BiomeMap<T> {
    int NULL_BIOME = Integer.MIN_VALUE;

    BiomeContext<T> getContext();

    int getBeach(Cell var1);

    int getCoast(Cell var1);

    int getRiver(Cell var1);

    int getLake(Cell var1);

    int getWetland(Cell var1);

    int getShallowOcean(Cell var1);

    int getDeepOcean(Cell var1);

    int getLand(Cell var1);

    int getMountain(Cell var1);

    int getVolcano(Cell var1);

    int provideBiome(Cell var1, Levels var2);

    BiomeTypeSet getLandSet();

    IntList getAllBiomes(BiomeType var1);

    void forEach(BiConsumer<String, BiomeSet> var1);

    static boolean isValid(int id) {
        return id != Integer.MIN_VALUE;
    }

    public interface Builder<T> {
        BiomeMap.Builder<T> addOcean(T var1, int var2);

        BiomeMap.Builder<T> addBeach(T var1, int var2);

        BiomeMap.Builder<T> addCoast(T var1, int var2);

        BiomeMap.Builder<T> addRiver(T var1, int var2);

        BiomeMap.Builder<T> addWetland(T var1, int var2);

        BiomeMap.Builder<T> addLake(T var1, int var2);

        BiomeMap.Builder<T> addMountain(T var1, int var2);

        BiomeMap.Builder<T> addVolcano(T var1, int var2);

        BiomeMap.Builder<T> addLand(BiomeType var1, T var2, int var3);

        BiomeMap<T> build();
    }
}
