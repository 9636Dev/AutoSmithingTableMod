package io.github.hw9636.autosmithingtable.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
public class UpgradeRecipeHelper {
    public static UpgradeRecipe fromItemStacks(Level level, final ItemStack input1, final ItemStack input2) {

        return level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING).stream()
                .filter((upgradeRecipe -> ((SmithingRecipeHelper)(upgradeRecipe)).getBase().test(input1) && upgradeRecipe.isAdditionIngredient(input2))).findFirst().orElse(null);
    }

}
