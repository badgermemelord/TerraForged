//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.world.terrain;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class TerrainType {
    private static final Object lock = new Object();
    private static final List<Terrain> REGISTRY = new CopyOnWriteArrayList();
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

    public TerrainType() {
    }

    public static void forEach(Consumer<Terrain> action) {
        REGISTRY.forEach(action);
    }

    public static Optional<Terrain> find(Predicate<Terrain> filter) {
        return REGISTRY.stream().filter(filter).findFirst();
    }

    public static Terrain get(String name) {
        Iterator var1 = REGISTRY.iterator();

        Terrain terrain;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            terrain = (Terrain)var1.next();
        } while(!terrain.getName().equalsIgnoreCase(name));

        return terrain;
    }

    public static Terrain get(int id) {
        synchronized(lock) {
            return id >= 0 && id < REGISTRY.size() ? (Terrain)REGISTRY.get(id) : NONE;
        }
    }

    public static Terrain register(Terrain instance) {
        synchronized(lock) {
            Terrain current = get(instance.getName());
            if (current != null) {
                return current;
            } else {
                Terrain terrain = instance.withId(REGISTRY.size());
                REGISTRY.add(terrain);
                return terrain;
            }
        }
    }

    public static Terrain registerComposite(Terrain a, Terrain b) {
        if (a == b) {
            return a;
        } else {
            synchronized(lock) {
                Terrain min = a.getId() < b.getId() ? a : b;
                Terrain max = a.getId() > b.getId() ? a : b;
                Terrain current = get(min.getName() + "-" + max.getName());
                if (current != null) {
                    return current;
                } else {
                    CompositeTerrain mix = new CompositeTerrain(REGISTRY.size(), min, max);
                    REGISTRY.add(mix);
                    return mix;
                }
            }
        }
    }

    private static Terrain register(String name, TerrainCategory type) {
        synchronized(lock) {
            Terrain terrain = new Terrain(REGISTRY.size(), name, type);
            REGISTRY.add(terrain);
            return terrain;
        }
    }

    private static Terrain registerWetlands(String name, TerrainCategory type) {
        synchronized(lock) {
            Terrain terrain = new ConfiguredTerrain(REGISTRY.size(), name, type, true);
            REGISTRY.add(terrain);
            return terrain;
        }
    }

    private static Terrain registerBadlands(String name, TerrainCategory type) {
        synchronized(lock) {
            Terrain terrain = new ConfiguredTerrain(REGISTRY.size(), name, type, 0.3F);
            REGISTRY.add(terrain);
            return terrain;
        }
    }

    private static Terrain registerMountain(String name, TerrainCategory type) {
        synchronized(lock) {
            Terrain terrain = new ConfiguredTerrain(REGISTRY.size(), name, type, true, true);
            REGISTRY.add(terrain);
            return terrain;
        }
    }

    private static Terrain registerVolcano(String name, TerrainCategory type) {
        synchronized(lock) {
            Terrain terrain = new ConfiguredTerrain(REGISTRY.size(), name, type, true, true) {
                public boolean isVolcano() {
                    return true;
                }

                public boolean overridesCoast() {
                    return true;
                }
            };
            REGISTRY.add(terrain);
            return terrain;
        }
    }

    static {
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
