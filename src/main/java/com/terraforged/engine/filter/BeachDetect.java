// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.heightmap.ControlPoints;
import com.terraforged.engine.world.terrain.TerrainType;

public class BeachDetect implements Filter, Filter.Visitor
{
    private final ControlPoints transition;
    private final float grad2;
    private final int radius = 8;
    private final int diameter = 17;
    
    public BeachDetect(final GeneratorContext context) {
        this.transition = new ControlPoints(context.settings.world.controlPoints);
        final float delta = 0.0018382353f;
        this.grad2 = delta * delta;
    }
    
    @Override
    public void apply(final Filterable map, final int seedX, final int seedZ, final int iterations) {
        this.iterate(map, this);
    }
    
    @Override
    public void visit(final Filterable cellMap, final Cell cell, final int dx, final int dz) {
        if (cell.terrain.isCoast() && cell.continentEdge < this.transition.beach) {
            final Cell n = cellMap.getCellRaw(dx, dz - 8);
            final Cell s = cellMap.getCellRaw(dx, dz + 8);
            final Cell e = cellMap.getCellRaw(dx + 8, dz);
            final Cell w = cellMap.getCellRaw(dx - 8, dz);
            final float gx = this.grad(e, w, cell);
            final float gz = this.grad(n, s, cell);
            final float d2 = gx * gx + gz * gz;
            if (d2 < 0.275f) {
                cell.terrain = TerrainType.BEACH;
            }
        }
    }
    
    private float grad(Cell a, Cell b, final Cell def) {
        int distance = 17;
        if (a.isAbsent()) {
            a = def;
            distance -= 8;
        }
        if (b.isAbsent()) {
            b = def;
            distance -= 8;
        }
        return (a.value - b.value) / distance;
    }
}
