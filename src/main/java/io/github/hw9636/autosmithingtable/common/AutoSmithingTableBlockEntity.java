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
package io.github.hw9636.autosmithingtable.common;

import io.github.hw9636.autosmithingtable.common.config.ASTConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoSmithingTableBlockEntity extends BlockEntity implements IEnergyStorage {

    @SuppressWarnings("unused")
    public static final int SIDE_NONE = 0;
    public static final int SIDE_INPUT1 = 1;
    public static final int SIDE_INPUT2 = 2;
    public static final int SIDE_OUTPUT = 3;

    public final ItemStackHandler baseSlots, additionSlots, outputSlots;
    public final ContainerData data;
    private final LazyOptional<ItemStackHandler> baseSlotsLazy;
    private final LazyOptional<ItemStackHandler> additionSlotsLazy;
    private final LazyOptional<ItemStackHandler> outputSlotsLazy;
    private int FEStored;
    private int progress;
    private boolean requiresUpdate, canInsertOutput, checkRecipe;
    private UpgradeRecipe currentRecipe;
    private int sidesConfig;

    public AutoSmithingTableBlockEntity(BlockPos pos, BlockState blockstate) {
        super(Registries.AUTO_SMITHING_TABLE_ENTITY_TYPE.get(), pos, blockstate);


        this.baseSlots = createInventory();
        this.additionSlots = createInventory();
        this.outputSlots = createOutputInventory();

        this.baseSlotsLazy = LazyOptional.of(() -> this.baseSlots);
        this.additionSlotsLazy = LazyOptional.of(() -> this.additionSlots);
        this.outputSlotsLazy = LazyOptional.of(() -> this.outputSlots);

        this.currentRecipe = null;

        this.requiresUpdate = false;
        this.canInsertOutput = false;
        this.checkRecipe = false;
        this.data = getData();

        this.FEStored = 0;
        this.progress = 0;

        this.sidesConfig = getDefaultSidesConfig();
    }

    private int getDefaultSidesConfig() {
        return (SIDE_INPUT1 << 10) | (SIDE_OUTPUT << 8) | (SIDE_INPUT2 << 6) | (SIDE_INPUT2 << 4) | (SIDE_INPUT2 << 2) | SIDE_INPUT2;
    }

    public static int getSide(int sidesConfig, Direction side) {
        return switch (side) {
            case UP -> (sidesConfig >> 10) & 3;
            case DOWN ->  (sidesConfig >> 8) & 3;
            case NORTH ->  (sidesConfig >> 6) & 3;
            case EAST -> (sidesConfig >> 4) & 3;
            case SOUTH -> (sidesConfig >> 2) & 3;
            case WEST -> sidesConfig & 3; // 3 = 111 (binary)
        };
    }

    private int getSide(Direction side) {
        return AutoSmithingTableBlockEntity.getSide(this.sidesConfig, side);
    }

    private boolean canInsert(ItemStack beforeInsert, ItemStack toInsert) {
        return beforeInsert.isEmpty() || (beforeInsert.is(toInsert.getItem()) && beforeInsert.getCount() + toInsert.getCount() <= beforeInsert.getMaxStackSize());
    }

    public void serverTick() {
        if (level == null || level.isClientSide) return; // Only server-side ticks

        if (this.checkRecipe) {
            currentRecipe = getRecipeFromStacks(getItemInSlot(baseSlotsLazy,0), getItemInSlot(additionSlotsLazy,0));
            this.checkRecipe = false;
        }

        if (this.currentRecipe != null && canInsert(getItemInSlot(outputSlotsLazy,0), this.currentRecipe.getResultItem())) {
            if (FEStored >= ASTConfig.COMMON.energyPerTick.get()) {
                FEStored -= ASTConfig.COMMON.energyPerTick.get();

                if (++progress == ASTConfig.COMMON.ticksPerCraft.get()) {
                    progress = 0;

                    ItemStack result = currentRecipe.getResultItem().copy();
                    result.setDamageValue(getItemInSlot(baseSlotsLazy, 0).getDamageValue());
                    if (insertItem(outputSlotsLazy,0, result).isEmpty()) {
                        getItemInSlot(baseSlotsLazy,0).shrink(1);
                        getItemInSlot(additionSlotsLazy,0).shrink(1);
                        currentRecipe = getRecipeFromStacks(getItemInSlot(baseSlotsLazy,0), getItemInSlot(additionSlotsLazy,0));
                    }
                }
            }
            else if (progress > 0) progress--;
        }

        if (requiresUpdate) {
            requiresUpdate = false;
            update();
        }
    }

    private UpgradeRecipe getRecipeFromStacks(ItemStack input1, ItemStack input2) {
        return level == null ? null : level.getRecipeManager().getRecipeFor(RecipeType.SMITHING,
                new SimpleContainer(input1, input2), level).orElse(null);
    }

    // Capabilities



    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction dir) {
        if (dir == null) return LazyOptional.empty();

        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return switch (this.getSide(dir)) {
                case SIDE_INPUT1 -> this.baseSlotsLazy.cast();
                case SIDE_INPUT2 -> this.additionSlotsLazy.cast();
                case SIDE_OUTPUT -> this.outputSlotsLazy.cast();
                default -> LazyOptional.empty();
            };
        }
        if (cap == CapabilityEnergy.ENERGY) return LazyOptional.of(() -> this).cast();

        return super.getCapability(cap, dir);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.baseSlotsLazy.invalidate();
        LazyOptional.of(() -> this).invalidate();
    }

    // Data Sync

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("InventoryBase", baseSlots.serializeNBT());
        tag.put("InventoryAddition", additionSlots.serializeNBT());
        tag.put("InventoryOutput", outputSlots.serializeNBT());

        tag.putInt("Sides", sidesConfig);
        tag.putInt("FEStored", FEStored);
        tag.putInt("Progress", progress);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return serializeNBT();
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);

        this.FEStored = tag.getInt("FEStored");
        this.progress = tag.getInt("Progress");

        this.baseSlots.deserializeNBT(tag.getCompound("InventoryBase"));
        this.additionSlots.deserializeNBT(tag.getCompound("InventoryAddition"));
        this.outputSlots.deserializeNBT(tag.getCompound("InventoryOutput"));

        this.sidesConfig = tag.getInt("Sides");

        this.checkRecipe = true; // Check recipe on next tick
    }

    // Util

    public static int setSide(Direction dir, int value, int sidesConfig) {
        return switch (dir) {
            case UP -> (sidesConfig & 0x3FF) | (value << 10); // 2 ^ 10
            case DOWN -> (sidesConfig & 0xCFF) | (value << 8); // 2 ^ 8
            case NORTH -> (sidesConfig & 0xF3F) | (value << 6); // 2 ^ 6
            case EAST -> (sidesConfig & 0xFCF) | (value << 4); // 2 ^ 4
            case SOUTH -> (sidesConfig & 0xFF3) | (value << 2);
            case WEST -> (sidesConfig & 0xFFC) | value;
        };
    }

    public void dropItems(double pX, double pY, double pZ) {
        if (level != null) {
            Containers.dropItemStack(level, pX, pY, pZ, this.getItemInSlot(baseSlotsLazy, 0));
            Containers.dropItemStack(level, pX, pY, pZ, this.getItemInSlot(additionSlotsLazy, 0));
            Containers.dropItemStack(level, pX, pY, pZ, this.getItemInSlot(outputSlotsLazy, 0));
        }
    }

    public void update() {
        requestModelDataUpdate();
        setChanged();

        if (this.level != null)
            this.level.setBlockAndUpdate(this.worldPosition, getBlockState());
    }

    private ContainerData getData() {
        return new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FEStored >> 16;
                    case 1 -> FEStored & 0xffff;
                    case 2 -> progress;
                    case 3 -> sidesConfig;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 2 -> progress = value;
                    case 3 -> sidesConfig = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    // Inventory Stuff

    public ItemStack getItemInSlot(LazyOptional<ItemStackHandler> slotsLazy, int slot) {
        return slotsLazy.map(inv -> inv.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    public ItemStack insertItem(LazyOptional<ItemStackHandler> slotsLazy, int slot, ItemStack stack) {
        this.requiresUpdate = true;
        if (slotsLazy == outputSlotsLazy) {
            this.canInsertOutput = true;
            ItemStack result = slotsLazy.map(inv -> inv.insertItem(slot, stack, false)).orElse(ItemStack.EMPTY);
            this.canInsertOutput = false;
            return result;
        }

        return slotsLazy.map(inv -> inv.insertItem(slot, stack, false)).orElse(ItemStack.EMPTY);
    }

    private ItemStackHandler createOutputInventory() {
        return new ItemStackHandler(1) {

            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!simulate) AutoSmithingTableBlockEntity.this.update();
                return super.extractItem(slot, amount, simulate);
            }
            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (canInsertOutput) {
                    if (!simulate) requiresUpdate = true;
                    return super.insertItem(slot,stack,simulate);
                }

                return stack;
            }
        };
    }

    private ItemStackHandler createInventory() {


        return new ItemStackHandler(1) {
            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!simulate) AutoSmithingTableBlockEntity.this.update();
                return super.extractItem(slot, amount, simulate);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (!simulate) requiresUpdate = true;
                return super.insertItem(slot, stack, simulate);
            }

            @Override
            protected void onContentsChanged(int slot) {
                currentRecipe = level == null ? null :getRecipeFromStacks(getItemInSlot(baseSlotsLazy,0), getItemInSlot(additionSlotsLazy,0));
                if (currentRecipe == null && progress > 0) progress = 0;
            }
        };
    }

    // Energy Capability

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int toReceive = Math.min(ASTConfig.COMMON.maxEnergyStored.get() - FEStored, maxReceive);
        if (!simulate) {
            this.FEStored += toReceive;
            this.requestModelDataUpdate();
        }

        return toReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return FEStored;
    }

    @Override
    public int getMaxEnergyStored() {
        return ASTConfig.COMMON.maxEnergyStored.get();
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return FEStored < ASTConfig.COMMON.maxEnergyStored.get();
    }
}