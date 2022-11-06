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
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", AutoSmithingBlockInfoProvider::new);
    }
}
