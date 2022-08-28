package io.github.hw9636.autosmithingtable;

import com.mojang.logging.LogUtils;
import io.github.hw9636.autosmithingtable.client.AutoSmithingTableScreen;
import io.github.hw9636.autosmithingtable.common.Registries;
import io.github.hw9636.autosmithingtable.common.config.ASTConfig;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register Config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ASTConfig.COMMON_SPEC);

        Registries.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);


    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        MenuScreens.register(Registries.AUTO_SMITHING_CONTAINER.get(), AutoSmithingTableScreen::new);
    }

    private void setup(final FMLCommonSetupEvent event)
    {

        // some preinit code
        LOGGER.info("HELLO FROM PREINIT");
        LOGGER.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());
    }
}
