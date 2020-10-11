/*
 * MIT License
 *
 * Copyright (c) 2020 TerraForged
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.terraforged.mod.biome.provider;

import com.terraforged.api.material.layer.LayerManager;
import com.terraforged.api.material.layer.LayerMaterial;
import com.terraforged.core.concurrent.Resource;
import com.terraforged.fm.GameContext;
import com.terraforged.mod.material.Materials;
import com.terraforged.mod.util.DummyBlockReader;
import com.terraforged.mod.util.ListUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;

import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class DesertBiomes {

    private final Set<Biome> reds;
    private final Set<Biome> whites;
    private final Set<Biome> deserts;
    private final List<Biome> redSand;
    private final List<Biome> whiteSand;
    private final LayerManager layerManager;

    private final int maxRedIndex;
    private final int maxWhiteIndex;

    private final Biome defaultRed;
    private final Biome defaultWhite;

    public DesertBiomes(Materials materials, List<Biome> deserts, GameContext context) {
        List<Biome> white = new LinkedList<>();
        List<Biome> red = new LinkedList<>();
        try (Resource<DummyBlockReader> reader = DummyBlockReader.pooled()) {
            for (Biome biome : deserts) {
                if (biome == null) {
                    continue;
                }
                BiomeGenerationSettings settings = BiomeHelper.getGenSettings(biome);
                if (settings == null) {
                    continue;
                }
                ISurfaceBuilderConfig config = settings.func_242502_e();
                if (config == null) {
                    continue;
                }
                BlockState top = config.getTop();
                MaterialColor color = top.getMaterialColor(reader.get().set(top), BlockPos.ZERO);
                int whiteDist2 = distance2(color, MaterialColor.SAND);
                int redDist2 = distance2(color, MaterialColor.ADOBE);
                if (whiteDist2 < redDist2) {
                    white.add(biome);
                } else {
                    red.add(biome);
                }
            }
        }
        this.layerManager = materials.getLayerManager();
        this.whiteSand = new ArrayList<>(white);
        this.redSand = new ArrayList<>(red);
        this.deserts = new HashSet<>(deserts);
        this.whites = new HashSet<>(white);
        this.reds = new HashSet<>(red);
        this.whiteSand.sort(Comparator.comparing(context.biomes::getName));
        this.redSand.sort(Comparator.comparing(context.biomes::getName));
        this.maxRedIndex = red.size() - 1;
        this.maxWhiteIndex = white.size() - 1;
        this.defaultRed = context.biomes.get(Biomes.BADLANDS);
        this.defaultWhite = context.biomes.get(Biomes.DESERT);
    }
    public boolean isDesert(Biome biome) {
        return deserts.contains(biome);
    }

    public boolean isRedDesert(Biome biome) {
        return reds.contains(biome);
    }

    public boolean isWhiteDesert(Biome biome) {
        return whites.contains(biome);
    }

    public Biome getRedDesert(float shape) {
        return ListUtils.get(redSand, maxRedIndex, shape, defaultRed);
    }

    public Biome getWhiteDesert(float shape) {
        return ListUtils.get(whiteSand, maxWhiteIndex, shape, defaultWhite);
    }

    public LayerMaterial getSandLayers(Biome biome) {
        return layerManager.getMaterial(Blocks.SAND);
    }

    private static int distance2(MaterialColor mc1, MaterialColor mc2) {
        Color c1 = new Color(mc1.colorValue);
        Color c2 = new Color(mc2.colorValue);
        int dr = c1.getRed() - c2.getRed();
        int dg = c1.getGreen() - c2.getGreen();
        int db = c1.getBlue() - c2.getBlue();
        return (dr * dr) + (dg * dg) + (db * db);
    }
}
