// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.poisson;

import com.terraforged.noise.Source;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class PoissionVisualizer
{
    public static void main(final String[] args) {
        final int size = 512;
        final int radius = 5;
        final int chunkSize = 16;
        final int chunks = size / chunkSize;
        final BufferedImage image = new BufferedImage(size, size, 1);
        final Poisson poisson = new Poisson(radius);
        final PoissonContext context = new PoissonContext(213L, new Random());
        context.density = Source.simplex(213, 200, 2).clamp(0.25, 0.75).map(0.0, 1.0);
        long time = 0L;
        long count = 0L;
        final int chunkX = 342;
        final int chunkZ = 546;
        for (int cz = 0; cz < chunks; ++cz) {
            for (int cx = 0; cx < chunks; ++cx) {
                final long start = System.nanoTime();
                final int n;
                final int n2;
                final BufferedImage bufferedImage;
                poisson.visit(chunkX + cx, chunkZ + cz, context, (x, z) -> {
                    x -= n << 4;
                    z -= n2 << 4;
                    if (x < 0 || x >= bufferedImage.getWidth() || z < 0 || z >= bufferedImage.getHeight()) {
                        return;
                    }
                    else {
                        bufferedImage.setRGB(x, z, Color.WHITE.getRGB());
                        return;
                    }
                });
                time += System.nanoTime() - start;
                ++count;
            }
        }
        final double total = time / 1000000.0;
        final double avg = total / count;
        System.out.printf("Total time: %.3fms, Average Per Chunk: %.3fms\n", total, avg);
        final JFrame frame = new JFrame();
        frame.add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(3);
    }
}
