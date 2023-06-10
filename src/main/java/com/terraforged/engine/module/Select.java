//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.module;

import com.terraforged.engine.cell.Cell;
import com.terraforged.noise.Module;

public class Select {
    private final Module control;

    public Select(Module control) {
        this.control = control;
    }

    public float getSelect(Cell cell, float x, float y) {
        return this.control.getValue(x, y);
    }
}
