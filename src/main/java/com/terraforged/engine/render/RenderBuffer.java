// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.render;

public interface RenderBuffer
{
    void beginQuads();
    
    void endQuads();
    
    void vertex(final float p0, final float p1, final float p2);
    
    void color(final float p0, final float p1, final float p2);
    
    void draw();
    
    default void dispose() {
    }
    
    default void noFill() {
    }
    
    default void noStroke() {
    }
}
