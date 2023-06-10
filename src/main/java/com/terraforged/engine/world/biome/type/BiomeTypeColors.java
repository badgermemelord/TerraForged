//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.world.biome.type;

import java.awt.Color;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class BiomeTypeColors {
    private static BiomeTypeColors instance = new BiomeTypeColors();
    private final Map<String, Color> colors = new HashMap();

    private BiomeTypeColors() {
        try {
            InputStream inputStream = BiomeType.class.getResourceAsStream("/biomes.txt");
            Throwable var2 = null;

            try {
                Properties properties = new Properties();
                properties.load(inputStream);

                for(Entry<?, ?> entry : properties.entrySet()) {
                    Color color = Color.decode("#" + entry.getValue().toString());
                    this.colors.put(entry.getKey().toString(), color);
                }
            } catch (Throwable var15) {
                var2 = var15;
                throw var15;
            } finally {
                if (inputStream != null) {
                    if (var2 != null) {
                        try {
                            inputStream.close();
                        } catch (Throwable var14) {
                            var2.addSuppressed(var14);
                        }
                    } else {
                        inputStream.close();
                    }
                }
            }
        } catch (IOException var17) {
            var17.printStackTrace();
        }
    }

    public Color getColor(String name, Color defaultColor) {
        return (Color)this.colors.getOrDefault(name, defaultColor);
    }

    public static BiomeTypeColors getInstance() {
        return instance;
    }

    public static void main(String[] args) throws Throwable {
        FileWriter writer = new FileWriter("biome_colors.properties");
        Throwable var2 = null;

        try {
            Properties properties = new Properties();

            for(BiomeType type : BiomeType.values()) {
                int r = type.getColor().getRed();
                int g = type.getColor().getGreen();
                int b = type.getColor().getBlue();
                properties.setProperty(type.name(), String.format("%02x%02x%02x", r, g, b));
            }

            properties.store(writer, "TerraForged BiomeType Hex Colors (do not include hash/pound character)");
        } catch (Throwable var18) {
            var2 = var18;
            throw var18;
        } finally {
            if (writer != null) {
                if (var2 != null) {
                    try {
                        writer.close();
                    } catch (Throwable var17) {
                        var2.addSuppressed(var17);
                    }
                } else {
                    writer.close();
                }
            }
        }
    }
}
