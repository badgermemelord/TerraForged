//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise;

public interface Noise {
    float getValue(float var1, float var2);

    default float maxValue() {
        return 1.0F;
    }

    default float minValue() {
        return 0.0F;
    }
}
