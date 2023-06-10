//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.world.terrain.populator;

import com.terraforged.cereal.spec.DataFactory;
import com.terraforged.cereal.spec.DataSpec;
import com.terraforged.cereal.spec.SpecName;
import com.terraforged.cereal.value.DataValue;
import com.terraforged.engine.world.terrain.Terrain;
import com.terraforged.engine.world.terrain.TerrainType;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;

public class TerrainConfig implements SpecName {
    private static final String SPEC_NAME = "Terrain";
    private final Terrain type;
    private final Module noise;
    private final float weight;
    private static final DataFactory<TerrainConfig> FACTORY = (data, spec, context) -> {
        return new TerrainConfig((Terrain)spec.get("type", data, (v) -> {
            return TerrainType.get(v.asString());
        }), (Module)spec.get("noise", data, Module.class, context), (Float)spec.get("weight", data, DataValue::asFloat));
    };

    public TerrainConfig(Terrain type, Module noise, float weight) {
        this.type = type;
        this.weight = weight;
        this.noise = noise;
    }

    public String getSpecName() {
        return "Terrain";
    }

    public TerrainPopulator createPopulator(Module baseNoise) {
        return new TerrainPopulator(this.type, baseNoise, this.noise, this.weight);
    }

    public static DataSpec<TerrainConfig> spec() {
        return DataSpec.builder("Terrain", TerrainConfig.class, FACTORY).add("type", TerrainType.NONE.getName(), (data) -> {
            return data.type.getName();
        }).add("weight", 1.0F, (data) -> {
            return data.weight;
        }).add("noise", Source.ZERO, (data) -> {
            return data.noise;
        }).build();
    }
}
