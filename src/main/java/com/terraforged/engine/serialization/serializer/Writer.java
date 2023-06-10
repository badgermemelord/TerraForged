//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.serialization.serializer;

public interface Writer {
    Writer name(String var1);

    Writer beginObject();

    Writer endObject();

    Writer beginArray();

    Writer endArray();

    Writer value(String var1);

    Writer value(float var1);

    Writer value(int var1);

    Writer value(boolean var1);

    default void readFrom(Object value) throws IllegalAccessException {
        new Serializer();
        Serializer.serialize(value, this);
    }
}
