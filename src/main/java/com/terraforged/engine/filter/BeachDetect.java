//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.filter.Filter.Visitor;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.heightmap.ControlPoints;
import com.terraforged.engine.world.terrain.TerrainType;

public class BeachDetect implements Filter, Visitor {
    private final ControlPoints transition;
    private final float grad2;
    private final int radius = 8;
    private final int diameter = 17;

    public BeachDetect(GeneratorContext context) {
        this.transition = new ControlPoints(context.settings.world.controlPoints);
        float delta = 0.0018382353F;
        this.grad2 = delta * delta;
    }

    public void apply(Filterable map, int seedX, int seedZ, int iterations) {
        this.iterate(map, this);
    }

    public void visit(Filterable cellMap, Cell cell, int dx, int dz) {
        if (cell.terrain.isCoast() && cell.continentEdge < this.transition.beach) {
            Cell n = cellMap.getCellRaw(dx, dz - 8);
            Cell s = cellMap.getCellRaw(dx, dz + 8);
            Cell e = cellMap.getCellRaw(dx + 8, dz);
            Cell w = cellMap.getCellRaw(dx - 8, dz);
            float gx = this.grad(e, w, cell);
            float gz = this.grad(n, s, cell);
            float d2 = gx * gx + gz * gz;
            if (d2 < 0.275F) {
                cell.terrain = TerrainType.BEACH;
            }
        }
    }

    private float grad(Cell a, Cell b, Cell def) {
        int distance = 17;
        if (a.isAbsent()) {
            a = def;
            distance -= 8;
        }

        if (b.isAbsent()) {
            b = def;
            distance -= 8;
        }

        return (a.value - b.value) / (float)distance;
    }
}
