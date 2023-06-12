//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.world.biome.map.BiomeMap.Builder;
import com.terraforged.engine.world.biome.type.BiomeType;

public interface BiomeCollector<T> extends Builder<T> {
    BiomeCollector<T> add(T var1);

    default Builder<T> addBeach(T biome, int count) {
        return this.add(biome);
    }

    default Builder<T> addCoast(T biome, int count) {
        return this.add(biome);
    }

    default Builder<T> addLake(T biome, int count) {
        return this.add(biome);
    }

    default Builder<T> addLand(BiomeType type, T biome, int count) {
        return this.add(biome);
    }

    default Builder<T> addMountain(T biome, int count) {
        return this.add(biome);
    }

    default Builder<T> addOcean(T biome, int count) {
        return this.add(biome);
    }

    default Builder<T> addRiver(T biome, int count) {
        return this.add(biome);
    }

    default Builder<T> addVolcano(T biome, int count) {
        return this.add(biome);
    }

    default Builder<T> addWetland(T biome, int count) {
        return this.add(biome);
    }

    default BiomeMap<T> build() {
        throw new UnsupportedOperationException();
    }
}
