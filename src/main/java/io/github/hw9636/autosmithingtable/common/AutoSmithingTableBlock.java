package io.github.hw9636.autosmithingtable.common;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoSmithingTableBlock extends Block implements EntityBlock {

    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.autosmithingtable.title");

    public AutoSmithingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, Level level, BlockPos pos, @NotNull Player player, InteractionHand pHand, BlockHitResult pHit) {

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AutoSmithingTableBlockEntity) {
            final AutoSmithingTableBlockEntity be = (AutoSmithingTableBlockEntity) level.getBlockEntity(pos);
            final MenuProvider menu = new SimpleMenuProvider(AutoSmithingContainer.getServerContainer(be, pos), CONTAINER_TITLE);
            NetworkHooks.openGui((ServerPlayer) player, menu, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean pIsMoving) {
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            level.getBlockEntity(pos).getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent((inv) -> {
                for (int i = 0;i<inv.getSlots();i++)
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), inv.getStackInSlot(i));
            });
        }

        super.onRemove(state, level, pos, newState, pIsMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null :
                (level, pos, state0, blockentity) -> ((AutoSmithingTableBlockEntity) blockentity).serverTick();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutoSmithingTableBlockEntity(pos, state);
    }
}
