// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.noise;

import com.terraforged.cereal.spec.SpecName;
import com.terraforged.noise.combiner.*;
import com.terraforged.noise.domain.Domain;
import com.terraforged.noise.func.CurveFunc;
import com.terraforged.noise.func.Interpolation;
import com.terraforged.noise.func.MidPointCurve;
import com.terraforged.noise.modifier.*;
import com.terraforged.noise.selector.*;
import com.terraforged.noise.source.NoiseSource;

public interface Module extends Noise, SpecName
{
    default String getSpecName() {
        return "";
    }
    
    default Module abs() {
        if (this instanceof Abs) {
            return this;
        }
        return new Abs(this);
    }
    
    default Module add(final Module other) {
        return new Add(new Module[] { this, other });
    }
    
    default Module alpha(final double alpha) {
        return this.alpha(Source.constant(alpha));
    }
    
    default Module alpha(final Module alpha) {
        if (alpha.minValue() < 0.0f || alpha.maxValue() > 1.0f) {
            return this;
        }
        return new Alpha(this, alpha);
    }
    
    default Module base(final Module other, final double falloff) {
        return this.base(other, falloff, Interpolation.CURVE3);
    }
    
    default Module base(final Module other, final double falloff, final Interpolation interpolation) {
        return new Base(this, other, (float)falloff, interpolation);
    }
    
    default Module bias(final Module bias) {
        if (bias.minValue() == 0.0f && bias.maxValue() == 0.0f) {
            return this;
        }
        return new Bias(this, bias);
    }
    
    default Module bias(final double bias) {
        return this.bias(Source.constant(bias));
    }
    
    default Module blend(final Module source0, final Module source1, final double midpoint, final double blendRange) {
        return this.blend(source0, source1, midpoint, blendRange, Interpolation.LINEAR);
    }
    
    default Module blend(final Module source0, final Module source1, final double midpoint, final double blendRange, final Interpolation interpolation) {
        return new Blend(this, source0, source1, (float)midpoint, (float)blendRange, interpolation);
    }
    
    default Module blendVar(final Module variable, final Module source0, final Module source1, final double midpoint, final double min, final double max, final Interpolation interpolation) {
        return new VariableBlend(this, variable, source0, source1, (float)midpoint, (float)min, (float)max, interpolation);
    }
    
    default Module boost() {
        return this.boost(1);
    }
    
    default Module boost(final int iterations) {
        if (iterations < 1) {
            return this;
        }
        return new Boost(this, iterations);
    }
    
    default Module cache() {
        if (this instanceof Cache) {
            return this;
        }
        return new Cache(this);
    }
    
    default Module clamp(final Module min, final Module max) {
        if (min.minValue() == min.maxValue() && min.minValue() == this.minValue() && max.minValue() == max.maxValue() && max.maxValue() == this.maxValue()) {
            return this;
        }
        return new Clamp(this, min, max);
    }
    
    default Module clamp(final double min, final double max) {
        return this.clamp(Source.constant(min), Source.constant(max));
    }
    
    default Module curve(final CurveFunc func) {
        return new Curve(this, func);
    }
    
    default Module curve(final double mid, final double steepness) {
        return new Curve(this, new MidPointCurve((float)mid, (float)steepness));
    }
    
    default Module freq(final double x, final double y) {
        return this.freq(Source.constant(x), Source.constant(y));
    }
    
    default Module freq(final Module x, final Module y) {
        return new Freq(this, x, y);
    }
    
    default Module curve(final Module mid, final Module steepness) {
        return new VariableCurve(this, mid, steepness);
    }
    
    default Module grad(final double lower, final double upper, final double strength) {
        return this.grad(Source.constant(lower), Source.constant(upper), Source.constant(strength));
    }
    
    default Module grad(final Module lower, final Module upper, final Module strength) {
        return new Grad(this, lower, upper, strength);
    }
    
    default Module invert() {
        return new Invert(this);
    }
    
    default Module map(final Module min, final Module max) {
        if (min.minValue() == min.maxValue() && min.minValue() == this.minValue() && max.minValue() == max.maxValue() && max.maxValue() == this.maxValue()) {
            return this;
        }
        return new Map(this, min, max);
    }
    
    default Module map(final double min, final double max) {
        return this.map(Source.constant(min), Source.constant(max));
    }
    
    default Module max(final Module other) {
        return new Max(new Module[] { this, other });
    }
    
    default Module min(final Module other) {
        return new Min(new Module[] { this, other });
    }
    
    default Module mod(final Module direction, final Module strength) {
        return new Modulate(this, direction, strength);
    }
    
    default Module mult(final Module other) {
        if (other.minValue() == 1.0f && other.maxValue() == 1.0f) {
            return this;
        }
        return new Multiply(new Module[] { this, other });
    }
    
    default Module blend(final double blend, final Module... sources) {
        return this.blend(blend, Interpolation.LINEAR, sources);
    }
    
    default Module blend(final double blend, final Interpolation interpolation, final Module... sources) {
        return new MultiBlend((float)blend, interpolation, this, sources);
    }
    
    default Module pow(final Module n) {
        if (n.minValue() == 0.0f && n.maxValue() == 0.0f) {
            return Source.ONE;
        }
        if (n.minValue() == 1.0f && n.maxValue() == 1.0f) {
            return this;
        }
        return new Power(this, n);
    }
    
    default Module pow(final double n) {
        return this.pow(Source.constant(n));
    }
    
    default Module powCurve(final double n) {
        return new PowerCurve(this, (float)n);
    }
    
    default Module scale(final Module scale) {
        if (scale.minValue() == 1.0f && scale.maxValue() == 1.0f) {
            return this;
        }
        return new Scale(this, scale);
    }
    
    default Module scale(final double scale) {
        return this.scale(Source.constant(scale));
    }
    
    default Module select(final Module lower, final Module upper, final double lowerBound, final double upperBound, final double falloff) {
        return this.select(lower, upper, lowerBound, upperBound, falloff, Interpolation.CURVE3);
    }
    
    default Module select(final Module lower, final Module upper, final double lowerBound, final double upperBound, final double falloff, final Interpolation interpolation) {
        return new Select(this, lower, upper, (float)lowerBound, (float)upperBound, (float)falloff, interpolation);
    }
    
    default Module steps(final int steps) {
        return this.steps(steps, 0.0, 0.0);
    }
    
    default Module steps(final int steps, final double slopeMin, final double slopeMax) {
        return this.steps(steps, slopeMin, slopeMax, Interpolation.LINEAR);
    }
    
    default Module steps(final int steps, final double slopeMin, final double slopeMax, final CurveFunc curveFunc) {
        return this.steps(Source.constant(steps), Source.constant(slopeMin), Source.constant(slopeMax), curveFunc);
    }
    
    default Module steps(final Module steps, final Module slopeMin, final Module slopeMax) {
        return this.steps(steps, slopeMin, slopeMax, Interpolation.LINEAR);
    }
    
    default Module steps(final Module steps, final Module slopeMin, final Module slopeMax, final CurveFunc curveFunc) {
        return new Steps(this, steps, slopeMin, slopeMax, curveFunc);
    }
    
    default Module sub(final Module other) {
        return new Sub(new Module[] { this, other });
    }
    
    default Module legacyTerrace(final double lowerCurve, final double upperCurve, final int steps, final double blendRange) {
        return this.legacyTerrace(Source.constant(lowerCurve), Source.constant(upperCurve), steps, blendRange);
    }
    
    default Module legacyTerrace(final Module lowerCurve, final Module upperCurve, final int steps, final double blendRange) {
        return new LegacyTerrace(this, lowerCurve, upperCurve, steps, (float)blendRange);
    }
    
    default Module terrace(final double lowerCurve, final double upperCurve, final double lowerHeight, final int steps, final double blendRange) {
        return this.terrace(Source.constant(lowerCurve), Source.constant(upperCurve), Source.constant(lowerHeight), steps, blendRange);
    }
    
    default Module terrace(final Module lowerCurve, final Module upperCurve, final Module lowerHeight, final int steps, final double blendRange) {
        return new Terrace(this, lowerCurve, upperCurve, lowerHeight, steps, (float)blendRange);
    }
    
    default Module terrace(final Module modulation, final double slope, final double blendMin, final double blendMax, final int steps) {
        return this.terrace(modulation, Source.ONE, slope, blendMin, blendMax, steps);
    }
    
    default Module terrace(final Module modulation, final double slope, final double blendMin, final double blendMax, final int steps, final int octaves) {
        return this.terrace(modulation, Source.ONE, Source.constant(slope), blendMin, blendMax, steps, octaves);
    }
    
    default Module terrace(final Module modulation, final Module mask, final double slope, final double blendMin, final double blendMax, final int steps) {
        return this.terrace(modulation, mask, Source.constant(slope), blendMin, blendMax, steps, 1);
    }
    
    default Module terrace(final Module modulation, final Module mask, final Module slope, final double blendMin, final double blendMax, final int steps) {
        return new AdvancedTerrace(this, modulation, mask, slope, (float)blendMin, (float)blendMax, steps, 1);
    }
    
    default Module terrace(final Module modulation, final Module mask, final Module slope, final double blendMin, final double blendMax, final int steps, final int octaves) {
        return new AdvancedTerrace(this, modulation, mask, slope, (float)blendMin, (float)blendMax, steps, octaves);
    }
    
    default Module threshold(final double threshold) {
        return new Threshold(this, Source.constant(threshold));
    }
    
    default Module threshold(final Module threshold) {
        return new Threshold(this, threshold);
    }
    
    default Module warp(final Domain domain) {
        return new Warp(this, domain);
    }
    
    default Module warp(final Module warpX, final Module warpZ, final double power) {
        return this.warp(warpX, warpZ, Source.constant(power));
    }
    
    default Module warp(final Module warpX, final Module warpZ, final Module power) {
        return this.warp(Domain.warp(warpX, warpZ, power));
    }
    
    default Module warp(final int seed, final int scale, final int octaves, final double power) {
        return this.warp(Source.PERLIN, seed, scale, octaves, power);
    }
    
    default Module warp(final Source source, final int seed, final int scale, final int octaves, final double power) {
        final Module x = Source.build(seed, scale, octaves).build(source);
        final Module y = Source.build(seed + 1, scale, octaves).build(source);
        final Module p = Source.constant(power);
        return this.warp(x, y, p);
    }
    
    default Module warp(final Class<? extends NoiseSource> source, final int seed, final int scale, final int octaves, final double power) {
        final Module x = Source.build(seed, scale, octaves).build(source);
        final Module y = Source.build(seed + 1, scale, octaves).build(source);
        final Module p = Source.constant(power);
        return this.warp(x, y, p);
    }
}
