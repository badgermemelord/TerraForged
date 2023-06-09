// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.module;

import com.terraforged.engine.cell.Cell;

public class Select
{
    private final Module control;
    
    public Select(final Module control) {
        this.control = control;
    }
    
    public float getSelect(final Cell cell, final float x, final float y) {
        return this.control.getValue(x, y);
    }
}
