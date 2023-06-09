// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.heightmap.Levels;

public class Steepness implements Filter, Filter.Visitor
{
    private final int radius;
    private final float scaler;
    private final float waterLevel;
    private final float maxBeachLevel;
    
    public Steepness(final int radius, final float scaler, final Levels levels) {
        this.radius = radius;
        this.scaler = scaler;
        this.waterLevel = levels.water;
        this.maxBeachLevel = levels.water(6);
    }
    
    @Override
    public void apply(final Filterable cellMap, final int seedX, final int seedZ, final int iterations) {
        this.iterate(cellMap, this);
    }
    
    @Override
    public void visit(final Filterable cellMap, final Cell cell, final int cx, final int cz) {
        float totalHeightDif = 0.0f;
        for (int dz = -1; dz <= 2; ++dz) {
            for (int dx = -1; dx <= 2; ++dx) {
                if (dx != 0 || dz != 0) {
                    final int x = cx + dx * this.radius;
                    final int z = cz + dz * this.radius;
                    final Cell neighbour = cellMap.getCellRaw(x, z);
                    if (!neighbour.isAbsent()) {
                        final float height = Math.max(neighbour.value, this.waterLevel);
                        totalHeightDif += Math.abs(cell.value - height) / this.radius;
                    }
                }
            }
        }
        cell.gradient = Math.min(1.0f, totalHeightDif * this.scaler);
    }
}
