// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.world.biome.type;

import com.terraforged.noise.util.NoiseUtil;
import com.terraforged.noise.util.Vec2f;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class BiomeTypeLoader
{
    private static BiomeTypeLoader instance;
    private final BiomeType[][] map;
    
    public BiomeTypeLoader() {
        this.map = new BiomeType[256][256];
        this.generateTypeMap();
    }
    
    public BiomeType[][] getTypeMap() {
        return this.map;
    }
    
    public Vec2f[] getRanges(final BiomeType type) {
        float minTemp = 1.0f;
        float maxTemp = 0.0f;
        float minMoist = 1.0f;
        float maxMoist = 0.0f;
        for (int moist = 0; moist < this.map.length; ++moist) {
            final BiomeType[] row = this.map[moist];
            for (int temp = 0; temp < row.length; ++temp) {
                final BiomeType t = row[temp];
                if (t == type) {
                    final float temperature = temp / (float)(row.length - 1);
                    final float moisture = moist / (float)(this.map.length - 1);
                    minTemp = Math.min(minTemp, temperature);
                    maxTemp = Math.max(maxTemp, temperature);
                    minMoist = Math.min(minMoist, moisture);
                    maxMoist = Math.max(maxMoist, moisture);
                }
            }
        }
        return new Vec2f[] { new Vec2f(minTemp, maxTemp), new Vec2f(minMoist, maxMoist) };
    }
    
    private BiomeType getType(final int x, final int y) {
        return this.map[y][x];
    }
    
    private void generateTypeMap() {
        try {
            final BufferedImage image = ImageIO.read(BiomeType.class.getResourceAsStream("/biomes.png"));
            final float xf = image.getWidth() / 256.0f;
            final float yf = image.getHeight() / 256.0f;
            for (int y = 0; y < 256; ++y) {
                for (int x = 0; x < 256; ++x) {
                    if (255 - y > x) {
                        this.map[255 - y][x] = BiomeType.ALPINE;
                    }
                    else {
                        final int ix = NoiseUtil.round(x * xf);
                        final int iy = NoiseUtil.round(y * yf);
                        final int argb = image.getRGB(ix, iy);
                        final Color color = fromARGB(argb);
                        this.map[255 - y][x] = forColor(color);
                    }
                }
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private static BiomeType forColor(final Color color) {
        BiomeType type = null;
        int closest = Integer.MAX_VALUE;
        for (final BiomeType t : BiomeType.values()) {
            final int distance2 = getDistance2(color, t.getLookup());
            if (distance2 < closest) {
                closest = distance2;
                type = t;
            }
        }
        if (type == null) {
            return BiomeType.GRASSLAND;
        }
        return type;
    }
    
    private static int getDistance2(final Color a, final Color b) {
        final int dr = a.getRed() - b.getRed();
        final int dg = a.getGreen() - b.getGreen();
        final int db = a.getBlue() - b.getBlue();
        return dr * dr + dg * dg + db * db;
    }
    
    private static Color fromARGB(final int argb) {
        final int b = argb & 0xFF;
        final int g = argb >> 8 & 0xFF;
        final int r = argb >> 16 & 0xFF;
        return new Color(r, g, b);
    }
    
    private static int dist2(final int x1, final int y1, final int x2, final int y2) {
        final int dx = x1 - x2;
        final int dy = y1 - y2;
        return dx * dx + dy * dy;
    }
    
    public static BiomeTypeLoader getInstance() {
        if (BiomeTypeLoader.instance == null) {
            BiomeTypeLoader.instance = new BiomeTypeLoader();
        }
        return BiomeTypeLoader.instance;
    }
}
