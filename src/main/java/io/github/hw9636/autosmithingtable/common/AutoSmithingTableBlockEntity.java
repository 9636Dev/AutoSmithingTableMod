package io.github.hw9636.autosmithingtable.common;

import io.github.hw9636.autosmithingtable.common.config.ASTConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoSmithingTableBlockEntity extends BlockEntity implements IEnergyStorage {
    public final ItemStackHandler baseSlots, additionSlots, outputSlots;
    public final ContainerData data;
    private final LazyOptional<ItemStackHandler> baseSlotsLazy;
    private final LazyOptional<ItemStackHandler> additionSlotsLazy;
    private final LazyOptional<ItemStackHandler> outputSlotsLazy;
    private int FEStored;
    private int progress;
    private boolean requiresUpdate, canInsertOutput, checkRecipe;
    private UpgradeRecipe currentRecipe;

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
    }

    private boolean canInsert(ItemStack beforeInsert, ItemStack toInsert) {
        return beforeInsert.isEmpty() || (beforeInsert.is(toInsert.getItem()) && beforeInsert.getCount() + toInsert.getCount() <= beforeInsert.getMaxStackSize());
    }

    public void serverTick() {
        if (level == null || level.isClientSide) return; // Only server-side ticks

        if (this.checkRecipe) {
            currentRecipe =  UpgradeRecipeHelper.fromItemStacks(level,
                    AutoSmithingTableBlockEntity.this.getItemInSlot(baseSlotsLazy, 0),
                    AutoSmithingTableBlockEntity.this.getItemInSlot(additionSlotsLazy, 0));
            this.checkRecipe = false;
        }

        if (this.currentRecipe != null && canInsert(getItemInSlot(outputSlotsLazy,0), this.currentRecipe.getResultItem())) {
            if (FEStored >= ASTConfig.COMMON.energyPerTick.get()) {
                FEStored -= ASTConfig.COMMON.energyPerTick.get();

                if (++progress == ASTConfig.COMMON.ticksPerCraft.get()) {
                    progress = 0;

                    if (insertItem(outputSlotsLazy,0, currentRecipe.getResultItem().copy()).isEmpty()) {
                        getItemInSlot(baseSlotsLazy,0).shrink(1);
                        getItemInSlot(additionSlotsLazy,0).shrink(1);
                        currentRecipe = UpgradeRecipeHelper.fromItemStacks(level, getItemInSlot(baseSlotsLazy,0), getItemInSlot(additionSlotsLazy,0));
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

    // Capabilities


    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction dir) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && dir != null) {
            return switch (dir) {
                case DOWN -> this.outputSlotsLazy.cast();
                case UP -> this.baseSlotsLazy.cast();

                case NORTH, EAST, WEST, SOUTH -> this.additionSlotsLazy.cast();
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
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("InventoryBase", baseSlots.serializeNBT());
        tag.put("InventoryAddition", additionSlots.serializeNBT());
        tag.put("InventoryOutput", outputSlots.serializeNBT());

        tag.putInt("FEStored", FEStored);
        tag.putInt("Progress", progress);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
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
    public void load(CompoundTag tag) {
        super.load(tag);

        this.FEStored = tag.getInt("FEStored");
        this.progress = tag.getInt("Progress");

        this.baseSlots.deserializeNBT(tag.getCompound("InventoryBase"));
        this.additionSlots.deserializeNBT(tag.getCompound("InventoryAddition"));
        this.outputSlots.deserializeNBT(tag.getCompound("InventoryOutput"));

        this.checkRecipe = true; // Check recipe on next tick
    }

    // Util

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
                    case 0 -> FEStored;
                    case 1 -> progress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> FEStored = value;
                    case 1 -> progress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    // Inventory Stuff
    public void setItemInSlot(LazyOptional<ItemStackHandler> slotsLazy, int slot, ItemStack item) {
        slotsLazy.ifPresent(inv -> inv.setStackInSlot(slot, item));
    }

    public ItemStack getItemInSlot(LazyOptional<ItemStackHandler> slotsLazy, int slot) {
        return slotsLazy.map(inv -> inv.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    public ItemStack extractItem(LazyOptional<ItemStackHandler> slotsLazy, int slot) {
        final int count = getItemInSlot(slotsLazy, slot).getCount();
        this.requiresUpdate = true;
        return slotsLazy.map(inv -> inv.extractItem(slot, count, false)).orElse(ItemStack.EMPTY);
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
                currentRecipe = level == null ? null : UpgradeRecipeHelper.fromItemStacks(level,
                        AutoSmithingTableBlockEntity.this.getItemInSlot(baseSlotsLazy, 0),
                        AutoSmithingTableBlockEntity.this.getItemInSlot(additionSlotsLazy, 0));
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
        return maxExtract;
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
