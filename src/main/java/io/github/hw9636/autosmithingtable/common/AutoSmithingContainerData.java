/*
    Copyright (C) 2023 9636Dev
    This file is part of 9636Dev's AutoSmithingTableMod.

    AutoSmithingTableMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AutoSmithingTableMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with AutoSmithingTableMod.  If not, see <https://www.gnu.org/licenses/>.
*/
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
}
