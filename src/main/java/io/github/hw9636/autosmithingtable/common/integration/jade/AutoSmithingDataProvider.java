package io.github.hw9636.autosmithingtable.common.integration.jade;

import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import io.github.hw9636.autosmithingtable.common.AutoSmithingTableBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.IServerDataProvider;

public class AutoSmithingDataProvider implements IServerDataProvider<BlockEntity> {



    private static final ResourceLocation ID = new ResourceLocation(AutoSmithingTableMod.MOD_ID, "jade_data_provider");
    @Override
    public ResourceLocation getUid() {
        return ID;
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, ServerPlayer serverPlayer, Level level, BlockEntity blockEntity, boolean b) {
        if (blockEntity instanceof AutoSmithingTableBlockEntity be) {
            compoundTag.putInt("progress", be.data.get(2));
        }
    }
}
