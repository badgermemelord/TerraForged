// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.biome.map.set.BiomeSet;
import com.terraforged.engine.world.biome.map.set.BiomeTypeSet;
import com.terraforged.engine.world.biome.type.BiomeType;
import com.terraforged.engine.world.heightmap.Levels;
import it.unimi.dsi.fastutil.ints.IntList;

import java.util.function.BiConsumer;

public interface BiomeMap<T>
{
    public static final int NULL_BIOME = Integer.MIN_VALUE;
    
    BiomeContext<T> getContext();
    
    int getBeach(final Cell p0);
    
    int getCoast(final Cell p0);
    
    int getRiver(final Cell p0);
    
    int getLake(final Cell p0);
    
    int getWetland(final Cell p0);
    
    int getShallowOcean(final Cell p0);
    
    int getDeepOcean(final Cell p0);
    
    int getLand(final Cell p0);
    
    int getMountain(final Cell p0);
    
    int getVolcano(final Cell p0);
    
    int provideBiome(final Cell p0, final Levels p1);
    
    BiomeTypeSet getLandSet();
    
    IntList getAllBiomes(final BiomeType p0);
    
    void forEach(final BiConsumer<String, BiomeSet> p0);
    
    default boolean isValid(final int id) {
        return id != Integer.MIN_VALUE;
    }
    
    public interface Builder<T>
    {
        Builder<T> addOcean(final T p0, final int p1);
        
        Builder<T> addBeach(final T p0, final int p1);
        
        Builder<T> addCoast(final T p0, final int p1);
        
        Builder<T> addRiver(final T p0, final int p1);
        
        Builder<T> addWetland(final T p0, final int p1);
        
        Builder<T> addLake(final T p0, final int p1);
        
        Builder<T> addMountain(final T p0, final int p1);
        
        Builder<T> addVolcano(final T p0, final int p1);
        
        Builder<T> addLand(final BiomeType p0, final T p1, final int p2);
        
        BiomeMap<T> build();
    }
}
