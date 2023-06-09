// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.tile.chunk;

import com.terraforged.engine.cell.Cell;

public interface ChunkWriter extends ChunkHolder
{
    Cell genCell(final int p0, final int p1);
    
    default void generate(final Cell.Visitor visitor) {
        for (int dz = 0; dz < 16; ++dz) {
            for (int dx = 0; dx < 16; ++dx) {
                visitor.visit(this.genCell(dx, dz), dx, dz);
            }
        }
    }
    
    default <T> void generate(final T ctx, final Visitor<T> visitor) {
        final int blockX = this.getBlockX();
        final int blockZ = this.getBlockZ();
        for (int dz = 0; dz < 16; ++dz) {
            for (int dx = 0; dx < 16; ++dx) {
                visitor.visit(this.genCell(dx, dz), dx, dz, blockX + dx, blockZ + dz, ctx);
            }
        }
    }
    
    public interface Visitor<T>
    {
        void visit(final Cell p0, final int p1, final int p2, final int p3, final int p4, final T p5);
    }
}
