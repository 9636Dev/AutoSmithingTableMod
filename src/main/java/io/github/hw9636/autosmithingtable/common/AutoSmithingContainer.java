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


        addDataSlots(data);

        this.addSlot(new SlotItemHandler(slots, AutoSmithingTableBlockEntity.INPUT_SLOT, 27, 47));
        this.addSlot(new SlotItemHandler(slots,  AutoSmithingTableBlockEntity.EXTRA_SLOT, 76, 47));
        this.addSlot(new SlotItemHandler(slots,  AutoSmithingTableBlockEntity.OUTPUT_SLOT, 134, 47) {
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
    public boolean stillValid(Player player) {
        return stillValid(this.containerAccess, player, Registries.AUTO_SMITHING_TABLE.get());
    }

    public static MenuConstructor getServerContainer(AutoSmithingTableBlockEntity entity, BlockPos pos) {
        return (id, playerInv, player) -> new AutoSmithingContainer(id, playerInv, entity.inventory, pos, new AutoSmithingContainerData(entity, 3));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;
        final Slot slot = getSlot(index);
        System.out.println(index);
        if (slot.hasItem()) {
            final ItemStack item = slot.getItem();
            returnStack = item.copy();
            if (index < 27) {
                if (!moveItemStackTo(item, 27, this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!moveItemStackTo(item, 0, 27, false))
                return ItemStack.EMPTY;

            if (item.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return returnStack;
    }
}
