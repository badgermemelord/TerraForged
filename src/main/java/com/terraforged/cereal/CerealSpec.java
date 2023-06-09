// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal;

public class CerealSpec
{
    public static final char NONE = '\0';
    public static final CerealSpec STANDARD;
    public final String indent;
    public final char delimiter;
    public final char separator;
    public final char escapeChar;
    
    public CerealSpec(final String indent, final char delimiter, final char separator, final char escapeChar) {
        this.indent = indent;
        this.delimiter = delimiter;
        this.escapeChar = escapeChar;
        this.separator = ((delimiter == '\0') ? ' ' : separator);
    }
    
    static {
        STANDARD = new CerealSpec("  ", '\0', ' ', '\'');
    }
}
