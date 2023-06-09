// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.map;

import com.terraforged.engine.world.biome.TempCategory;
import com.terraforged.engine.world.biome.map.defaults.DefaultBiomes;
import com.terraforged.engine.world.biome.map.defaults.FallbackBiomes;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.ints.IntSet;

public interface BiomeContext<T> extends IntComparator
{
    int getId(final T p0);
    
    T getValue(final int p0);
    
    String getName(final int p0);
    
    IntSet getRiverOverrides();
    
    Defaults<T> getDefaults();
    
    Properties<T> getProperties();
    
    default int compare(final int a, final int b) {
        return this.getName(a).compareTo(this.getName(b));
    }
    
    public interface Properties<T>
    {
        BiomeContext<T> getContext();
        
        float getDepth(final T p0);
        
        float getMoisture(final T p0);
        
        float getTemperature(final T p0);
        
        TempCategory getTempCategory(final T p0);
        
        TempCategory getMountainCategory(final T p0);
        
        default float getDepth(final int id) {
            return this.getDepth(this.getContext().getValue(id));
        }
        
        default float getMoisture(final int id) {
            return this.getMoisture(this.getContext().getValue(id));
        }
        
        default float getTemperature(final int id) {
            return this.getTemperature(this.getContext().getValue(id));
        }
        
        default TempCategory getTempCategory(final int id) {
            return this.getTempCategory(this.getContext().getValue(id));
        }
        
        default TempCategory getMountainCategory(final int id) {
            return this.getMountainCategory(this.getContext().getValue(id));
        }
    }
    
    public interface Defaults<T>
    {
        DefaultBiomes getDefaults();
        
        FallbackBiomes<T> getFallbacks();
    }
}
