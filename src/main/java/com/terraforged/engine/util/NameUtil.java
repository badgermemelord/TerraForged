// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.util;

public class NameUtil
{
    public static String toDisplayName(final String input) {
        final StringBuilder sb = new StringBuilder(input.length() + 8);
        for (int i = 0; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (i == 0) {
                c = Character.toUpperCase(c);
            }
            else if (Character.isUpperCase(c)) {
                sb.append(' ');
            }
            else {
                if (c == '_') {
                    sb.append(' ');
                    continue;
                }
                if (input.charAt(i - 1) == '_' || input.charAt(i - 1) == ' ') {
                    c = Character.toUpperCase(c);
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
    
    public static String toTranslationKey(final String parent, final String name) {
        final StringBuilder sb = new StringBuilder(name.length() * 2);
        if (!parent.isEmpty()) {
            sb.append(parent).append('.');
        }
        for (int i = 0; i < name.length(); ++i) {
            final char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append('_');
            }
            sb.append(Character.toLowerCase(c));
        }
        return sb.toString();
    }
    
    public static String toDisplayNameKey(final String name) {
        return "display.terraforged." + name;
    }
    
    public static String toTooltipKey(final String name) {
        return "tooltip.terraforged." + name;
    }
}
