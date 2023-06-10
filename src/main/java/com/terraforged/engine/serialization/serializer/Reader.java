//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.serialization.serializer;

import java.util.Collection;

public interface Reader {
    int getSize();

    Reader getChild(String var1);

    Reader getChild(int var1);

    Collection<String> getKeys();

    String getString();

    boolean getBool();

    float getFloat();

    int getInt();

    default boolean has(String key) {
        return this.getKeys().contains(key);
    }

    default String getString(String key) {
        return this.getChild(key).getString();
    }

    default boolean getBool(String key) {
        return this.getChild(key).getBool();
    }

    default float getFloat(String key) {
        return this.getChild(key).getFloat();
    }

    default int getInt(String key) {
        return this.getChild(key).getInt();
    }

    default String getString(int index) {
        return this.getChild(index).getString();
    }

    default boolean getBool(int index) {
        return this.getChild(index).getBool();
    }

    default float getFloat(int index) {
        return this.getChild(index).getFloat();
    }

    default int getInt(int index) {
        return this.getChild(index).getInt();
    }

    default boolean writeTo(Object object) throws Throwable {
        return Deserializer.deserialize(this, object);
    }
}
