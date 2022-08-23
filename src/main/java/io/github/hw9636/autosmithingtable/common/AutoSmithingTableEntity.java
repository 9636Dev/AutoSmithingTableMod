package io.github.hw9636.autosmithingtable.common;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AutoSmithingTableEntity extends BlockEntity implements IEnergyStorage {

    public static final Logger logger = LogManager.getLogger();
    public static final int MAX_ENERGY_STORED = 100_000;
    public static final int INVENTORY_SLOTS = 3;
    public static final int INPUT_SLOT = 0;
    public static final int EXTRA_SLOT = 1;
    public static final int OUTPUT_SLOT = 2;
    public final ItemStackHandler inventory;
    public final ContainerData data;
    private LazyOptional<ItemStackHandler> inventoryLazy;
    private int FEStored;

    private boolean requiresUpdate;
    public AutoSmithingTableEntity(BlockPos pos, BlockState blockstate) {
        super(Registries.AUTO_SMITHING_TABLE_ENTITY_TYPE.get(), pos, blockstate);

        this.inventory = createInventory(INVENTORY_SLOTS);
        this.inventoryLazy = LazyOptional.of(() -> this.inventory);
        this.requiresUpdate = false;
        this.data = getData();
    }

    public void serverTick() {

//        assert this.level != null;
//        this.level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING).stream().anyMatch((upgradeRecipe -> {
//            logger.info(upgradeRecipe.getIngredients().get(0));
//            return false;
//        }));


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
                if (index == 0) return FEStored;
                return 0;
            }

            @Override
            public void set(int index, int value) {
                if (index == 0) FEStored = value;
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
    }

    // Inventory Stuff

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
                if (slot != OUTPUT_SLOT) return ItemStack.EMPTY;
                if (!simulate) AutoSmithingTableEntity.this.update();
                return super.extractItem(slot, amount, simulate);
            }

            @NotNull
            @Override
            public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                if (slot == OUTPUT_SLOT) return stack; // No inserting
                if (!simulate) AutoSmithingTableEntity.this.update();
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    // Energy Capability

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        // 100,000 - 90000, 20000 = 10000
        int received = Math.min(MAX_ENERGY_STORED - FEStored, maxReceive);
        if (!simulate)
            this.FEStored += received;
        return maxReceive - received;
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
        return MAX_ENERGY_STORED;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return FEStored < MAX_ENERGY_STORED;
    }
}
