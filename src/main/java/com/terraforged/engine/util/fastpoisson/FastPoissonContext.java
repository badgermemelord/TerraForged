//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.util.fastpoisson;

import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class FastPoissonContext {
    public final int radius;
    public final int radius2;
    public final float jitter;
    public final float pad;
    public final float frequency;
    public final float scale;
    public final Module density;

    public FastPoissonContext(int radius, float jitter, float frequency, Module density) {
        this.radius = radius;
        this.density = density;
        this.frequency = Math.min(0.5F, frequency);
        this.scale = 1.0F / this.frequency;
        this.jitter = NoiseUtil.clamp(jitter, 0.0F, 1.0F);
        this.pad = (1.0F - this.jitter) * 0.5F;
        this.radius2 = radius * radius;
    }
}
