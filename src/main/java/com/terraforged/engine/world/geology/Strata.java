//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.geology;

import com.terraforged.engine.Seed;
import com.terraforged.engine.concurrent.Resource;
import com.terraforged.engine.world.geology.Stratum.Visitor;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Strata<T> {
    private final Module heightMod;
    private final List<Stratum<T>> strata;

    private Strata(Module heightMod, List<Stratum<T>> strata) {
        this.strata = strata;
        this.heightMod = heightMod;
    }

    public <Context> boolean downwards(int x, int y, int z, Context context, Visitor<T, Context> visitor) {
        Resource<DepthBuffer> buffer = DepthBuffer.get();
        Throwable var7 = null;

        boolean var8;
        try {
            this.initBuffer((DepthBuffer)buffer.get(), x, z);
            var8 = this.downwards(x, y, z, (DepthBuffer)buffer.get(), context, visitor);
        } catch (Throwable var17) {
            var7 = var17;
            throw var17;
        } finally {
            if (buffer != null) {
                if (var7 != null) {
                    try {
                        buffer.close();
                    } catch (Throwable var16) {
                        var7.addSuppressed(var16);
                    }
                } else {
                    buffer.close();
                }
            }
        }

        return var8;
    }

    public <Context> boolean downwards(int x, int y, int z, DepthBuffer depthBuffer, Context ctx, Visitor<T, Context> visitor) {
        this.initBuffer(depthBuffer, x, z);
        int py = y;
        T last = null;

        for(int i = 0; i < this.strata.size(); ++i) {
            float depth = depthBuffer.getDepth(i);
            int height = NoiseUtil.round(depth * (float)y);
            T value = (T)((Stratum)this.strata.get(i)).getValue();
            last = value;

            for(int dy = 0; dy < height; ++dy) {
                if (py <= y) {
                    boolean cont = visitor.visit(py, value, ctx);
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
            while(py > 0) {
                visitor.visit(py, last, ctx);
                --py;
            }
        }

        return true;
    }

    private int getYOffset(int x, int z) {
        return (int)(64.0F * this.heightMod.getValue((float)x, (float)z));
    }

    private void initBuffer(DepthBuffer buffer, int x, int z) {
        buffer.init(this.strata.size());

        for(int i = 0; i < this.strata.size(); ++i) {
            float depth = ((Stratum)this.strata.get(i)).getDepth((float)x, (float)z);
            buffer.set(i, depth);
        }
    }

    public static <T> Strata.Builder<T> builder(int seed, com.terraforged.noise.source.Builder noise) {
        return new Strata.Builder<>(seed, noise);
    }

    public static class Builder<T> {
        private final Seed seed;
        private final com.terraforged.noise.source.Builder noise;
        private final List<Stratum<T>> strata = new LinkedList();

        public Builder(int seed, com.terraforged.noise.source.Builder noise) {
            this.seed = new Seed(seed);
            this.noise = noise;
        }

        public Strata.Builder<T> add(T material, double depth) {
            Module module = this.noise.seed(this.seed.next()).perlin().scale(depth);
            this.strata.add(Stratum.of(material, module));
            return this;
        }

        public Strata.Builder<T> add(Source type, T material, double depth) {
            Module module = this.noise.seed(this.seed.next()).build(type).scale(depth);
            this.strata.add(Stratum.of(material, module));
            return this;
        }

        public Strata<T> build() {
            Module height = Source.cell(this.seed.next(), 100);
            return new Strata<>(height, new ArrayList(this.strata));
        }
    }
}
