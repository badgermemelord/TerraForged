//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
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

public class Builder {
    public static final int DEFAULT_SEED = 1337;
    public static final int DEFAULT_OCTAVES = 1;
    public static final float DEFAULT_GAIN = 0.5F;
    public static final float DEFAULT_RIDGE_GAIN = 0.975F;
    public static final float DEFAULT_LACUNARITY = 2.0F;
    public static final float DEFAULT_FREQUENCY = 1.0F;
    public static final float DEFAULT_DISTANCE = 1.0F;
    public static final CellFunc DEFAULT_CELL_FUNC;
    public static final EdgeFunc DEFAULT_EDGE_FUNC;
    public static final DistanceFunc DEFAULT_DIST_FUNC;
    public static Interpolation DEFAULT_INTERPOLATION;
    private int seed = 1337;
    private int octaves = 1;
    private float gain = Float.MAX_VALUE;
    private float lacunarity = 2.0F;
    private float frequency = 1.0F;
    private float displacement = 1.0F;
    private Module source;
    private CellFunc cellFunc;
    private EdgeFunc edgeFunc;
    private DistanceFunc distFunc;
    private Interpolation interpolation;

    public Builder() {
        this.source = Source.ZERO;
        this.cellFunc = DEFAULT_CELL_FUNC;
        this.edgeFunc = DEFAULT_EDGE_FUNC;
        this.distFunc = DEFAULT_DIST_FUNC;
        this.interpolation = DEFAULT_INTERPOLATION;
    }

    public int getSeed() {
        return this.seed;
    }

    public int getOctaves() {
        return this.octaves;
    }

    public float getGain() {
        if (this.gain == Float.MAX_VALUE) {
            this.gain = 0.5F;
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

    public Builder seed(int seed) {
        this.seed = seed;
        return this;
    }

    public Builder octaves(int octaves) {
        this.octaves = octaves;
        return this;
    }

    public Builder gain(double gain) {
        this.gain = (float)gain;
        return this;
    }

    public Builder lacunarity(double lacunarity) {
        this.lacunarity = (float)lacunarity;
        return this;
    }

    public Builder scale(int frequency) {
        this.frequency = 1.0F / (float)frequency;
        return this;
    }

    public Builder frequency(double frequency) {
        this.frequency = (float)frequency;
        return this;
    }

    public Builder displacement(double displacement) {
        this.displacement = (float)displacement;
        return this;
    }

    public Builder interp(Interpolation interpolation) {
        this.interpolation = interpolation;
        return this;
    }

    public Builder cellFunc(CellFunc cellFunc) {
        this.cellFunc = cellFunc;
        return this;
    }

    public Builder edgeFunc(EdgeFunc cellType) {
        this.edgeFunc = cellType;
        return this;
    }

    public Builder distFunc(DistanceFunc cellDistance) {
        this.distFunc = cellDistance;
        return this;
    }

    public Builder source(Module source) {
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
            this.gain = 0.975F;
        }

        return new RidgeNoise(this);
    }

    public NoiseSource simplexRidge() {
        if (this.gain == Float.MAX_VALUE) {
            this.gain = 0.975F;
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

    public Module build(Source source) {
        return source.build(this);
    }

    public Module build(Class<? extends Module> type) {
        try {
            Constructor<? extends Module> constructor = type.getConstructor(Builder.class);
            return (Module)constructor.newInstance(this);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException | NoSuchMethodException var3) {
            var3.printStackTrace();
            return this.perlin();
        }
    }

    static {
        DEFAULT_CELL_FUNC = CellFunc.CELL_VALUE;
        DEFAULT_EDGE_FUNC = EdgeFunc.DISTANCE_2;
        DEFAULT_DIST_FUNC = DistanceFunc.EUCLIDEAN;
        DEFAULT_INTERPOLATION = Interpolation.CURVE3;
    }
}
