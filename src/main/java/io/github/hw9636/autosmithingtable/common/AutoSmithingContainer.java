package io.github.hw9636.autosmithingtable.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

public class AutoSmithingContainer extends AbstractContainerMenu {

    private final ContainerLevelAccess containerAccess;
    public final ContainerData data;

    public AutoSmithingContainer(int id, Inventory playerInv) {
        this(id, playerInv, new ItemStackHandler(3), BlockPos.ZERO, new SimpleContainerData(3));
    }

    public AutoSmithingContainer(int id, Inventory playerInv, IItemHandler slots, BlockPos pos, ContainerData data) {
        super(Registries.AUTO_SMITHING_CONTAINER.get(), id);

        this.containerAccess = ContainerLevelAccess.create(playerInv.player.level, pos);
        this.data = data;

        this.addSlot(new SlotItemHandler(slots, AutoSmithingTableEntity.INPUT_SLOT, 27, 47));
        this.addSlot(new SlotItemHandler(slots,  AutoSmithingTableEntity.EXTRA_SLOT, 76, 47));
        this.addSlot(new SlotItemHandler(slots,  AutoSmithingTableEntity.OUTPUT_SLOT, 134, 47) {
            public boolean mayPlace(ItemStack p_39818_) {
                return false;
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return false;
    }

    public static MenuConstructor getServerContainer(AutoSmithingTableEntity entity, BlockPos pos) {
        return (id, playerInv, player) -> new AutoSmithingContainer(id, playerInv, entity.inventory, pos, new AutoSmithingContainerData(entity, 3));
    }
}
