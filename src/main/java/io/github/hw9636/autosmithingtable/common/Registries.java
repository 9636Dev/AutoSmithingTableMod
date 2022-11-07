package io.github.hw9636.autosmithingtable.common;

import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

public class Registries {

    public static final CreativeModeTab TAB = new CreativeModeTab("autosmithingtable") {
        @Override
        public @NotNull ItemStack makeIcon() {
            return AUTO_SMITHING_TABLE_ITEM.get().getDefaultInstance();
        }
    };
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, AutoSmithingTableMod.MOD_ID);

    public static final RegistryObject<Block> AUTO_SMITHING_TABLE = BLOCKS.register("auto_smithing_table",
            () -> new AutoSmithingTableBlock(BlockBehaviour.Properties.of(Material.METAL).strength(1.5f).requiresCorrectToolForDrops()));

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AutoSmithingTableMod.MOD_ID);

    public static final RegistryObject<Item> AUTO_SMITHING_TABLE_ITEM = ITEMS.register("auto_smithing_table",
            () -> new BlockItem(AUTO_SMITHING_TABLE.get(), new Item.Properties().tab(TAB)));

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, AutoSmithingTableMod.MOD_ID);

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<BlockEntityType<AutoSmithingTableBlockEntity>> AUTO_SMITHING_TABLE_ENTITY_TYPE = BLOCK_ENTITIES.register("auto_smithing_table",
            () -> BlockEntityType.Builder.of(AutoSmithingTableBlockEntity::new, AUTO_SMITHING_TABLE.get()).build(null));

    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, AutoSmithingTableMod.MOD_ID);

    public static final RegistryObject<MenuType<AutoSmithingContainer>> AUTO_SMITHING_CONTAINER = CONTAINERS.register("auto_smithing_table",
            () -> new MenuType<>(AutoSmithingContainer::new));
}
