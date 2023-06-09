// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.world.biome.type.BiomeType;

public interface BiomeCollector<T> extends BiomeMap.Builder<T>
{
    BiomeCollector<T> add(final T p0);
    
    default BiomeMap.Builder<T> addBeach(final T biome, final int count) {
        return this.add(biome);
    }
    
    default BiomeMap.Builder<T> addCoast(final T biome, final int count) {
        return this.add(biome);
    }
    
    default BiomeMap.Builder<T> addLake(final T biome, final int count) {
        return this.add(biome);
    }
    
    default BiomeMap.Builder<T> addLand(final BiomeType type, final T biome, final int count) {
        return this.add(biome);
    }
    
    default BiomeMap.Builder<T> addMountain(final T biome, final int count) {
        return this.add(biome);
    }
    
    default BiomeMap.Builder<T> addOcean(final T biome, final int count) {
        return this.add(biome);
    }
    
    default BiomeMap.Builder<T> addRiver(final T biome, final int count) {
        return this.add(biome);
    }
    
    default BiomeMap.Builder<T> addVolcano(final T biome, final int count) {
        return this.add(biome);
    }
    
    default BiomeMap.Builder<T> addWetland(final T biome, final int count) {
        return this.add(biome);
    }
    
    default BiomeMap<T> build() {
        throw new UnsupportedOperationException();
    }
}
