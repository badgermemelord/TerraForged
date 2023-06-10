// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.provider;

import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.settings.TerrainSettings;
import com.terraforged.engine.world.terrain.LandForms;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.noise.Module;
import java.util.List;
import java.util.function.Consumer;

public interface TerrainProvider {
    LandForms getLandforms();

    List<Populator> getPopulators();

    int getVariantCount(Terrain var1);

    default Populator getPopulator(Terrain terrain) {
        return this.getPopulator(terrain, 0);
    }

    Populator getPopulator(Terrain var1, int var2);

    default void forEach(Consumer<TerrainPopulator> consumer) {
    }

    default Terrain getTerrain(String name) {
        return null;
    }

    void registerMixable(TerrainPopulator var1);

    void registerUnMixable(TerrainPopulator var1);

    default void registerMixable(Terrain type, Module base, Module variance, TerrainSettings.Terrain settings) {
        this.registerMixable(TerrainPopulator.of(type, base, variance, settings));
    }

    default void registerUnMixable(Terrain type, Module base, Module variance, TerrainSettings.Terrain settings) {
        this.registerUnMixable(TerrainPopulator.of(type, base, variance, settings));
    }
}

