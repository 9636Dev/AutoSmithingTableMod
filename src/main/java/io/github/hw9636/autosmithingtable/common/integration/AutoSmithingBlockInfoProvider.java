package io.github.hw9636.autosmithingtable.common.integration;

import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import io.github.hw9636.autosmithingtable.common.AutoSmithingTableBlockEntity;
import io.github.hw9636.autosmithingtable.common.Registries;
import io.github.hw9636.autosmithingtable.common.config.ASTConfig;
import mcjty.theoneprobe.api.*;
import mcjty.theoneprobe.apiimpl.styles.ProgressStyle;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;

public class AutoSmithingBlockInfoProvider implements IProbeInfoProvider, Function<ITheOneProbe, Void> {

    @Override
    public ResourceLocation getID() {
        return new ResourceLocation(AutoSmithingTableMod.MOD_ID,"topprovider");
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
        if (blockState.is(Registries.AUTO_SMITHING_TABLE.get()) && blockState.hasBlockEntity() && level.getBlockEntity(iProbeHitData.getPos()) instanceof AutoSmithingTableBlockEntity be)
        {
            ProgressStyle progressStyle = new ProgressStyle();
            progressStyle.showText(false);
            iProbeInfo.progress(be.data.get(2), ASTConfig.COMMON.ticksPerCraft.get(), progressStyle);
        }

    }

    @Override
    public Void apply(ITheOneProbe top) {
        top.registerProvider(this);
        return null;
    }
}
