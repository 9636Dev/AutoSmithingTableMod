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
import net.minecraft.world.item.crafting.Ingredient;
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
    public static final int INVENTORY_SLOTS = 3;
    public static final int INPUT_SLOT = 0;
    public static final int EXTRA_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public final ItemStackHandler inventory;
    public final ContainerData data;
    private LazyOptional<ItemStackHandler> inventoryLazy;
    private int FEStored;
    private int progress;
    private boolean requiresUpdate;
    private UpgradeRecipeIngredients currentRecipe;

    public AutoSmithingTableBlockEntity(BlockPos pos, BlockState blockstate) {
        super(Registries.AUTO_SMITHING_TABLE_ENTITY_TYPE.get(), pos, blockstate);


        this.inventory = createInventory(INVENTORY_SLOTS);
        this.inventoryLazy = LazyOptional.of(() -> this.inventory);
        this.currentRecipe = null;

        this.requiresUpdate = false;
        this.data = getData();

        this.FEStored = 0;
        this.progress = 0;
    }

    private boolean matchRecipe(Ingredient base, Ingredient addition) {
        return base.test(getItemInSlot(0)) && addition.test(getItemInSlot(1));
    }

    private boolean canInsert(ItemStack beforeInsert, ItemStack toInsert) {
        return beforeInsert.isEmpty() || (beforeInsert.is(toInsert.getItem()) && beforeInsert.getCount() + toInsert.getCount() <= beforeInsert.getMaxStackSize());
    }

    public void serverTick() {
        if (level == null || level.isClientSide) return;

        if (this.currentRecipe != null && canInsert(getItemInSlot(2), this.currentRecipe.getResult())) {
            if (FEStored >= ASTConfig.COMMON.energyPerTick.get()) {
                FEStored -= ASTConfig.COMMON.energyPerTick.get();

                if (++progress == ASTConfig.COMMON.ticksPerCraft.get()) {
                    progress = 0;

                    if (insertItem(-2, currentRecipe.getResult().copy()).isEmpty()) {
                        getItemInSlot(0).shrink(1);
                        getItemInSlot(1).shrink(1);
                        currentRecipe = UpgradeRecipeIngredients.fromItemstacks(level, getItemInSlot(0), getItemInSlot(1));
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
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return this.inventoryLazy.cast();
        if (cap == CapabilityEnergy.ENERGY) return LazyOptional.of(() -> this).cast();

        return super.getCapability(cap, dir);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inventoryLazy.invalidate();
        LazyOptional.of(() -> this).invalidate();
    }

    // Data Sync

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);

        tag.put("Inventory", inventory.serializeNBT());
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
        this.inventory.deserializeNBT(tag.getCompound("Inventory"));
        this.progress = tag.getInt("Progress");
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
    public void setItemInSlot(int slot, ItemStack item) {
        this.inventoryLazy.ifPresent(inv -> inv.setStackInSlot(slot, item));
    }

    public ItemStack getItemInSlot(int slot) {
        return this.inventoryLazy.map(inv -> inv.getStackInSlot(slot)).orElse(ItemStack.EMPTY);
    }

    public ItemStack extractItem(int slot) {
        final int count = getItemInSlot(slot).getCount();
        this.requiresUpdate = true;
        return this.inventoryLazy.map(inv -> inv.extractItem(slot, count, false)).orElse(ItemStack.EMPTY);
    }

    public ItemStack insertItem(int slot, ItemStack stack) {
        this.requiresUpdate = true;
        return this.inventoryLazy.map(inv -> inv.insertItem(slot, stack, false)).orElse(ItemStack.EMPTY);
    }

    private ItemStackHandler createInventory(int size) {
        return new ItemStackHandler(size) {
            @NotNull
            @Override
            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (!simulate) AutoSmithingTableBlockEntity.this.update();
                return super.extractItem(slot, amount, simulate);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (slot == -2) return super.insertItem(OUTPUT_SLOT,stack,simulate);
                if (slot == OUTPUT_SLOT) return stack;
                else {
                    if (!simulate) requiresUpdate = true;
                    return super.insertItem(slot, stack, simulate);
                }
            }

            @Override
            protected void onContentsChanged(int slot) {
                currentRecipe = level == null ? null : UpgradeRecipeIngredients.fromItemstacks(level, getStackInSlot(0), getStackInSlot(1));
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
