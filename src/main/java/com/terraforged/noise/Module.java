// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise;

import com.terraforged.cereal.spec.SpecName;
import com.terraforged.noise.combiner.Add;
import com.terraforged.noise.combiner.Max;
import com.terraforged.noise.combiner.Min;
import com.terraforged.noise.combiner.Multiply;
import com.terraforged.noise.combiner.Sub;
import com.terraforged.noise.domain.Domain;
import com.terraforged.noise.func.CurveFunc;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.func.MidPointCurve;
import com.terraforged.noise.modifier.Abs;
import com.terraforged.noise.modifier.AdvancedTerrace;
import com.terraforged.noise.modifier.Alpha;
import com.terraforged.noise.modifier.Bias;
import com.terraforged.noise.modifier.Boost;
import com.terraforged.noise.modifier.Cache;
import com.terraforged.noise.modifier.Clamp;
import com.terraforged.noise.modifier.Curve;
import com.terraforged.noise.modifier.Freq;
import com.terraforged.noise.modifier.Grad;
import com.terraforged.noise.modifier.Invert;
import com.terraforged.noise.modifier.LegacyTerrace;
import com.terraforged.noise.modifier.Map;
import com.terraforged.noise.modifier.Modulate;
import com.terraforged.noise.modifier.Power;
import com.terraforged.noise.modifier.PowerCurve;
import com.terraforged.noise.modifier.Scale;
import com.terraforged.noise.modifier.Steps;
import com.terraforged.noise.modifier.Terrace;
import com.terraforged.noise.modifier.Threshold;
import com.terraforged.noise.modifier.VariableCurve;
import com.terraforged.noise.modifier.Warp;
import com.terraforged.noise.selector.Base;
import com.terraforged.noise.selector.Blend;
import com.terraforged.noise.selector.MultiBlend;
import com.terraforged.noise.selector.Select;
import com.terraforged.noise.selector.VariableBlend;
import com.terraforged.noise.source.NoiseSource;

public interface Module extends Noise, SpecName {
    default String getSpecName() {
        return "";
    }

    default Module abs() {
        return (Module)(this instanceof Abs ? this : new Abs(this));
    }

    default Module add(Module other) {
        return new Add(new Module[]{this, other});
    }

    default Module alpha(double alpha) {
        return this.alpha(Source.constant(alpha));
    }

    default Module alpha(Module alpha) {
        return (Module)(!(alpha.minValue() < 0.0F) && !(alpha.maxValue() > 1.0F) ? new Alpha(this, alpha) : this);
    }

    default Module base(Module other, double falloff) {
        return this.base(other, falloff, Interpolation.CURVE3);
    }

    default Module base(Module other, double falloff, Interpolation interpolation) {
        return new Base(this, other, (float)falloff, interpolation);
    }

    default Module bias(Module bias) {
        return (Module)(bias.minValue() == 0.0F && bias.maxValue() == 0.0F ? this : new Bias(this, bias));
    }

    default Module bias(double bias) {
        return this.bias(Source.constant(bias));
    }

    default Module blend(Module source0, Module source1, double midpoint, double blendRange) {
        return this.blend(source0, source1, midpoint, blendRange, Interpolation.LINEAR);
    }

    default Module blend(Module source0, Module source1, double midpoint, double blendRange, Interpolation interpolation) {
        return new Blend(this, source0, source1, (float)midpoint, (float)blendRange, interpolation);
    }

    default Module blendVar(Module variable, Module source0, Module source1, double midpoint, double min, double max, Interpolation interpolation) {
        return new VariableBlend(this, variable, source0, source1, (float)midpoint, (float)min, (float)max, interpolation);
    }

    default Module boost() {
        return this.boost(1);
    }

    default Module boost(int iterations) {
        return (Module)(iterations < 1 ? this : new Boost(this, iterations));
    }

    default Module cache() {
        return (Module)(this instanceof Cache ? this : new Cache(this));
    }

    default Module clamp(Module min, Module max) {
        return (Module)(min.minValue() == min.maxValue() && min.minValue() == this.minValue() && max.minValue() == max.maxValue() && max.maxValue() == this.maxValue() ? this : new Clamp(this, min, max));
    }

    default Module clamp(double min, double max) {
        return this.clamp(Source.constant(min), Source.constant(max));
    }

    default Module curve(CurveFunc func) {
        return new Curve(this, func);
    }

    default Module curve(double mid, double steepness) {
        return new Curve(this, new MidPointCurve((float)mid, (float)steepness));
    }

    default Module freq(double x, double y) {
        return this.freq(Source.constant(x), Source.constant(y));
    }

    default Module freq(Module x, Module y) {
        return new Freq(this, x, y);
    }

    default Module curve(Module mid, Module steepness) {
        return new VariableCurve(this, mid, steepness);
    }

    default Module grad(double lower, double upper, double strength) {
        return this.grad(Source.constant(lower), Source.constant(upper), Source.constant(strength));
    }

    default Module grad(Module lower, Module upper, Module strength) {
        return new Grad(this, lower, upper, strength);
    }

    default Module invert() {
        return new Invert(this);
    }

    default Module map(Module min, Module max) {
        return (Module)(min.minValue() == min.maxValue() && min.minValue() == this.minValue() && max.minValue() == max.maxValue() && max.maxValue() == this.maxValue() ? this : new Map(this, min, max));
    }

    default Module map(double min, double max) {
        return this.map(Source.constant(min), Source.constant(max));
    }

    default Module max(Module other) {
        return new Max(new Module[]{this, other});
    }

    default Module min(Module other) {
        return new Min(new Module[]{this, other});
    }

    default Module mod(Module direction, Module strength) {
        return new Modulate(this, direction, strength);
    }

    default Module mult(Module other) {
        return (Module)(other.minValue() == 1.0F && other.maxValue() == 1.0F ? this : new Multiply(new Module[]{this, other}));
    }

    default Module blend(double blend, Module... sources) {
        return this.blend(blend, Interpolation.LINEAR, sources);
    }

    default Module blend(double blend, Interpolation interpolation, Module... sources) {
        return new MultiBlend((float)blend, interpolation, this, sources);
    }

    default Module pow(Module n) {
        if (n.minValue() == 0.0F && n.maxValue() == 0.0F) {
            return Source.ONE;
        } else {
            return (Module)(n.minValue() == 1.0F && n.maxValue() == 1.0F ? this : new Power(this, n));
        }
    }

    default Module pow(double n) {
        return this.pow(Source.constant(n));
    }

    default Module powCurve(double n) {
        return new PowerCurve(this, (float)n);
    }

    default Module scale(Module scale) {
        return (Module)(scale.minValue() == 1.0F && scale.maxValue() == 1.0F ? this : new Scale(this, scale));
    }

    default Module scale(double scale) {
        return this.scale(Source.constant(scale));
    }

    default Module select(Module lower, Module upper, double lowerBound, double upperBound, double falloff) {
        return this.select(lower, upper, lowerBound, upperBound, falloff, Interpolation.CURVE3);
    }

    default Module select(Module lower, Module upper, double lowerBound, double upperBound, double falloff, Interpolation interpolation) {
        return new Select(this, lower, upper, (float)lowerBound, (float)upperBound, (float)falloff, interpolation);
    }

    default Module steps(int steps) {
        return this.steps(steps, 0.0, 0.0);
    }

    default Module steps(int steps, double slopeMin, double slopeMax) {
        return this.steps(steps, slopeMin, slopeMax, Interpolation.LINEAR);
    }

    default Module steps(int steps, double slopeMin, double slopeMax, CurveFunc curveFunc) {
        return this.steps(Source.constant((double)steps), Source.constant(slopeMin), Source.constant(slopeMax), curveFunc);
    }

    default Module steps(Module steps, Module slopeMin, Module slopeMax) {
        return this.steps(steps, slopeMin, slopeMax, Interpolation.LINEAR);
    }

    default Module steps(Module steps, Module slopeMin, Module slopeMax, CurveFunc curveFunc) {
        return new Steps(this, steps, slopeMin, slopeMax, curveFunc);
    }

    default Module sub(Module other) {
        return new Sub(new Module[]{this, other});
    }

    default Module legacyTerrace(double lowerCurve, double upperCurve, int steps, double blendRange) {
        return this.legacyTerrace(Source.constant(lowerCurve), Source.constant(upperCurve), steps, blendRange);
    }

    default Module legacyTerrace(Module lowerCurve, Module upperCurve, int steps, double blendRange) {
        return new LegacyTerrace(this, lowerCurve, upperCurve, steps, (float)blendRange);
    }

    default Module terrace(double lowerCurve, double upperCurve, double lowerHeight, int steps, double blendRange) {
        return this.terrace(Source.constant(lowerCurve), Source.constant(upperCurve), Source.constant(lowerHeight), steps, blendRange);
    }

    default Module terrace(Module lowerCurve, Module upperCurve, Module lowerHeight, int steps, double blendRange) {
        return new Terrace(this, lowerCurve, upperCurve, lowerHeight, steps, (float)blendRange);
    }

    default Module terrace(Module modulation, double slope, double blendMin, double blendMax, int steps) {
        return this.terrace(modulation, Source.ONE, slope, blendMin, blendMax, steps);
    }

    default Module terrace(Module modulation, double slope, double blendMin, double blendMax, int steps, int octaves) {
        return this.terrace(modulation, Source.ONE, Source.constant(slope), blendMin, blendMax, steps, octaves);
    }

    default Module terrace(Module modulation, Module mask, double slope, double blendMin, double blendMax, int steps) {
        return this.terrace(modulation, mask, Source.constant(slope), blendMin, blendMax, steps, 1);
    }

    default Module terrace(Module modulation, Module mask, Module slope, double blendMin, double blendMax, int steps) {
        return new AdvancedTerrace(this, modulation, mask, slope, (float)blendMin, (float)blendMax, steps, 1);
    }

    default Module terrace(Module modulation, Module mask, Module slope, double blendMin, double blendMax, int steps, int octaves) {
        return new AdvancedTerrace(this, modulation, mask, slope, (float)blendMin, (float)blendMax, steps, octaves);
    }

    default Module threshold(double threshold) {
        return new Threshold(this, Source.constant(threshold));
    }

    default Module threshold(Module threshold) {
        return new Threshold(this, threshold);
    }

    default Module warp(Domain domain) {
        return new Warp(this, domain);
    }

    default Module warp(Module warpX, Module warpZ, double power) {
        return this.warp(warpX, warpZ, Source.constant(power));
    }

    default Module warp(Module warpX, Module warpZ, Module power) {
        return this.warp(Domain.warp(warpX, warpZ, power));
    }

    default Module warp(int seed, int scale, int octaves, double power) {
        return this.warp(Source.PERLIN, seed, scale, octaves, power);
    }

    default Module warp(Source source, int seed, int scale, int octaves, double power) {
        Module x = Source.build(seed, scale, octaves).build(source);
        Module y = Source.build(seed + 1, scale, octaves).build(source);
        Module p = Source.constant(power);
        return this.warp(x, y, p);
    }

    default Module warp(Class<? extends NoiseSource> source, int seed, int scale, int octaves, double power) {
        Module x = Source.build(seed, scale, octaves).build(source);
        Module y = Source.build(seed + 1, scale, octaves).build(source);
        Module p = Source.constant(power);
        return this.warp(x, y, p);
    }
}
