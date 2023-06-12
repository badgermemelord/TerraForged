//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.world.biome.TempCategory;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiomes;
import com.terraforged.engine.world.biome.map.defaults.FallbackBiomes;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;

public interface BiomeContext<T> extends IntComparator {
    int getId(T var1);

    T getValue(int var1);

    String getName(int var1);

    IntSet getRiverOverrides();

    BiomeContext.Defaults<T> getDefaults();

    BiomeContext.Properties<T> getProperties();

    default int compare(int a, int b) {
        return this.getName(a).compareTo(this.getName(b));
    }

    public interface Defaults<T> {
        DefaultBiomes getDefaults();

        FallbackBiomes<T> getFallbacks();
    }

    public interface Properties<T> {
        BiomeContext<T> getContext();

        float getDepth(T var1);

        float getMoisture(T var1);

        float getTemperature(T var1);

        TempCategory getTempCategory(T var1);

        TempCategory getMountainCategory(T var1);

        default float getDepth(int id) {
            return this.getDepth(this.getContext().getValue(id));
        }

        default float getMoisture(int id) {
            return this.getMoisture(this.getContext().getValue(id));
        }

        default float getTemperature(int id) {
            return this.getTemperature(this.getContext().getValue(id));
        }

        default TempCategory getTempCategory(int id) {
            return this.getTempCategory(this.getContext().getValue(id));
        }

        default TempCategory getMountainCategory(int id) {
            return this.getMountainCategory(this.getContext().getValue(id));
        }
    }
}
