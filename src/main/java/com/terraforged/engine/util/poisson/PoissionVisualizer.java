//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.util.poisson;

import com.terraforged.noise.Source;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class PoissionVisualizer {
    public PoissionVisualizer() {
    }

    public static void main(String[] args) {
        int size = 512;
        int radius = 5;
        int chunkSize = 16;
        int chunks = size / chunkSize;
        BufferedImage image = new BufferedImage(size, size, 1);
        Poisson poisson = new Poisson(radius);
        PoissonContext context = new PoissonContext(213L, new Random());
        context.density = Source.simplex(213, 200, 2).clamp(0.25, 0.75).map(0.0, 1.0);
        long time = 0L;
        long count = 0L;
        int chunkX = 342;
        int chunkZ = 546;

        for(int cz = 0; cz < chunks; ++cz) {
            for(int cx = 0; cx < chunks; ++cx) {
                long start = System.nanoTime();
                poisson.visit(chunkX + cx, chunkZ + cz, context, (x, z) -> {
                    x -= chunkX << 4;
                    z -= chunkZ << 4;
                    if (x >= 0 && x < image.getWidth() && z >= 0 && z < image.getHeight()) {
                        image.setRGB(x, z, Color.WHITE.getRGB());
                    }
                });
                time += System.nanoTime() - start;
                ++count;
            }
        }

        double total = (double)time / 1000000.0;
        double avg = total / (double)count;
        System.out.printf("Total time: %.3fms, Average Per Chunk: %.3fms\n", total, avg);
        JFrame frame = new JFrame();
        frame.add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo((Component)null);
        frame.setDefaultCloseOperation(3);
    }
}
