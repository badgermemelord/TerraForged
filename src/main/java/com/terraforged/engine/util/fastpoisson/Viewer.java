//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.util.fastpoisson;

import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class Viewer {
    public Viewer() {
    }

    public static void main(String[] args) {
        Random random = new Random(12345L);
        FastPoisson poisson = new FastPoisson();
        BufferedImage image = new BufferedImage(128, 128, 1);
        JLabel label = new JLabel(new ImageIcon(image));
        render(0.0F, 0.0F, image, poisson, random, label);
        JFrame frame = new JFrame();
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo((Component)null);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }

    private static void render(float ox, float oz, BufferedImage image, FastPoisson poisson, Random random, JLabel label) {
        render(ox, oz, image, poisson, random);
        label.setIcon(new ImageIcon(image.getScaledInstance(512, 512, 2)));
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(20L);
            } catch (InterruptedException var7) {
                var7.printStackTrace();
            }

            render(ox, oz + 0.5F, image, poisson, random, label);
        });
    }

    private static void render(float px, float pz, BufferedImage image, FastPoisson poisson, Random random) {
        int ix = (int)px;
        int iz = (int)pz;
        int chunkX = ix >> 4;
        int chunkZ = iz >> 4;
        boolean lines = true;
        PosUtil.iterate(0, 0, image.getWidth(), image.getHeight(), image, (dx, dz, img) -> {
            int x = NoiseUtil.round((float)(ix + dx));
            int z = NoiseUtil.round((float)(iz + dz));
            int xx = x & 15;
            int zz = z & 15;
            int color = xx != 0 && zz != 0 ? 2236962 : 0;
            image.setRGB(dx, dz, color);
        });
        FastPoissonContext config = new FastPoissonContext(4, 0.75F, 0.2F, Source.ONE);
        long start = System.currentTimeMillis();
        int lengthX = image.getWidth() >> 4;
        int lengthZ = image.getHeight() >> 4;
        PosUtil.iterate(chunkX, chunkZ, lengthX + 1, lengthZ + 1, (Object)null, (cx, cz, ctx) -> {
            int color = NoiseUtil.hash(cx, cz);
            random.setSeed(PosUtil.pack(cx, cz));
            poisson.visit(1, cx, cz, random, config, image, (x, z, img) -> {
                int relX = x - ix;
                int relZ = z - iz;
                if (PosUtil.contains(relX, relZ, 0, 0, image.getWidth(), image.getHeight())) {
                    image.setRGB(relX, relZ, color);
                }

            });
        });
        long time = System.currentTimeMillis() - start;
    }
}
