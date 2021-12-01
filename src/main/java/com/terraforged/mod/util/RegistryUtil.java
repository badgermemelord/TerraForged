package com.terraforged.mod.util;

import com.google.common.base.Suppliers;
import com.mojang.serialization.DynamicOps;
import com.terraforged.mod.TerraForged;
import com.terraforged.mod.worldgen.Generator;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.RegistryReadOps;
import net.minecraft.world.level.dimension.LevelStem;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class RegistryUtil {
    public static MappedRegistry<LevelStem> reseed(long seed, MappedRegistry<LevelStem> registry) {
        var overworld = registry.getOrThrow(LevelStem.OVERWORLD);

        if (overworld.generator() instanceof Generator) {
            var generator = overworld.generator().withSeed(seed);
            if (generator == overworld.generator()) return registry;

            var lifecycle = registry.lifecycle(overworld);
            var levelStem = new LevelStem(overworld.typeSupplier(), generator);
            registry.registerOrOverride(OptionalInt.empty(), LevelStem.OVERWORLD, levelStem, lifecycle);

            TerraForged.LOG.info("Re-seeded generator: {}", seed);
        }

        return registry;
    }

    public static Optional<RegistryAccess> getAccess(DynamicOps<?> ops) {
        if (!(ops instanceof RegistryReadOps)) {
            return Optional.empty();
        }

        try {
            var field = GETTER.get();
            var access = (RegistryAccess) field.get(ops);
            return Optional.ofNullable(access);
        } catch (Throwable t) {
            t.printStackTrace();
            return Optional.empty();
        }
    }

    protected static final Supplier<Field> GETTER = Suppliers.memoize(() -> {
        for (var field : RegistryReadOps.class.getDeclaredFields()) {
            if (field.getType() == RegistryAccess.class) {
                field.setAccessible(true);
                return field;
            }
        }

        throw new UnsupportedOperationException("Method not found!");
    });
}