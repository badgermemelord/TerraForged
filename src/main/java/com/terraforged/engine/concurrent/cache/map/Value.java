//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.concurrent.cache.map;

public class Value {
    public final int id;

    public Value(int id) {
        this.id = id;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Value value = (Value)o;
            return this.id == value.id;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return this.id;
    }
}
