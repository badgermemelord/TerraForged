// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.tile.Size;

public interface Filterable
{
    int getBlockX();
    
    int getBlockZ();
    
    Size getSize();
    
    Cell[] getBacking();
    
    Cell getCellRaw(final int p0, final int p1);
}
