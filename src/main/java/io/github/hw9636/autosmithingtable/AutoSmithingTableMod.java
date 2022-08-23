package io.github.hw9636.autosmithingtable;

import com.mojang.logging.LogUtils;
import io.github.hw9636.autosmithingtable.common.Registries;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(AutoSmithingTableMod.MOD_ID)
public class AutoSmithingTableMod
{
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "autosmithingtable";

    public AutoSmithingTableMod()
    {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        Registries.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
