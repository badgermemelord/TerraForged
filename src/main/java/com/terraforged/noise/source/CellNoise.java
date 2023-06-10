//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.CellFunc;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.util.Noise;

public class CellNoise extends NoiseSource {
    private final Module lookup;
    private final CellFunc cellFunc;
    private final DistanceFunc distFunc;
    private final float min;
    private final float max;
    private final float range;
    private final float distance;

    public CellNoise(Builder builder) {
        super(builder);
        this.lookup = builder.getSource();
        this.cellFunc = builder.getCellFunc();
        this.distFunc = builder.getDistFunc();
        this.distance = builder.getDisplacement();
        this.min = min(this.cellFunc, this.lookup);
        this.max = max(this.cellFunc, this.lookup);
        this.range = this.max - this.min;
    }

    public String getSpecName() {
        return "Cell";
    }

    public float getValue(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float value = Noise.cell(x, y, seed, this.distance, this.cellFunc, this.distFunc, this.lookup);
        return this.cellFunc.mapValue(value, this.min, this.max, this.range);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || this.getClass() != o.getClass()) {
            return false;
        } else if (!super.equals(o)) {
            return false;
        } else {
            CellNoise cellNoise = (CellNoise)o;
            if (Float.compare(cellNoise.min, this.min) != 0) {
                return false;
            } else if (Float.compare(cellNoise.max, this.max) != 0) {
                return false;
            } else if (Float.compare(cellNoise.range, this.range) != 0) {
                return false;
            } else if (Float.compare(cellNoise.distance, this.distance) != 0) {
                return false;
            } else if (!this.lookup.equals(cellNoise.lookup)) {
                return false;
            } else if (this.cellFunc != cellNoise.cellFunc) {
                return false;
            } else {
                return this.distFunc == cellNoise.distFunc;
            }
        }
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.lookup.hashCode();
        result = 31 * result + this.cellFunc.hashCode();
        result = 31 * result + this.distFunc.hashCode();
        result = 31 * result + (this.min != 0.0F ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + (this.max != 0.0F ? Float.floatToIntBits(this.max) : 0);
        result = 31 * result + (this.range != 0.0F ? Float.floatToIntBits(this.range) : 0);
        return 31 * result + (this.distance != 0.0F ? Float.floatToIntBits(this.distance) : 0);
    }

    static float min(CellFunc func, Module lookup) {
        if (func == CellFunc.NOISE_LOOKUP) {
            return lookup.minValue();
        } else {
            return func == CellFunc.DISTANCE ? -1.0F : -1.0F;
        }
    }

    static float max(CellFunc func, Module lookup) {
        if (func == CellFunc.NOISE_LOOKUP) {
            return lookup.maxValue();
        } else {
            return func == CellFunc.DISTANCE ? 0.25F : 1.0F;
        }
    }

    public static DataSpec<CellNoise> spec() {
        return specBuilder("Cell", CellNoise.class, CellNoise::new)
                .add("distance", 1.0F, f -> f.distance)
                .add("cell_func", Builder.DEFAULT_CELL_FUNC, f -> f.cellFunc)
                .add("dist_func", Builder.DEFAULT_DIST_FUNC, f -> f.distFunc)
                .addObj("source", Module.class, f -> f.lookup)
                .build();
    }
}
