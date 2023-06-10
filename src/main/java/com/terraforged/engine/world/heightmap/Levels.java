//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.heightmap;

import com.terraforged.engine.settings.Settings;
import com.terraforged.engine.settings.WorldSettings;
import com.terraforged.noise.util.NoiseUtil;

public class Levels {
    public final int worldHeight;
    public final float unit;
    public final int waterY;
    private final int groundY;
    public final int groundLevel;
    public final int waterLevel;
    public final float ground;
    public final float water;
    private final float elevationRange;

    public Levels(WorldSettings settings) {
        this(settings.properties.worldHeight, settings.properties.seaLevel);
    }

    public Levels(int height, int seaLevel) {
        this.worldHeight = Math.max(1, height);
        this.unit = NoiseUtil.div(1, this.worldHeight);
        this.waterLevel = seaLevel;
        this.groundLevel = this.waterLevel + 1;
        this.waterY = Math.min(this.waterLevel - 1, this.worldHeight);
        this.groundY = Math.min(this.groundLevel - 1, this.worldHeight);
        this.ground = NoiseUtil.div(this.groundY, this.worldHeight);
        this.water = NoiseUtil.div(this.waterY, this.worldHeight);
        this.elevationRange = 1.0F - this.water;
    }

    public int scale(float value) {
        return value >= 1.0F ? this.worldHeight - 1 : (int)(value * (float)this.worldHeight);
    }

    public float elevation(float value) {
        return value <= this.water ? 0.0F : (value - this.water) / this.elevationRange;
    }

    public float elevation(int y) {
        return y <= this.waterY ? 0.0F : this.scale(y - this.waterY) / this.elevationRange;
    }

    public float scale(int level) {
        return NoiseUtil.div(level, this.worldHeight);
    }

    public float water(int amount) {
        return NoiseUtil.div(this.waterY + amount, this.worldHeight);
    }

    public float ground(int amount) {
        return NoiseUtil.div(this.groundY + amount, this.worldHeight);
    }

    public static float scale(int steps, Settings settings) {
        return (float)steps / (float)settings.world.properties.worldHeight;
    }
}
