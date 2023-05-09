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
package io.github.hw9636.autosmithingtable.common;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AutoSmithingContainer extends AbstractContainerMenu {

    public static final int INVENTORY_SLOTS_START = 0;
    public static final int INVENTORY_SLOTS_END = 26;

    public static final int HOTBAR_SLOTS_START = 27;
    public static final int HOTBAR_SLOTS_END = 35;

    public static final int BASE_SLOTS_START = 36;
    public static final int BASE_SLOTS_END = 36;

    public static final int ADDITION_SLOTS_START = 37;
    public static final int ADDITION_SLOTS_END = 37;

    public final ContainerLevelAccess containerAccess;
    public final ContainerData data;

    public AutoSmithingContainer(int id, Inventory playerInv) {
        this(id, playerInv, BlockPos.ZERO, new SimpleContainerData(4), new ItemStackHandler(1),
        new ItemStackHandler(1),new ItemStackHandler(1));
    }

    public AutoSmithingContainer(int id, Inventory playerInv, BlockPos pos, ContainerData data, IItemHandler baseSlots,
                                 IItemHandler additionSlots, IItemHandler outputSlots) {
        super(Registries.AUTO_SMITHING_CONTAINER.get(), id);

        this.containerAccess = ContainerLevelAccess.create(playerInv.player.level, pos);
        this.data = data;

        addDataSlots(data);

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInv, k, 8 + k * 18, 142));
        }

        this.addSlot(new SlotItemHandler(baseSlots, 0, 27, 47));
        this.addSlot(new SlotItemHandler(additionSlots,  0, 76, 47));
        this.addSlot(new SlotItemHandler(outputSlots,  0, 134, 47) {
            public boolean mayPlace(@NotNull ItemStack p_39818_) {
                return false;
            }
        });

    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.containerAccess, player, Registries.AUTO_SMITHING_TABLE.get());
    }

    public static MenuConstructor getServerContainer(AutoSmithingTableBlockEntity entity, BlockPos pos) {
        return (id, playerInv, player) -> new AutoSmithingContainer(id, playerInv, pos,
                new AutoSmithingContainerData(entity, 4), entity.baseSlots, entity.additionSlots,
                entity.outputSlots);
    }

    private void moveItemToContainer(ItemStack item) {
        Optional<Optional<UpgradeRecipe>> optionalOptionalRecipe = this.containerAccess.evaluate((level, pos) -> level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING).stream()
                    .filter((ur) -> ur.isAdditionIngredient(item)).findFirst());
        if (optionalOptionalRecipe.isPresent()) {
            Optional<UpgradeRecipe> recipeOptional = optionalOptionalRecipe.get();
            if (recipeOptional.isPresent()) {
                moveItemStackTo(item, ADDITION_SLOTS_START, ADDITION_SLOTS_END + 1, false);
                return;
            }
        }
        moveItemStackTo(item, BASE_SLOTS_START, BASE_SLOTS_END + 1, false);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack returnStack = ItemStack.EMPTY;

        final Slot slot = getSlot(index);
        if (slot.hasItem()) {
            final ItemStack item = slot.getItem();
            returnStack = item.copy();
            if (index <= 35) {
                // Check for slots in Container first
                moveItemToContainer(item);

                // Inventory slots
                if (index <= INVENTORY_SLOTS_END) {
                    if (!moveItemStackTo(item, HOTBAR_SLOTS_START, HOTBAR_SLOTS_END + 1, true))
                        return ItemStack.EMPTY;
                }
                else {
                    if (!moveItemStackTo(item, INVENTORY_SLOTS_START, INVENTORY_SLOTS_END + 1, false))
                        return ItemStack.EMPTY;
                }
            }
            else { // From Container
                if (!moveItemStackTo(item, INVENTORY_SLOTS_START, HOTBAR_SLOTS_END + 1, true))
                        return ItemStack.EMPTY;
            }

            if (item.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return returnStack;
    }
}
