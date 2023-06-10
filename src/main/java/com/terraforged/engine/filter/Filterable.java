//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.tile.Size;

public interface Filterable {
    int getBlockX();

    int getBlockZ();

    Size getSize();

    Cell[] getBacking();

    Cell getCellRaw(int var1, int var2);
}
