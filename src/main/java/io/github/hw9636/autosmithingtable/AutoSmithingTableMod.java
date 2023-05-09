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
package io.github.hw9636.autosmithingtable;

import io.github.hw9636.autosmithingtable.client.AutoSmithingTableScreen;
import io.github.hw9636.autosmithingtable.common.Registries;
import io.github.hw9636.autosmithingtable.common.config.AutoSmithingTableConfig;
import io.github.hw9636.autosmithingtable.common.integration.AutoSmithingTableHooks;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AutoSmithingTableMod.MOD_ID)
public class AutoSmithingTableMod
{
    public static final String MOD_ID = "autosmithingtable";

    private final AutoSmithingTableHooks hooks;

    public AutoSmithingTableMod()
    {
        this.hooks = new AutoSmithingTableHooks();

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AutoSmithingTableConfig.COMMON_SPEC);

        Registries.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.BLOCK_ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        Registries.CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
        MenuScreens.register(Registries.AUTO_SMITHING_CONTAINER.get(), AutoSmithingTableScreen::new);    }

    private void setup(final FMLCommonSetupEvent event)
    {
        hooks.commonSetup(event);
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        hooks.enqueueIMC(event);
    }
}
