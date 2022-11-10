package io.github.hw9636.autosmithingtable.common.integration.jade;

import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class AutoSmithingComponentProvider implements IBlockComponentProvider {
    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        iTooltip.append(new TranslatableComponent("tooltip.autosmithingtable.jade_progress", blockAccessor.getServerData().getInt("progress")));
    }

    private static final ResourceLocation ID = new ResourceLocation(AutoSmithingTableMod.MOD_ID, "jade_component_provider");

    @Override
    public ResourceLocation getUid() {
        return ID;
    }
}
