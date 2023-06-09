// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.util.Noise;
import com.terraforged.noise.util.NoiseUtil;

public class CellEdgeNoise extends NoiseSource
{
    private final EdgeFunc edgeFunc;
    private final DistanceFunc distFunc;
    private final float distance;
    
    public CellEdgeNoise(final Builder builder) {
        super(builder);
        this.edgeFunc = builder.getEdgeFunc();
        this.distFunc = builder.getDistFunc();
        this.distance = builder.getDisplacement();
    }
    
    @Override
    public String getSpecName() {
        return "CellEdge";
    }
    
    @Override
    public float getValue(float x, float y, final int seed) {
        x *= this.frequency;
        y *= this.frequency;
        final float value = Noise.cellEdge(x, y, seed, this.distance, this.edgeFunc, this.distFunc);
        return NoiseUtil.map(value, this.edgeFunc.min(), this.edgeFunc.max(), this.edgeFunc.range());
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
        final CellEdgeNoise that = (CellEdgeNoise)o;
        return Float.compare(that.distance, this.distance) == 0 && this.edgeFunc == that.edgeFunc && this.distFunc == that.distFunc;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + this.edgeFunc.hashCode();
        result = 31 * result + this.distFunc.hashCode();
        result = 31 * result + ((this.distance != 0.0f) ? Float.floatToIntBits(this.distance) : 0);
        return result;
    }
    
    public static DataSpec<CellEdgeNoise> spec() {
        return NoiseSource.specBuilder("CellEdge", CellEdgeNoise.class, CellEdgeNoise::new).add("distance", (Object)1.0f, f -> f.distance).add("edge_func", (Object)Builder.DEFAULT_EDGE_FUNC, f -> f.edgeFunc).add("dist_func", (Object)Builder.DEFAULT_DIST_FUNC, f -> f.distFunc).build();
    }
}
