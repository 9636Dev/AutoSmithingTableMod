package io.github.hw9636.autosmithingtable.common;

import net.minecraft.world.inventory.SimpleContainerData;

public class AutoSmithingContainerData extends SimpleContainerData {
    private final AutoSmithingTableBlockEntity be;

    public AutoSmithingContainerData(AutoSmithingTableBlockEntity be, int size) {
        super(size);
        this.be = be;
    }

    @Override
    public int get(int pIndex) {
        return this.be.data.get(pIndex);
    }

    @Override
    public void set(int pIndex, int pValue) {
        this.be.data.set(pIndex, pValue);
    }
}
