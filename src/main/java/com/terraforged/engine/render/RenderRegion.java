// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.render;

import com.terraforged.engine.tile.Tile;

public class RenderRegion
{
    private final Tile tile;
    private final Object lock;
    private RenderBuffer mesh;
    
    public RenderRegion(final Tile tile) {
        this.lock = new Object();
        this.tile = tile;
    }
    
    public Tile getTile() {
        return this.tile;
    }
    
    public RenderBuffer getMesh() {
        synchronized (this.lock) {
            return this.mesh;
        }
    }
    
    public void setMesh(final RenderBuffer mesh) {
        synchronized (this.lock) {
            this.mesh = mesh;
        }
    }
    
    public void clear() {
        synchronized (this.lock) {
            if (this.mesh != null) {
                this.mesh.dispose();
                this.mesh = null;
            }
        }
    }
}
