//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.settings;

import com.terraforged.engine.serialization.annotation.Comment;
import com.terraforged.engine.serialization.annotation.Rand;
import com.terraforged.engine.serialization.annotation.Range;
import com.terraforged.engine.serialization.annotation.Serializable;
import com.terraforged.engine.world.terrain.populator.TerrainPopulator;
import com.terraforged.noise.Module;

@Serializable
public class TerrainSettings {
    public TerrainSettings.General general = new TerrainSettings.General();
    public TerrainSettings.Terrain steppe = new TerrainSettings.Terrain(1.0F, 1.0F, 1.0F);
    public TerrainSettings.Terrain plains = new TerrainSettings.Terrain(2.0F, 1.0F, 1.0F);
    public TerrainSettings.Terrain hills = new TerrainSettings.Terrain(2.0F, 1.0F, 1.0F);
    public TerrainSettings.Terrain dales = new TerrainSettings.Terrain(1.5F, 1.0F, 1.0F);
    public TerrainSettings.Terrain plateau = new TerrainSettings.Terrain(1.5F, 1.0F, 1.0F);
    public TerrainSettings.Terrain badlands = new TerrainSettings.Terrain(1.0F, 1.0F, 1.0F);
    public TerrainSettings.Terrain torridonian = new TerrainSettings.Terrain(2.0F, 1.0F, 1.0F);
    public TerrainSettings.Terrain mountains = new TerrainSettings.Terrain(2.5F, 1.0F, 1.0F);
    public TerrainSettings.Terrain volcano = new TerrainSettings.Terrain(5.0F, 1.0F, 1.0F);

    public TerrainSettings() {
    }

    @Serializable
    public static class General {
        @Rand
        @Comment({"A seed offset used to randomise terrain distribution"})
        public int terrainSeedOffset = 0;
        @Range(
                min = 125.0F,
                max = 5000.0F
        )
        @Comment({"Controls the size of terrain regions"})
        public int terrainRegionSize = 1200;
        @Range(
                min = 0.01F,
                max = 1.0F
        )
        @Comment({"Globally controls the vertical scaling of terrain"})
        public float globalVerticalScale = 0.98F;
        @Range(
                min = 0.01F,
                max = 5.0F
        )
        @Comment({"Globally controls the horizontal scaling of terrain"})
        public float globalHorizontalScale = 1.0F;
        @Comment({"Carries out extra processing on mountains to make them look even nicer.", "Can be disabled to improve performance slightly."})
        public boolean fancyMountains = true;

        public General() {
        }
    }

    @Serializable
    public static class Terrain {
        @Range(
                min = 0.0F,
                max = 10.0F
        )
        @Comment({"Controls how common this terrain type is"})
        public float weight = 1.0F;
        @Range(
                min = 0.0F,
                max = 2.0F
        )
        @Comment({"Controls the base height of this terrain"})
        public float baseScale = 1.0F;
        @Range(
                min = 0.0F,
                max = 10.0F
        )
        @Comment({"Stretches or compresses the terrain vertically"})
        public float verticalScale = 1.0F;
        @Range(
                min = 0.0F,
                max = 10.0F
        )
        @Comment({"Stretches or compresses the terrain horizontally"})
        public float horizontalScale = 1.0F;

        public Terrain() {
        }

        public Terrain(float weight, float vertical, float horizontal) {
            this.weight = weight;
            this.verticalScale = vertical;
            this.horizontalScale = horizontal;
        }

        public Module apply(double bias, double scale, Module module) {
            double moduleBias = bias * (double)this.baseScale;
            double moduleScale = scale * (double)this.verticalScale;
            Module outputModule = module.scale(moduleScale).bias(moduleBias);
            return TerrainPopulator.clamp(outputModule);
        }
    }
}
