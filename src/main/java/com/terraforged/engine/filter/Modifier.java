//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;
import com.terraforged.noise.util.NoiseUtil;

public interface Modifier {
    float getValueModifier(float var1);

    default float modify(Cell cell, float value) {
        float strengthModifier = 1.0F;
        float erosionModifier = cell.terrain.erosionModifier();
        if (erosionModifier != 1.0F) {
            float alpha = NoiseUtil.map(cell.terrainRegionEdge, 0.0F, 0.15F, 0.15F);
            strengthModifier = NoiseUtil.lerp(1.0F, erosionModifier, alpha);
        }

        if (cell.riverMask < 0.1F) {
            strengthModifier *= NoiseUtil.map(cell.riverMask, 0.002F, 0.1F, 0.098F);
        }

        return this.getValueModifier(cell.value) * strengthModifier * value;
    }

    default Modifier invert() {
        return v -> 1.0F - this.getValueModifier(v);
    }

    static Modifier range(final float minValue, final float maxValue) {
        return new Modifier() {
            private final float min = minValue;
            private final float max = maxValue;
            private final float range = maxValue - minValue;

            @Override
            public float getValueModifier(float value) {
                if (value > this.max) {
                    return 1.0F;
                } else {
                    return value < this.min ? 0.0F : (value - this.min) / this.range;
                }
            }
        };
    }
}
