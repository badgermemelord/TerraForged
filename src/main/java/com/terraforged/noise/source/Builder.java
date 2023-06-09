// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise.source;

import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import com.terraforged.noise.func.CellFunc;
import com.terraforged.noise.func.DistanceFunc;
import com.terraforged.noise.func.EdgeFunc;
import com.terraforged.noise.func.Interpolation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Builder
{
    public static final int DEFAULT_SEED = 1337;
    public static final int DEFAULT_OCTAVES = 1;
    public static final float DEFAULT_GAIN = 0.5f;
    public static final float DEFAULT_RIDGE_GAIN = 0.975f;
    public static final float DEFAULT_LACUNARITY = 2.0f;
    public static final float DEFAULT_FREQUENCY = 1.0f;
    public static final float DEFAULT_DISTANCE = 1.0f;
    public static final CellFunc DEFAULT_CELL_FUNC;
    public static final EdgeFunc DEFAULT_EDGE_FUNC;
    public static final DistanceFunc DEFAULT_DIST_FUNC;
    public static Interpolation DEFAULT_INTERPOLATION;
    private int seed;
    private int octaves;
    private float gain;
    private float lacunarity;
    private float frequency;
    private float displacement;
    private Module source;
    private CellFunc cellFunc;
    private EdgeFunc edgeFunc;
    private DistanceFunc distFunc;
    private Interpolation interpolation;
    
    public Builder() {
        this.seed = 1337;
        this.octaves = 1;
        this.gain = Float.MAX_VALUE;
        this.lacunarity = 2.0f;
        this.frequency = 1.0f;
        this.displacement = 1.0f;
        this.source = Source.ZERO;
        this.cellFunc = Builder.DEFAULT_CELL_FUNC;
        this.edgeFunc = Builder.DEFAULT_EDGE_FUNC;
        this.distFunc = Builder.DEFAULT_DIST_FUNC;
        this.interpolation = Builder.DEFAULT_INTERPOLATION;
    }
    
    public int getSeed() {
        return this.seed;
    }
    
    public int getOctaves() {
        return this.octaves;
    }
    
    public float getGain() {
        if (this.gain == Float.MAX_VALUE) {
            this.gain = 0.5f;
        }
        return this.gain;
    }
    
    public float getFrequency() {
        return this.frequency;
    }
    
    public float getDisplacement() {
        return this.displacement;
    }
    
    public float getLacunarity() {
        return this.lacunarity;
    }
    
    public Interpolation getInterp() {
        return this.interpolation;
    }
    
    public CellFunc getCellFunc() {
        return this.cellFunc;
    }
    
    public EdgeFunc getEdgeFunc() {
        return this.edgeFunc;
    }
    
    public DistanceFunc getDistFunc() {
        return this.distFunc;
    }
    
    public Module getSource() {
        return this.source;
    }
    
    public Builder seed(final int seed) {
        this.seed = seed;
        return this;
    }
    
    public Builder octaves(final int octaves) {
        this.octaves = octaves;
        return this;
    }
    
    public Builder gain(final double gain) {
        this.gain = (float)gain;
        return this;
    }
    
    public Builder lacunarity(final double lacunarity) {
        this.lacunarity = (float)lacunarity;
        return this;
    }
    
    public Builder scale(final int frequency) {
        this.frequency = 1.0f / frequency;
        return this;
    }
    
    public Builder frequency(final double frequency) {
        this.frequency = (float)frequency;
        return this;
    }
    
    public Builder displacement(final double displacement) {
        this.displacement = (float)displacement;
        return this;
    }
    
    public Builder interp(final Interpolation interpolation) {
        this.interpolation = interpolation;
        return this;
    }
    
    public Builder cellFunc(final CellFunc cellFunc) {
        this.cellFunc = cellFunc;
        return this;
    }
    
    public Builder edgeFunc(final EdgeFunc cellType) {
        this.edgeFunc = cellType;
        return this;
    }
    
    public Builder distFunc(final DistanceFunc cellDistance) {
        this.distFunc = cellDistance;
        return this;
    }
    
    public Builder source(final Module source) {
        this.source = source;
        return this;
    }
    
    public NoiseSource perlin() {
        return new PerlinNoise(this);
    }
    
    public NoiseSource perlin2() {
        return new PerlinNoise2(this);
    }
    
    public NoiseSource simplex() {
        return new SimplexNoise(this);
    }
    
    public NoiseSource simplex2() {
        return new SimplexNoise2(this);
    }
    
    public NoiseSource ridge() {
        if (this.gain == Float.MAX_VALUE) {
            this.gain = 0.975f;
        }
        return new RidgeNoise(this);
    }
    
    public NoiseSource simplexRidge() {
        if (this.gain == Float.MAX_VALUE) {
            this.gain = 0.975f;
        }
        return new SimplexRidgeNoise(this);
    }
    
    public NoiseSource billow() {
        return new BillowNoise(this);
    }
    
    public NoiseSource cubic() {
        return new CubicNoise(this);
    }
    
    public NoiseSource cell() {
        return new CellNoise(this);
    }
    
    public NoiseSource cellEdge() {
        return new CellEdgeNoise(this);
    }
    
    public NoiseSource sin() {
        return new Sin(this);
    }
    
    public Module constant() {
        return new Constant(this);
    }
    
    public Rand rand() {
        return new Rand(this);
    }
    
    public Module build(final Source source) {
        return source.build(this);
    }
    
    public Module build(final Class<? extends Module> type) {
        try {
            final Constructor<? extends Module> constructor = type.getConstructor(Builder.class);
            return (Module)constructor.newInstance(this);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | InstantiationException ex2) {
            final ReflectiveOperationException ex;
            final ReflectiveOperationException e = ex;
            e.printStackTrace();
            return this.perlin();
        }
    }
    
    static {
        DEFAULT_CELL_FUNC = CellFunc.CELL_VALUE;
        DEFAULT_EDGE_FUNC = EdgeFunc.DISTANCE_2;
        DEFAULT_DIST_FUNC = DistanceFunc.EUCLIDEAN;
        Builder.DEFAULT_INTERPOLATION = Interpolation.CURVE3;
    }
}
