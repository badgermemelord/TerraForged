// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.Module;
import com.terraforged.noise.func.CellFunc;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.util.Noise;

public class CellNoise extends NoiseSource
{
    private final Module lookup;
    private final CellFunc cellFunc;
    private final DistanceFunc distFunc;
    private final float min;
    private final float max;
    private final float range;
    private final float distance;
    
    public CellNoise(final Builder builder) {
        super(builder);
        this.lookup = builder.getSource();
        this.cellFunc = builder.getCellFunc();
        this.distFunc = builder.getDistFunc();
        this.distance = builder.getDisplacement();
        this.min = min(this.cellFunc, this.lookup);
        this.max = max(this.cellFunc, this.lookup);
        this.range = this.max - this.min;
    }
    
    @Override
    public String getSpecName() {
        return "Cell";
    }
    
    @Override
    public float getValue(float x, float y, final int seed) {
        x *= this.frequency;
        y *= this.frequency;
        final float value = Noise.cell(x, y, seed, this.distance, this.cellFunc, this.distFunc, this.lookup);
        return this.cellFunc.mapValue(value, this.min, this.max, this.range);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        final CellNoise cellNoise = (CellNoise)o;
        return Float.compare(cellNoise.min, this.min) == 0 && Float.compare(cellNoise.max, this.max) == 0 && Float.compare(cellNoise.range, this.range) == 0 && Float.compare(cellNoise.distance, this.distance) == 0 && this.lookup.equals(cellNoise.lookup) && this.cellFunc == cellNoise.cellFunc && this.distFunc == cellNoise.distFunc;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.lookup.hashCode();
        result = 31 * result + this.cellFunc.hashCode();
        result = 31 * result + this.distFunc.hashCode();
        result = 31 * result + ((this.min != 0.0f) ? Float.floatToIntBits(this.min) : 0);
        result = 31 * result + ((this.max != 0.0f) ? Float.floatToIntBits(this.max) : 0);
        result = 31 * result + ((this.range != 0.0f) ? Float.floatToIntBits(this.range) : 0);
        result = 31 * result + ((this.distance != 0.0f) ? Float.floatToIntBits(this.distance) : 0);
        return result;
    }
    
    static float min(final CellFunc func, final Module lookup) {
        if (func == CellFunc.NOISE_LOOKUP) {
            return lookup.minValue();
        }
        if (func == CellFunc.DISTANCE) {
            return -1.0f;
        }
        return -1.0f;
    }
    
    static float max(final CellFunc func, final Module lookup) {
        if (func == CellFunc.NOISE_LOOKUP) {
            return lookup.maxValue();
        }
        if (func == CellFunc.DISTANCE) {
            return 0.25f;
        }
        return 1.0f;
    }
    
    public static DataSpec<CellNoise> spec() {
        return NoiseSource.specBuilder("Cell", CellNoise.class, CellNoise::new).add("distance", (Object)1.0f, f -> f.distance).add("cell_func", (Object)Builder.DEFAULT_CELL_FUNC, f -> f.cellFunc).add("dist_func", (Object)Builder.DEFAULT_DIST_FUNC, f -> f.distFunc).addObj("source", Module.class, f -> f.lookup).build();
    }
}
