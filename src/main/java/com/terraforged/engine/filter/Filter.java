//
// Source code recreated from a .class file by Quiltflower
//

package com.terraforged.engine.filter;

import com.terraforged.engine.cell.Cell;

public interface Filter {
    void apply(Filterable var1, int var2, int var3, int var4);

    default void iterate(Filterable map, Filter.Visitor visitor) {
        for(int dz = 0; dz < map.getSize().total; ++dz) {
            for(int dx = 0; dx < map.getSize().total; ++dx) {
                Cell cell = map.getCellRaw(dx, dz);
                visitor.visit(map, cell, dx, dz);
            }
        }
    }

    public interface Visitor {
        void visit(Filterable var1, Cell var2, int var3, int var4);
    }
}
