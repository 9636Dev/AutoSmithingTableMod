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
package io.github.hw9636.autosmithingtable.common.integration.jei;

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
