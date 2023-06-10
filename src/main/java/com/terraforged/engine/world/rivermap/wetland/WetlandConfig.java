//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.rivermap.wetland;

import com.terraforged.engine.settings.RiverSettings.Wetland;
import com.terraforged.engine.util.Variance;
import com.terraforged.noise.util.NoiseUtil;

public class WetlandConfig {
    public final int skipSize;
    public final Variance length;
    public final Variance width;

    public WetlandConfig(Wetland settings) {
        this.skipSize = Math.max(1, NoiseUtil.round((1.0F - settings.chance) * 10.0F));
        this.length = Variance.of((double)settings.sizeMin, (double)settings.sizeMax);
        this.width = Variance.of(50.0, 150.0);
    }
}
