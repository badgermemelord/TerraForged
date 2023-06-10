//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

public class CellEdgeNoise extends NoiseSource {
    private final EdgeFunc edgeFunc;
    private final DistanceFunc distFunc;
    private final float distance;

    public CellEdgeNoise(Builder builder) {
        super(builder);
        this.edgeFunc = builder.getEdgeFunc();
        this.distFunc = builder.getDistFunc();
        this.distance = builder.getDisplacement();
    }

    public String getSpecName() {
        return "CellEdge";
    }

    public float getValue(float x, float y, int seed) {
        x *= this.frequency;
        y *= this.frequency;
        float value = Noise.cellEdge(x, y, seed, this.distance, this.edgeFunc, this.distFunc);
        return NoiseUtil.map(value, this.edgeFunc.min(), this.edgeFunc.max(), this.edgeFunc.range());
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o == null || this.getClass() != o.getClass()) {
            return false;
        } else if (!super.equals(o)) {
            return false;
        } else {
            CellEdgeNoise that = (CellEdgeNoise)o;
            if (Float.compare(that.distance, this.distance) != 0) {
                return false;
            } else if (this.edgeFunc != that.edgeFunc) {
                return false;
            } else {
                return this.distFunc == that.distFunc;
            }
        }
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.edgeFunc.hashCode();
        result = 31 * result + this.distFunc.hashCode();
        return 31 * result + (this.distance != 0.0F ? Float.floatToIntBits(this.distance) : 0);
    }

    public static DataSpec<CellEdgeNoise> spec() {
        return specBuilder("CellEdge", CellEdgeNoise.class, CellEdgeNoise::new)
                .add("distance", 1.0F, f -> f.distance)
                .add("edge_func", Builder.DEFAULT_EDGE_FUNC, f -> f.edgeFunc)
                .add("dist_func", Builder.DEFAULT_DIST_FUNC, f -> f.distFunc)
                .build();
    }
}
