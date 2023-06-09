// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.terrain;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TerrainType
{
    private static final Object lock;
    private static final List<Terrain> REGISTRY;
    public static final Terrain NONE;
    public static final Terrain DEEP_OCEAN;
    public static final Terrain SHALLOW_OCEAN;
    public static final Terrain COAST;
    public static final Terrain BEACH;
    public static final Terrain RIVER;
    public static final Terrain LAKE;
    public static final Terrain WETLAND;
    public static final Terrain FLATS;
    public static final Terrain BADLANDS;
    public static final Terrain PLATEAU;
    public static final Terrain HILLS;
    public static final Terrain MOUNTAINS;
    public static final Terrain MOUNTAIN_CHAIN;
    public static final Terrain VOLCANO;
    public static final Terrain VOLCANO_PIPE;
    
    public static void forEach(final Consumer<Terrain> action) {
        TerrainType.REGISTRY.forEach(action);
    }
    
    public static Optional<Terrain> find(final Predicate<Terrain> filter) {
        return TerrainType.REGISTRY.stream().filter(filter).findFirst();
    }
    
    public static Terrain get(final String name) {
        for (final Terrain terrain : TerrainType.REGISTRY) {
            if (terrain.getName().equalsIgnoreCase(name)) {
                return terrain;
            }
        }
        return null;
    }
    
    public static Terrain get(final int id) {
        synchronized (TerrainType.lock) {
            if (id >= 0 && id < TerrainType.REGISTRY.size()) {
                return TerrainType.REGISTRY.get(id);
            }
            return TerrainType.NONE;
        }
    }
    
    public static Terrain register(final Terrain instance) {
        synchronized (TerrainType.lock) {
            final Terrain current = get(instance.getName());
            if (current != null) {
                return current;
            }
            final Terrain terrain = instance.withId(TerrainType.REGISTRY.size());
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    public static Terrain registerComposite(final Terrain a, final Terrain b) {
        if (a == b) {
            return a;
        }
        synchronized (TerrainType.lock) {
            final Terrain min = (a.getId() < b.getId()) ? a : b;
            final Terrain max = (a.getId() > b.getId()) ? a : b;
            final Terrain current = get(min.getName() + "-" + max.getName());
            if (current != null) {
                return current;
            }
            final CompositeTerrain mix = new CompositeTerrain(TerrainType.REGISTRY.size(), min, max);
            TerrainType.REGISTRY.add(mix);
            return mix;
        }
    }
    
    private static Terrain register(final String name, final TerrainCategory type) {
        synchronized (TerrainType.lock) {
            final Terrain terrain = new Terrain(TerrainType.REGISTRY.size(), name, type);
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    private static Terrain registerWetlands(final String name, final TerrainCategory type) {
        synchronized (TerrainType.lock) {
            final Terrain terrain = new ConfiguredTerrain(TerrainType.REGISTRY.size(), name, type, true);
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    private static Terrain registerBadlands(final String name, final TerrainCategory type) {
        synchronized (TerrainType.lock) {
            final Terrain terrain = new ConfiguredTerrain(TerrainType.REGISTRY.size(), name, type, 0.3f);
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    private static Terrain registerMountain(final String name, final TerrainCategory type) {
        synchronized (TerrainType.lock) {
            final Terrain terrain = new ConfiguredTerrain(TerrainType.REGISTRY.size(), name, type, true, true);
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    private static Terrain registerVolcano(final String name, final TerrainCategory type) {
        synchronized (TerrainType.lock) {
            final Terrain terrain = new ConfiguredTerrain(TerrainType.REGISTRY.size(), name, type, true, true) {
                @Override
                public boolean isVolcano() {
                    return true;
                }
                
                @Override
                public boolean overridesCoast() {
                    return true;
                }
            };
            TerrainType.REGISTRY.add(terrain);
            return terrain;
        }
    }
    
    static {
        lock = new Object();
        REGISTRY = new CopyOnWriteArrayList<Terrain>();
        NONE = register("none", TerrainCategory.NONE);
        DEEP_OCEAN = register("deep_ocean", TerrainCategory.DEEP_OCEAN);
        SHALLOW_OCEAN = register("ocean", TerrainCategory.SHALLOW_OCEAN);
        COAST = register("coast", TerrainCategory.COAST);
        BEACH = register("beach", TerrainCategory.BEACH);
        RIVER = register("river", TerrainCategory.RIVER);
        LAKE = register("lake", TerrainCategory.LAKE);
        WETLAND = registerWetlands("wetland", TerrainCategory.WETLAND);
        FLATS = register("flats", TerrainCategory.FLATLAND);
        BADLANDS = registerBadlands("badlands", TerrainCategory.FLATLAND);
        PLATEAU = register("plateau", TerrainCategory.LOWLAND);
        HILLS = register("hills", TerrainCategory.LOWLAND);
        MOUNTAINS = registerMountain("mountains", TerrainCategory.HIGHLAND);
        MOUNTAIN_CHAIN = registerMountain("mountain_chain", TerrainCategory.HIGHLAND);
        VOLCANO = registerVolcano("volcano", TerrainCategory.HIGHLAND);
        VOLCANO_PIPE = registerVolcano("volcano_pipe", TerrainCategory.HIGHLAND);
    }
}
