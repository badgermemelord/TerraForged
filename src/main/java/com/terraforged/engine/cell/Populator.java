//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.cell;

import com.terraforged.engine.concurrent.Resource;
import com.terraforged.noise.Module;

public interface Populator extends Module {
    void apply(Cell var1, float var2, float var3);

    default float getValue(float x, float z) {
        Resource<Cell> cell = Cell.getResource();
        Throwable var4 = null;

        float var5;
        try {
            this.apply((Cell)cell.get(), x, z);
            var5 = ((Cell)cell.get()).value;
        } catch (Throwable var14) {
            var4 = var14;
            throw var14;
        } finally {
            if (cell != null) {
                if (var4 != null) {
                    try {
                        cell.close();
                    } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                    }
                } else {
                    cell.close();
                }
            }

        }

        return var5;
    }
}
