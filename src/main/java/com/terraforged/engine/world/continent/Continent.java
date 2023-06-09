// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.world.rivermap.Rivermap;

public interface Continent extends Populator
{
    float getEdgeValue(final float p0, final float p1);
    
    default float getLandValue(final float x, final float z) {
        return this.getEdgeValue(x, z);
    }
    
    long getNearestCenter(final float p0, final float p1);
    
    Rivermap getRivermap(final int p0, final int p1);
    
    default Rivermap getRivermap(final Cell cell) {
        return this.getRivermap(cell.continentX, cell.continentZ);
    }
}
