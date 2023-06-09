// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.render;

import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.heightmap.Levels;

public class RenderSettings
{
    public int width;
    public int height;
    public int resolution;
    public float zoom;
    public final Levels levels;
    public RenderMode renderMode;
    
    public RenderSettings(final GeneratorContext context) {
        this.zoom = 1.0f;
        this.renderMode = RenderMode.BIOME_TYPE;
        this.levels = context.levels;
    }
}
