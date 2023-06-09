// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.poisson;

import com.terraforged.noise.Source;

import java.util.Random;

public class PoissonContext
{
    public int offsetX;
    public int offsetZ;
    public int startX;
    public int startZ;
    public int endX;
    public int endZ;
    public Module density;
    public final int seed;
    public final Random random;
    
    public PoissonContext(final long seed, final Random random) {
        this.density = Source.ONE;
        this.seed = (int)seed;
        this.random = random;
    }
}
