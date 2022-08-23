package io.github.hw9636.autosmithingtable.common;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class AutoSmithingTableBlock extends Block implements EntityBlock {

    private static final Component CONTAINER_TITLE = new TranslatableComponent("container.autosmithingtable.title");

    public AutoSmithingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(BlockState pState, Level level, BlockPos pos, Player player, InteractionHand pHand, BlockHitResult pHit) {

        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AutoSmithingTableEntity) {
            final AutoSmithingTableEntity be = (AutoSmithingTableEntity) level.getBlockEntity(pos);
            final MenuProvider menu = new SimpleMenuProvider(AutoSmithingContainer.getServerContainer(be, pos), CONTAINER_TITLE);
            NetworkHooks.openGui((ServerPlayer) player, menu, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return pLevel.isClientSide ? null :
                (level, pos, state0, blockentity) -> ((AutoSmithingTableEntity) blockentity).serverTick();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AutoSmithingTableEntity(pos, state);
    }
}
