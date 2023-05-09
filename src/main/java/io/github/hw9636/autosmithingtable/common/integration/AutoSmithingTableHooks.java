/*
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
package io.github.hw9636.autosmithingtable.common.integration;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

@SuppressWarnings("unused")
public class AutoSmithingTableHooks {
    public boolean TOPLoaded;

    public AutoSmithingTableHooks() {
        TOPLoaded = false;
    }

    public void commonSetup(final FMLCommonSetupEvent event) {
        TOPLoaded = ModList.get().isLoaded("theoneprobe");
    }

    public void enqueueIMC(final InterModEnqueueEvent event) {
        if (TOPLoaded)
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", AutoSmithingBlockInfoProvider::new);
    }
}
