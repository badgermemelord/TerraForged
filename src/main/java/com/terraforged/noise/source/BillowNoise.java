//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;

public class BillowNoise extends RidgeNoise {
    public BillowNoise(Builder builder) {
        super(builder);
    }

    public String getSpecName() {
        return "Billow";
    }

    public float getValue(float x, float y, int seed) {
        return 1.0F - super.getValue(x, y, seed);
    }

    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public int hashCode() {
        return super.hashCode();
    }

    public static DataSpec<BillowNoise> billowSpec() {
        return specBuilder("Billow", BillowNoise.class, BillowNoise::new).build();
    }
}
