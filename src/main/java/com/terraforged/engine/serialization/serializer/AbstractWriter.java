// 
// Decompiled by Procyon v0.5.36
// 

package com.terraforged.engine.serialization.serializer;

public abstract class AbstractWriter<T, O, A, S extends AbstractWriter<T, ?, ?, ?>> implements Writer
{
    private final Context root;
    private String name;
    private Context context;
    
    public AbstractWriter() {
        this.root = new Context((Context)null);
        this.name = "";
        this.context = this.root;
    }
    
    public T get() {
        return (T)this.root.build();
    }
    
    @Override
    public S name(final String name) {
        this.name = name;
        return this.self();
    }
    
    @Override
    public S beginObject() {
        return this.begin(this.createObject(), null);
    }
    
    @Override
    public S endObject() {
        return this.end();
    }
    
    @Override
    public S beginArray() {
        return this.begin(null, this.createArray());
    }
    
    @Override
    public S endArray() {
        return this.end();
    }
    
    @Override
    public S value(final String value) {
        return this.append(this.create(value));
    }
    
    @Override
    public S value(final float value) {
        return this.append(this.create(value));
    }
    
    @Override
    public S value(final int value) {
        return this.append(this.create(value));
    }
    
    @Override
    public S value(final boolean value) {
        return this.append(this.create(value));
    }
    
    private S begin(final O object, final A array) {
        if (this.root.isPresent()) {
            (this.context = new Context(this.context)).set(this.name, object, array);
        }
        else {
            this.root.set(this.name, object, array);
        }
        return this.self();
    }
    
    private S end() {
        if (this.context != this.root && this.context.isPresent()) {
            final String name = this.context.name;
            final T value = (T)this.context.build();
            this.context = this.context.parent;
            this.append(name, value);
        }
        return this.self();
    }
    
    private S append(final T value) {
        return this.append(this.name, value);
    }
    
    private S append(final String name, final T value) {
        if (this.context.objectValue != null) {
            this.add(this.context.objectValue, name, value);
        }
        else if (this.context.arrayValue != null) {
            this.add(this.context.arrayValue, value);
        }
        return this.self();
    }
    
    protected abstract S self();
    
    protected abstract boolean isObject(final T p0);
    
    protected abstract boolean isArray(final T p0);
    
    protected abstract void add(final O p0, final String p1, final T p2);
    
    protected abstract void add(final A p0, final T p1);
    
    protected abstract O createObject();
    
    protected abstract A createArray();
    
    protected abstract T closeObject(final O p0);
    
    protected abstract T closeArray(final A p0);
    
    protected abstract T create(final String p0);
    
    protected abstract T create(final int p0);
    
    protected abstract T create(final float p0);
    
    protected abstract T create(final boolean p0);
    
    private class Context
    {
        private final Context parent;
        private String name;
        private O objectValue;
        private A arrayValue;
        
        private Context(final Context root) {
            this.parent = ((root != null) ? root : this);
        }
        
        private T build() {
            if (this.objectValue != null) {
                return AbstractWriter.this.closeObject(this.objectValue);
            }
            if (this.arrayValue != null) {
                return AbstractWriter.this.closeArray(this.arrayValue);
            }
            return null;
        }
        
        private void set(final String n, final O o, final A a) {
            this.name = n;
            this.objectValue = o;
            this.arrayValue = a;
        }
        
        private boolean isPresent() {
            return this.objectValue != null || this.arrayValue != null;
        }
    }
}
