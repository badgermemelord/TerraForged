// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.modifier;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.climate.Climate;

public abstract class AbstractOffsetModifier implements BiomeModifier
{
    private final Climate climate;
    
    public AbstractOffsetModifier(final Climate climate) {
        this.climate = climate;
    }
    
    @Override
    public int modify(final int in, final Cell cell, final int x, final int z) {
        final float dx = this.climate.getOffsetX((float)x, (float)z, 50.0f);
        final float dz = this.climate.getOffsetX((float)x, (float)z, 50.0f);
        return this.modify(in, cell, x, z, x + dx, z + dz);
    }
    
    protected abstract int modify(final int p0, final Cell p1, final int p2, final int p3, final float p4, final float p5);
}
