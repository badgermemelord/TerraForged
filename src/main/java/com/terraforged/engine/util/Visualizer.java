//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.util;

import com.terraforged.engine.util.pos.PosIterator;
import com.terraforged.noise.Module;
import com.terraforged.noise.Source;
import java.awt.Color;
import java.awt.Component;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Visualizer {
    public Visualizer() {
    }

    public static void main(String[] args) {
        int size = 512;
        Module noise = Source.simplex(123, 40, 2).warp(Source.RAND, 124, 2, 1, 4.0);
        BufferedImage image = new BufferedImage(size, size, 1);
        PosIterator iterator = PosIterator.area(0, 0, size, size);

        while(iterator.next()) {
            float value = noise.getValue((float)iterator.x(), (float)iterator.z());
            image.setRGB(iterator.x(), iterator.z(), getMaterial(value));
        }

        JFrame frame = new JFrame();
        frame.add(new JLabel(new ImageIcon(image)));
        frame.setVisible(true);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo((Component)null);
        frame.setDefaultCloseOperation(3);
    }

    private static int getMaterial(float value) {
        if ((double)value > 0.6) {
            return (double)value < 0.75 ? Color.HSBtoRGB(0.05F, 0.4F, 0.2F) : Color.HSBtoRGB(0.05F, 0.4F, 0.4F);
        } else {
            return Color.HSBtoRGB(0.25F, 0.4F, 0.6F);
        }
    }
}
