// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.cell;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.noise.Module;

public interface Populator extends Module
{
    void apply(final Cell p0, final float p1, final float p2);
    
    default float getValue(final float x, final float z) {
        try (final Resource<Cell> cell = Cell.getResource()) {
            this.apply(cell.get(), x, z);
            return cell.get().value;
        }
    }
}
