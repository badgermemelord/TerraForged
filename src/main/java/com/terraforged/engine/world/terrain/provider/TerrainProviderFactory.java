// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.provider;

import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.heightmap.RegionConfig;

public interface TerrainProviderFactory
{
    TerrainProvider create(final GeneratorContext p0, final RegionConfig p1, final Populator p2);
}
