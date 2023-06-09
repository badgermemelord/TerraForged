// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util;

import com.terraforged.engine.util.pos.PosIterator;
import com.terraforged.noise.Source;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Visualizer
{
    public static void main(final String[] args) {
        final int size = 512;
        final Module noise = Source.simplex(123, 40, 2).warp(Source.RAND, 124, 2, 1, 4.0);
        final BufferedImage image = new BufferedImage(size, size, 1);
        final PosIterator iterator = PosIterator.area(0, 0, size, size);
        while (iterator.next()) {
            final float value = noise.getValue((float)iterator.x(), (float)iterator.z());
            image.setRGB(iterator.x(), iterator.z(), getMaterial(value));
        }
        final JFrame frame = new JFrame();
        frame.add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(3);
    }
    
    private static int getMaterial(final float value) {
        if (value <= 0.6) {
            return Color.HSBtoRGB(0.25f, 0.4f, 0.6f);
        }
        if (value < 0.75) {
            return Color.HSBtoRGB(0.05f, 0.4f, 0.2f);
        }
        return Color.HSBtoRGB(0.05f, 0.4f, 0.4f);
    }
}
