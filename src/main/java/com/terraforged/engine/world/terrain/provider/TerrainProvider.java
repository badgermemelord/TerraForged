// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.provider;

import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.settings.TerrainSettings;
import com.terraforged.engine.world.terrain.LandForms;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;

import java.util.List;
import java.util.function.Consumer;

public interface TerrainProvider
{
    LandForms getLandforms();
    
    List<Populator> getPopulators();
    
    int getVariantCount(final Terrain p0);
    
    default Populator getPopulator(final Terrain terrain) {
        return this.getPopulator(terrain, 0);
    }
    
    Populator getPopulator(final Terrain p0, final int p1);
    
    default void forEach(final Consumer<TerrainPopulator> consumer) {
    }
    
    default Terrain getTerrain(final String name) {
        return null;
    }
    
    void registerMixable(final TerrainPopulator p0);
    
    void registerUnMixable(final TerrainPopulator p0);
    
    default void registerMixable(final Terrain type, final Module base, final Module variance, final TerrainSettings.Terrain settings) {
        this.registerMixable(TerrainPopulator.of(type, base, variance, settings));
    }
    
    default void registerUnMixable(final Terrain type, final Module base, final Module variance, final TerrainSettings.Terrain settings) {
        this.registerUnMixable(TerrainPopulator.of(type, base, variance, settings));
    }
}
