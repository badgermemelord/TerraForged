// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise;

import com.terraforged.noise.func.CellFunc;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.source.Builder;
import com.terraforged.noise.source.Constant;
import com.terraforged.noise.source.Line;
import com.terraforged.noise.source.Rand;
import com.terraforged.noise.util.NoiseSpec;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public enum Source
{
    BILLOW((Function<Builder, Module>)Builder::billow), 
    CELL((Function<Builder, Module>)Builder::cell), 
    CELL_EDGE((Function<Builder, Module>)Builder::cellEdge), 
    CONST(Builder::constant), 
    CUBIC((Function<Builder, Module>)Builder::cubic), 
    PERLIN((Function<Builder, Module>)Builder::perlin), 
    PERLIN2((Function<Builder, Module>)Builder::perlin2), 
    RIDGE((Function<Builder, Module>)Builder::ridge), 
    SIMPLEX((Function<Builder, Module>)Builder::simplex), 
    SIMPLEX2((Function<Builder, Module>)Builder::simplex2), 
    SIMPLEX_RIDGE((Function<Builder, Module>)Builder::simplex2), 
    SIN((Function<Builder, Module>)Builder::sin), 
    RAND((Function<Builder, Module>)Builder::rand);
    
    public static final Module ONE;
    public static final Module ZERO;
    public static final Module HALF;
    private final Function<Builder, Module> fn;
    
    private Source(final Function<Builder, Module> fn) {
        this.fn = fn;
    }
    
    public Module build(final Builder builder) {
        return this.fn.apply(builder);
    }
    
    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder build(final int scale, final int octaves) {
        return build(ThreadLocalRandom.current().nextInt(), scale, octaves);
    }
    
    public static Builder build(final int seed, final int scale, final int octaves) {
        return builder().seed(seed).scale(scale).octaves(octaves);
    }
    
    public static Module perlin(final int scale, final int octaves) {
        return perlin(ThreadLocalRandom.current().nextInt(), scale, octaves);
    }
    
    public static Module perlin(final int seed, final double freq, final int octaves) {
        return builder().seed(seed).frequency(freq).octaves(octaves).perlin();
    }
    
    public static Module perlin(final int seed, final int scale, final int octaves) {
        return builder().seed(seed).scale(scale).octaves(octaves).perlin();
    }
    
    public static Module simplex(final int scale, final int octaves) {
        return simplex(ThreadLocalRandom.current().nextInt(), scale, octaves);
    }
    
    public static Module simplex(final int seed, final int scale, final int octaves) {
        return builder().seed(seed).scale(scale).octaves(octaves).simplex();
    }
    
    public static Module billow(final int scale, final int octaves) {
        return billow(ThreadLocalRandom.current().nextInt(), scale, octaves);
    }
    
    public static Module billow(final int seed, final int scale, final int octaves) {
        return builder().seed(seed).scale(scale).octaves(octaves).billow();
    }
    
    public static Module ridge(final int scale, final int octaves) {
        return ridge(ThreadLocalRandom.current().nextInt(), scale, octaves);
    }
    
    public static Module ridge(final int seed, final int scale, final int octaves) {
        return builder().seed(seed).scale(scale).octaves(octaves).ridge();
    }
    
    public static Module simplexRidge(final int scale, final int octaves) {
        return ridge(ThreadLocalRandom.current().nextInt(), scale, octaves);
    }
    
    public static Module simplexRidge(final int seed, final int scale, final int octaves) {
        return builder().seed(seed).scale(scale).octaves(octaves).simplexRidge();
    }
    
    public static Module cubic(final int scale, final int octaves) {
        return cubic(ThreadLocalRandom.current().nextInt(), scale, octaves);
    }
    
    public static Module cubic(final int seed, final int scale, final int octaves) {
        return builder().seed(seed).scale(scale).octaves(octaves).cubic();
    }
    
    public static Module cell(final int scale) {
        return cell(ThreadLocalRandom.current().nextInt(), scale);
    }
    
    public static Module cell(final int scale, final CellFunc cellFunc) {
        return cell(ThreadLocalRandom.current().nextInt(), scale, cellFunc);
    }
    
    public static Module cell(final int seed, final int scale) {
        return cell(seed, scale, CellFunc.CELL_VALUE);
    }
    
    public static Module cell(final int seed, final int scale, final DistanceFunc distFunc) {
        return builder().seed(seed).scale(scale).distFunc(distFunc).cell();
    }
    
    public static Module cell(final int seed, final int scale, final CellFunc cellFunc) {
        return builder().seed(seed).scale(scale).cellFunc(cellFunc).cell();
    }
    
    public static Module cell(final int seed, final int scale, final DistanceFunc distFunc, final CellFunc cellFunc) {
        return builder().seed(seed).scale(scale).distFunc(distFunc).cellFunc(cellFunc).cell();
    }
    
    public static Module cellNoise(final int scale, final Module source) {
        return cellNoise(ThreadLocalRandom.current().nextInt(), scale, source);
    }
    
    public static Module cellNoise(final int seed, final int scale, final Module source) {
        return builder().seed(seed).scale(scale).cellFunc(CellFunc.NOISE_LOOKUP).source(source).cell();
    }
    
    public static Module cellNoise(final int seed, final int scale, final DistanceFunc distFunc, final Module source) {
        return builder().seed(seed).scale(scale).cellFunc(CellFunc.NOISE_LOOKUP).distFunc(distFunc).source(source).cell();
    }
    
    public static Module cellEdge(final int scale) {
        return cellEdge(ThreadLocalRandom.current().nextInt(), scale);
    }
    
    public static Module cellEdge(final int scale, final EdgeFunc func) {
        return cellEdge(ThreadLocalRandom.current().nextInt(), scale, func);
    }
    
    public static Module cellEdge(final int seed, final int scale) {
        return builder().seed(seed).scale(scale).cellEdge();
    }
    
    public static Module cellEdge(final int seed, final int scale, final EdgeFunc func) {
        return builder().seed(seed).scale(scale).edgeFunc(func).cellEdge();
    }
    
    public static Module cellEdge(final int seed, final int scale, final DistanceFunc distFunc, final EdgeFunc edgeFunc) {
        return builder().seed(seed).scale(scale).distFunc(distFunc).edgeFunc(edgeFunc).cellEdge();
    }
    
    public static Rand rand(final int scale) {
        return rand(ThreadLocalRandom.current().nextInt(), scale);
    }
    
    public static Rand rand(final int seed, final int scale) {
        return build(seed, scale, 0).rand();
    }
    
    public static Module sin(final int scale, final Module source) {
        return builder().scale(scale).source(source).sin();
    }
    
    public static Line line(final double x1, final double y1, final double x2, final double y2, final double radius, final double fadeIn, final double fadeOut) {
        return line(x1, y1, x2, y2, constant(radius * radius), constant(fadeIn), constant(fadeOut));
    }
    
    public static Line line(final double x1, final double y1, final double x2, final double y2, final double radius, final double fadeIn, final double fadeOut, final double feather) {
        return line(x1, y1, x2, y2, constant(radius * radius), constant(fadeIn), constant(fadeOut), feather);
    }
    
    public static Line line(final double x1, final double y1, final double x2, final double y2, final Module radius2, final Module fadeIn, final Module fadeOut) {
        return line(x1, y1, x2, y2, radius2, fadeIn, fadeOut, 0.33);
    }
    
    public static Line line(final double x1, final double y1, final double x2, final double y2, final Module radius2, final Module fadeIn, final Module fadeOut, final double feather) {
        return new Line((float)x1, (float)y1, (float)x2, (float)y2, radius2, fadeIn, fadeOut, (float)feather);
    }
    
    public static Module constant(final double value) {
        if (value == 0.0) {
            return Source.ZERO;
        }
        if (value == 0.5) {
            return Source.HALF;
        }
        if (value == 1.0) {
            return Source.ONE;
        }
        return new Constant((float)value);
    }
    
    static {
        NoiseSpec.init();
        ONE = new Constant(1.0f);
        ZERO = new Constant(0.0f);
        HALF = new Constant(0.5f);
    }
}
