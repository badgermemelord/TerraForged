// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.serial;

import com.terraforged.cereal.CerealSpec;
import com.terraforged.cereal.value.DataValue;

import java.io.IOException;
import java.io.Writer;

public class DataWriter implements AutoCloseable
{
    private final Writer writer;
    private final CerealSpec spec;
    private int indents;
    private boolean newLine;
    
    public DataWriter(final Writer writer) {
        this(writer, CerealSpec.STANDARD);
    }
    
    public DataWriter(final Writer writer, final CerealSpec spec) {
        this.indents = 0;
        this.newLine = false;
        this.writer = writer;
        this.spec = spec;
    }
    
    public void write(final DataValue value) throws IOException {
        value.appendTo(this);
    }
    
    public DataWriter beginObj() throws IOException {
        this.newLine();
        this.append('{');
        this.newLine = true;
        ++this.indents;
        return this;
    }
    
    public DataWriter endObj() throws IOException {
        --this.indents;
        this.newLine();
        this.append('}');
        this.newLine = true;
        return this;
    }
    
    public DataWriter beginList() throws IOException {
        this.newLine();
        this.append('[');
        this.newLine = true;
        ++this.indents;
        return this;
    }
    
    public DataWriter endList() throws IOException {
        --this.indents;
        this.newLine();
        this.append(']');
        this.newLine = true;
        return this;
    }
    
    public DataWriter name(final String name) throws IOException {
        this.newLine();
        this.append(name);
        this.append(this.spec.delimiter);
        this.append(this.spec.separator);
        return this;
    }
    
    public DataWriter type(final String name) throws IOException {
        if (!name.isEmpty()) {
            this.newLine();
            this.append(name);
            this.append(this.spec.separator);
        }
        return this;
    }
    
    public DataWriter value(final Object value) throws IOException {
        if (value instanceof String && escape(value.toString())) {
            this.append(this.spec.escapeChar);
            this.append(value.toString());
            this.append(this.spec.escapeChar);
        }
        else {
            this.append(value.toString());
        }
        this.newLine = true;
        return this;
    }
    
    public DataWriter value(final DataValue value) throws IOException {
        value.appendTo(this);
        return this;
    }
    
    private void append(final char c) throws IOException {
        if (c != '\0') {
            this.writer.append(c);
        }
    }
    
    private void append(final String string) throws IOException {
        if (string.length() > 0) {
            this.writer.append((CharSequence)string);
        }
    }
    
    private void newLine() throws IOException {
        if (this.newLine && !this.spec.indent.isEmpty()) {
            this.append('\n');
            this.newLine = false;
            this.indent();
        }
    }
    
    private void indent() throws IOException {
        if (!this.spec.indent.isEmpty()) {
            for (int i = 0; i < this.indents; ++i) {
                this.append(this.spec.indent);
            }
        }
    }
    
    private static boolean escape(final String in) {
        for (int i = 0; i < in.length(); ++i) {
            final char c = in.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void close() throws Exception {
        this.writer.close();
    }
}
