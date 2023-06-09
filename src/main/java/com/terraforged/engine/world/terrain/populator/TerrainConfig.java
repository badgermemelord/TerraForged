// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain.populator;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.spec.SpecName;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.noise.Source;

public class TerrainConfig implements SpecName
{
    private static final String SPEC_NAME = "Terrain";
    private final Terrain type;
    private final Module noise;
    private final float weight;
    private static final DataFactory<TerrainConfig> FACTORY;
    
    public TerrainConfig(final Terrain type, final Module noise, final float weight) {
        this.type = type;
        this.weight = weight;
        this.noise = noise;
    }
    
    @Override
    public String getSpecName() {
        return "Terrain";
    }
    
    public TerrainPopulator createPopulator(final Module baseNoise) {
        return new TerrainPopulator(this.type, baseNoise, this.noise, this.weight);
    }
    
    public static DataSpec<TerrainConfig> spec() {
        return DataSpec.builder("Terrain", TerrainConfig.class, TerrainConfig.FACTORY).add("type", (Object)TerrainType.NONE.getName(), data -> data.type.getName()).add("weight", (Object)1.0f, data -> data.weight).add("noise", (Object)Source.ZERO, data -> data.noise).build();
    }
    
    static {
        FACTORY = ((data, spec, context) -> new TerrainConfig(spec.get("type", data, v -> TerrainType.get(v.asString())), spec.get("noise", data, Module.class, context), spec.get("weight", data, DataValue::asFloat)));
    }
}
