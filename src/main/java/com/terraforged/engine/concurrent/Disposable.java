//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.terraforged.engine.concurrent;

public interface Disposable {
    void dispose();

    public interface Listener<T> {
        void onDispose(T var1);
    }
}
