// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.serial;

import java.util.Arrays;

public class DataBuffer
{
    private char[] buffer;
    private int index;
    private boolean decimal;
    private boolean numeric;
    
    public DataBuffer() {
        this.buffer = new char[5];
        this.index = -1;
        this.decimal = false;
        this.numeric = true;
    }
    
    public void reset() {
        this.index = -1;
        this.numeric = true;
        this.decimal = false;
    }
    
    public void append(final char c) {
        ++this.index;
        if (this.index >= this.buffer.length) {
            this.buffer = Arrays.copyOf(this.buffer, this.buffer.length * 2);
        }
        this.buffer[this.index] = c;
        if (this.numeric) {
            if (Character.isDigit(c)) {
                return;
            }
            if (c == '.' && !this.decimal && this.index > 0) {
                this.decimal = true;
                return;
            }
            if (c == '-' && this.index == 0) {
                return;
            }
            this.numeric = false;
        }
    }
    
    public Object getValue() {
        if (this.index == 4 && matches(this.buffer, 4, "true")) {
            return true;
        }
        if (this.index == 5 && matches(this.buffer, 5, "false")) {
            return false;
        }
        if (!this.numeric) {
            return this.toString();
        }
        if (this.decimal) {
            return parseDouble(this.buffer, this.index + 1);
        }
        return parseLong(this.buffer, this.index + 1);
    }
    
    @Override
    public String toString() {
        return new String(this.buffer, 0, this.index + 1);
    }
    
    public static boolean matches(final char[] buffer, final int length, final String other) {
        if (length != other.length()) {
            return false;
        }
        for (int i = 0; i < length; ++i) {
            if (Character.toUpperCase(buffer[i]) != Character.toUpperCase(other.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    
    public static long parseLong(final char[] buffer, final int length) {
        long value = 0L;
        boolean negative = false;
        for (int i = 0; i < length; ++i) {
            final char c = buffer[i];
            if (i == 0 && c == '-') {
                negative = true;
            }
            else {
                value = value * 10L + (c - '0');
            }
        }
        return negative ? (-value) : value;
    }
    
    public static double parseDouble(final char[] buffer, final int length) {
        double value = 0.0;
        int decimalPlace = 0;
        boolean negative = false;
        for (int i = 0; i < length; ++i) {
            final char c = buffer[i];
            if (i == 0 && c == '-') {
                negative = true;
            }
            else if (c == '.') {
                decimalPlace = 1;
            }
            else {
                value = value * 10.0 + (c - '0');
                if (decimalPlace > 0) {
                    decimalPlace *= 10;
                }
            }
        }
        if (decimalPlace > 0) {
            value /= decimalPlace;
        }
        return negative ? (-value) : value;
    }
}
