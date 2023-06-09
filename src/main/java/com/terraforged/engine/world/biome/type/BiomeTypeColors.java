// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.type;

import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BiomeTypeColors
{
    private static BiomeTypeColors instance;
    private final Map<String, Color> colors;
    
    private BiomeTypeColors() {
        this.colors = new HashMap<String, Color>();
        try (final InputStream inputStream = BiomeType.class.getResourceAsStream("/biomes.txt")) {
            final Properties properties = new Properties();
            properties.load(inputStream);
            for (final Map.Entry<?, ?> entry : properties.entrySet()) {
                final Color color = Color.decode("#" + entry.getValue().toString());
                this.colors.put(entry.getKey().toString(), color);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public Color getColor(final String name, final Color defaultColor) {
        return this.colors.getOrDefault(name, defaultColor);
    }
    
    public static BiomeTypeColors getInstance() {
        return BiomeTypeColors.instance;
    }
    
    public static void main(final String[] args) throws Throwable {
        try (final FileWriter writer = new FileWriter("biome_colors.properties")) {
            final Properties properties = new Properties();
            for (final BiomeType type : BiomeType.values()) {
                final int r = type.getColor().getRed();
                final int g = type.getColor().getGreen();
                final int b = type.getColor().getBlue();
                properties.setProperty(type.name(), String.format("%02x%02x%02x", r, g, b));
            }
            properties.store(writer, "TerraForged BiomeType Hex Colors (do not include hash/pound character)");
        }
    }
    
    static {
        BiomeTypeColors.instance = new BiomeTypeColors();
    }
}
