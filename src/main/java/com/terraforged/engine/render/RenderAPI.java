// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.render;

public interface RenderAPI
{
    void pushMatrix();
    
    void popMatrix();
    
    void translate(final float p0, final float p1, final float p2);
    
    void rotateX(final float p0);
    
    void rotateY(final float p0);
    
    void rotateZ(final float p0);
    
    RenderBuffer createBuffer();
}
