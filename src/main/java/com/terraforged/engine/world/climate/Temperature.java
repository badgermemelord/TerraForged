//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.climate;

import com.terraforged.noise.Module;
import com.terraforged.noise.util.NoiseUtil;

public class Temperature implements Module {
    private final int power;
    private final float frequency;

    public Temperature(float frequency, int power) {
        this.frequency = frequency;
        this.power = power;
    }

    public float getValue(float x, float y) {
        y *= this.frequency;
        float sin = NoiseUtil.sin(y);
        sin = NoiseUtil.clamp(sin, -1.0F, 1.0F);
        float value = NoiseUtil.pow(sin, this.power);
        value = NoiseUtil.copySign(value, sin);
        return NoiseUtil.map(value, -1.0F, 1.0F, 2.0F);
    }
}
