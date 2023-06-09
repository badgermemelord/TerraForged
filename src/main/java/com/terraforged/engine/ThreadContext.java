// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine;

import com.terraforged.engine.cell.Cell;
import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.concurrent.SimpleResource;

public class ThreadContext
{
    public final Resource<Cell> cell;
    
    public ThreadContext() {
        this.cell = new SimpleResource<Cell>(new Cell(), Cell::reset);
    }
}
