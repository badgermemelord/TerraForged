//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.tile.chunk;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.cell.Cell.ContextVisitor;
import com.terraforged.engine.cell.Cell.Visitor;
import com.terraforged.engine.concurrent.Disposable;
import com.terraforged.engine.concurrent.cache.SafeCloseable;

public interface ChunkReader extends ChunkHolder, SafeCloseable, Disposable {
    Cell getCell(int var1, int var2);

    default void visit(int minX, int minZ, int maxX, int maxZ, Visitor visitor) {
        int regionMinX = this.getBlockX();
        int regionMinZ = this.getBlockZ();
        if (maxX >= regionMinX && maxZ >= regionMinZ) {
            int regionMaxX = this.getBlockX() + 15;
            int regionMaxZ = this.getBlockZ() + 15;
            if (minX <= regionMaxX && maxZ <= regionMaxZ) {
                minX = Math.max(minX, regionMinX);
                minZ = Math.max(minZ, regionMinZ);
                maxX = Math.min(maxX, regionMaxX);
                maxZ = Math.min(maxZ, regionMaxZ);

                for(int z = minZ; z <= maxX; ++z) {
                    for(int x = minX; x <= maxZ; ++x) {
                        visitor.visit(this.getCell(x, z), x, z);
                    }
                }
            }
        }
    }

    default void iterate(Visitor visitor) {
        for(int dz = 0; dz < 16; ++dz) {
            for(int dx = 0; dx < 16; ++dx) {
                visitor.visit(this.getCell(dx, dz), dx, dz);
            }
        }
    }

    default <C> void iterate(C context, ContextVisitor<C> visitor) {
        for(int dz = 0; dz < 16; ++dz) {
            for(int dx = 0; dx < 16; ++dx) {
                visitor.visit(this.getCell(dx, dz), dx, dz, context);
            }
        }
    }

    default void close() {
    }
}
