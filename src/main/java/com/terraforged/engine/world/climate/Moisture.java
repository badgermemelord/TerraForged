//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.climate;

import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;

public class Moisture implements Module {
    private final Module source;
    private final int power;

    public Moisture(int seed, int scale, int power) {
        this(Source.simplex(seed, scale, 1).clamp(0.125, 0.875).map(0.0, 1.0), power);
    }

    public Moisture(Module source, int power) {
        this.source = source.freq(0.5, 1.0);
        this.power = power;
    }

    public float getValue(float x, float y) {
        float noise = this.source.getValue(x, y);
        if (this.power < 2) {
            return noise;
        } else {
            noise = (noise - 0.5F) * 2.0F;
            float value = NoiseUtil.pow(noise, this.power);
            value = NoiseUtil.copySign(value, noise);
            return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
        }
    }
}
