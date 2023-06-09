// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.continent;

import com.terraforged.engine.Seed;
import com.terraforged.engine.serialization.annotation.Unstable;
import com.terraforged.engine.world.GeneratorContext;
import com.terraforged.engine.world.continent.advanced.AdvancedContinentGenerator;
import com.terraforged.engine.world.continent.fancy.FancyContinentGenerator;
import com.terraforged.engine.world.continent.simple.MultiContinentGenerator;
import com.terraforged.engine.world.continent.simple.SingleContinentGenerator;

public enum ContinentType
{
    MULTI(0) {
        @Override
        public Continent create(final Seed seed, final GeneratorContext context) {
            return new MultiContinentGenerator(seed, context);
        }
    }, 
    SINGLE(1) {
        @Override
        public Continent create(final Seed seed, final GeneratorContext context) {
            return new SingleContinentGenerator(seed, context);
        }
    }, 
    MULTI_IMPROVED(2) {
        @Override
        public Continent create(final Seed seed, final GeneratorContext context) {
            return new AdvancedContinentGenerator(seed, context);
        }
    }, 
    @Unstable
    FANCY(3) {
        @Override
        public Continent create(final Seed seed, final GeneratorContext context) {
            return new FancyContinentGenerator(seed, context);
        }
    };
    
    public final int index;
    
    private ContinentType(final int index) {
        this.index = index;
    }
    
    public abstract Continent create(final Seed p0, final GeneratorContext p1);
}
