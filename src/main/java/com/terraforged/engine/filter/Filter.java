// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;

public interface Filter
{
    void apply(final Filterable p0, final int p1, final int p2, final int p3);
    
    default void iterate(final Filterable map, final Visitor visitor) {
        for (int dz = 0; dz < map.getSize().total; ++dz) {
            for (int dx = 0; dx < map.getSize().total; ++dx) {
                final Cell cell = map.getCellRaw(dx, dz);
                visitor.visit(map, cell, dx, dz);
            }
        }
    }
    
    public interface Visitor
    {
        void visit(final Filterable p0, final Cell p1, final int p2, final int p3);
    }
}
