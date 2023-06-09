// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util.fastpoisson;

import com.terraforged.engine.util.pos.PosUtil;
import com.terraforged.noise.Source;
import com.terraforged.noise.util.NoiseUtil;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Viewer
{
    public static void main(final String[] args) {
        final Random random = new Random(12345L);
        final FastPoisson poisson = new FastPoisson();
        final BufferedImage image = new BufferedImage(128, 128, 1);
        final JLabel label = new JLabel(new ImageIcon(image));
        render(0.0f, 0.0f, image, poisson, random, label);
        final JFrame frame = new JFrame();
        frame.add(label);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(3);
        frame.setVisible(true);
    }
    
    private static void render(final float ox, final float oz, final BufferedImage image, final FastPoisson poisson, final Random random, final JLabel label) {
        render(ox, oz, image, poisson, random);
        label.setIcon(new ImageIcon(image.getScaledInstance(512, 512, 2)));
        SwingUtilities.invokeLater(() -> {
            try {
                Thread.sleep(20L);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            render(ox, oz + 0.5f, image, poisson, random, label);
        });
    }
    
    private static void render(final float px, final float pz, final BufferedImage image, final FastPoisson poisson, final Random random) {
        final int ix = (int)px;
        final int iz = (int)pz;
        final int chunkX = ix >> 4;
        final int chunkZ = iz >> 4;
        final boolean lines = true;
        final Object o;
        final int x2;
        final Object o2;
        final int z2;
        final int xx;
        final int zz;
        final int color;
        PosUtil.iterate(0, 0, image.getWidth(), image.getHeight(), image, (dx, dz, img) -> {
            x2 = NoiseUtil.round((float)(o + dx));
            z2 = NoiseUtil.round((float)(o2 + dz));
            xx = (x2 & 0xF);
            zz = (z2 & 0xF);
            color = ((xx == 0 || zz == 0) ? 0 : 2236962);
            image.setRGB((int)dx, (int)dz, color);
            return;
        });
        final FastPoissonContext config = new FastPoissonContext(4, 0.75f, 0.2f, Source.ONE);
        final long start = System.currentTimeMillis();
        final int lengthX = image.getWidth() >> 4;
        final int lengthZ = image.getHeight() >> 4;
        final int color2;
        final FastPoissonContext context;
        final int n;
        final int relX;
        final int n2;
        final int relZ;
        final int rgb;
        PosUtil.iterate(chunkX, chunkZ, lengthX + 1, lengthZ + 1, null, (cx, cz, ctx) -> {
            color2 = NoiseUtil.hash(cx, cz);
            random.setSeed(PosUtil.pack(cx, cz));
            poisson.visit(1, cx, cz, random, context, image, (x, z, img) -> {
                relX = x - n;
                relZ = z - n2;
                if (PosUtil.contains(relX, relZ, 0, 0, image.getWidth(), image.getHeight())) {
                    image.setRGB(relX, relZ, rgb);
                }
            });
            return;
        });
        final long time = System.currentTimeMillis() - start;
    }
}
