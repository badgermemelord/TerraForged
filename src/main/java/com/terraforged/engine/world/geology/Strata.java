// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.geology;

import com.terraforged.engine.Seed;
import com.terraforged.engine.concurrent.Resource;
import com.terraforged.noise.Source;
import com.terraforged.noise.source.Builder;
import com.terraforged.noise.util.NoiseUtil;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Strata<T>
{
    private final Module heightMod;
    private final List<Stratum<T>> strata;
    
    private Strata(final Module heightMod, final List<Stratum<T>> strata) {
        this.strata = strata;
        this.heightMod = heightMod;
    }
    
    public <Context> boolean downwards(final int x, final int y, final int z, final Context context, final Stratum.Visitor<T, Context> visitor) {
        try (final Resource<DepthBuffer> buffer = DepthBuffer.get()) {
            this.initBuffer(buffer.get(), x, z);
            return this.downwards(x, y, z, buffer.get(), context, visitor);
        }
    }
    
    public <Context> boolean downwards(final int x, final int y, final int z, final DepthBuffer depthBuffer, final Context ctx, final Stratum.Visitor<T, Context> visitor) {
        this.initBuffer(depthBuffer, x, z);
        int py = y;
        T last = null;
        for (int i = 0; i < this.strata.size(); ++i) {
            final float depth = depthBuffer.getDepth(i);
            final int height = NoiseUtil.round(depth * y);
            final T value = last = this.strata.get(i).getValue();
            for (int dy = 0; dy < height; ++dy) {
                if (py <= y) {
                    final boolean cont = visitor.visit(py, value, ctx);
                    if (!cont) {
                        return false;
                    }
                }
                if (--py < 0) {
                    return false;
                }
            }
        }
        if (last != null) {
            while (py > 0) {
                visitor.visit(py, last, ctx);
                --py;
            }
        }
        return true;
    }
    
    private int getYOffset(final int x, final int z) {
        return (int)(64.0f * this.heightMod.getValue((float)x, (float)z));
    }
    
    private void initBuffer(final DepthBuffer buffer, final int x, final int z) {
        buffer.init(this.strata.size());
        for (int i = 0; i < this.strata.size(); ++i) {
            final float depth = this.strata.get(i).getDepth((float)x, (float)z);
            buffer.set(i, depth);
        }
    }
    
    public static <T> Builder<T> builder(final int seed, final com.terraforged.noise.source.Builder noise) {
        return new Builder<T>(seed, noise);
    }
    
    public static class Builder<T>
    {
        private final Seed seed;
        private final com.terraforged.noise.source.Builder noise;
        private final List<Stratum<T>> strata;
        
        public Builder(final int seed, final com.terraforged.noise.source.Builder noise) {
            this.strata = new LinkedList<Stratum<T>>();
            this.seed = new Seed(seed);
            this.noise = noise;
        }
        
        public Builder<T> add(final T material, final double depth) {
            final Module module = this.noise.seed(this.seed.next()).perlin().scale(depth);
            this.strata.add(Stratum.of(material, module));
            return this;
        }
        
        public Builder<T> add(final Source type, final T material, final double depth) {
            final Module module = this.noise.seed(this.seed.next()).build(type).scale(depth);
            this.strata.add(Stratum.of(material, module));
            return this;
        }
        
        public Strata<T> build() {
            final Module height = Source.cell(this.seed.next(), 100);
            return new Strata<T>(height, new ArrayList(this.strata), null);
        }
    }
}
