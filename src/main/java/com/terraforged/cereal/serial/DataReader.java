// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.cereal.serial;

import com.terraforged.cereal.CerealSpec;
import com.terraforged.cereal.value.DataList;
import com.terraforged.cereal.value.DataObject;
import com.terraforged.cereal.value.DataValue;

import java.io.IOException;
import java.io.Reader;

public class DataReader implements AutoCloseable
{
    private static final char NONE = '\uffff';
    private final Reader reader;
    private final CerealSpec spec;
    private final DataBuffer buffer;
    private char c;
    
    public DataReader(final Reader reader) {
        this(reader, CerealSpec.STANDARD);
    }
    
    public DataReader(final Reader reader, final CerealSpec spec) {
        this.buffer = new DataBuffer();
        this.c = '\uffff';
        this.reader = reader;
        this.spec = spec;
    }
    
    private boolean next() throws IOException {
        final int i = this.reader.read();
        if (i == -1) {
            return false;
        }
        this.c = (char)i;
        return true;
    }
    
    private void skipSpace() throws IOException {
        while (Character.isWhitespace(this.c)) {
            if (!this.next()) {
                throw new IOException("Unexpected end");
            }
        }
    }
    
    public DataValue read() throws IOException {
        if (this.next()) {
            return this.readValue();
        }
        return DataValue.NULL;
    }
    
    private DataValue readValue() throws IOException {
        this.skipSpace();
        if (this.c == '{' && this.next()) {
            return this.readObject("");
        }
        if (this.c == '[' && this.next()) {
            return this.readList();
        }
        final Object value = this.readPrimitive();
        if (value instanceof String) {
            this.skipSpace();
            if (this.c == '{' && this.next()) {
                return this.readObject(value.toString());
            }
        }
        return DataValue.of(value);
    }
    
    private DataValue readObject(final String type) throws IOException {
        final DataObject data = new DataObject(type);
        while (true) {
            this.skipSpace();
            if (this.c == '}') {
                break;
            }
            final String key = this.readKey();
            final DataValue value = this.readValue();
            data.add(key, value);
        }
        this.next();
        return data;
    }
    
    private DataValue readList() throws IOException {
        final DataList list = new DataList();
        while (true) {
            this.skipSpace();
            if (this.c == ']') {
                break;
            }
            list.add(this.readValue());
        }
        this.next();
        return list;
    }
    
    private String readKey() throws IOException {
        this.skipSpace();
        this.buffer.reset();
        this.buffer.append(this.c);
        while (this.next()) {
            if (!Character.isLetterOrDigit(this.c) && this.c != '_') {
                if (this.c == ':') {
                    this.next();
                }
                return this.buffer.toString();
            }
            this.buffer.append(this.c);
        }
        throw new IOException("Unexpected end: " + this.buffer.toString());
    }
    
    private Object readPrimitive() throws IOException {
        if (this.c == this.spec.escapeChar) {
            return this.readEscapedString();
        }
        this.buffer.reset();
        this.buffer.append(this.c);
        while (this.next()) {
            if (!Character.isLetterOrDigit(this.c) && this.c != '.' && this.c != '-' && this.c != '_') {
                return this.buffer.getValue();
            }
            this.buffer.append(this.c);
        }
        throw new IOException("Unexpected end of string: " + this.buffer.toString());
    }
    
    private String readEscapedString() throws IOException {
        this.buffer.reset();
        while (this.next()) {
            if (this.c == this.spec.escapeChar) {
                this.next();
                return this.buffer.toString();
            }
            this.buffer.append(this.c);
        }
        throw new IOException("Unexpected end of string: " + this.buffer.toString());
    }
    
    @Override
    public void close() throws Exception {
        this.reader.close();
    }
}
