//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.continent;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Populator;
import com.terraforged.engine.world.rivermap.Rivermap;

public interface Continent extends Populator {
    float getEdgeValue(float var1, float var2);

    default float getLandValue(float x, float z) {
        return this.getEdgeValue(x, z);
    }

    long getNearestCenter(float var1, float var2);

    Rivermap getRivermap(int var1, int var2);

    default Rivermap getRivermap(Cell cell) {
        return this.getRivermap(cell.continentX, cell.continentZ);
    }
}
