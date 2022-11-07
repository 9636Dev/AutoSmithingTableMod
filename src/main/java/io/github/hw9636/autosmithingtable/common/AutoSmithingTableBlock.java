package io.github.hw9636.autosmithingtable.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoSmithingTableBlock extends Block implements EntityBlock {

    private static final Component CONTAINER_TITLE = Component.translatable("container.autosmithingtable.title");

    public AutoSmithingTableBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState pState, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand pHand, @NotNull BlockHitResult pHit) {


        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AutoSmithingTableBlockEntity be) {

            ItemStack itemStack = player.getItemInHand(pHand);

            if (itemStack.isEmpty() && player.isCrouching()) {
                int sidesConfig = be.data.get(3);
                Direction direction = pHit.getDirection();
                int value = AutoSmithingTableBlockEntity.getSide(sidesConfig, direction);

                if (value < AutoSmithingTableBlockEntity.SIDE_OUTPUT) value++;
                else value = 0;

                be.data.set(3, AutoSmithingTableBlockEntity.setSide(direction, value, sidesConfig));
                player.sendSystemMessage(Component.translatable("message.autosmithingtable.change_side_to_" + value));

                return InteractionResult.SUCCESS;
            }

            final MenuProvider menu = new SimpleMenuProvider(AutoSmithingContainer.getServerContainer(be, pos), CONTAINER_TITLE);
            NetworkHooks.openScreen((ServerPlayer) player, menu, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean pIsMoving) {
        if (state.hasBlockEntity() && state.getBlock() != newState.getBlock()) {
            if (level.getBlockEntity(pos) instanceof AutoSmithingTableBlockEntity be) {
                be.dropItems(pos.getX(), pos.getY(), pos.getZ());
            }
        }

        super.onRemove(state, level, pos, newState, pIsMoving);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, @NotNull BlockState pState, @NotNull BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null :
                (level, pos, state0, blockentity) -> ((AutoSmithingTableBlockEntity) blockentity).serverTick();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new AutoSmithingTableBlockEntity(pos, state);
    }
}
