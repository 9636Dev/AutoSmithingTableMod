package io.github.hw9636.autosmithingtable.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
public class UpgradeRecipeHelper {
    public static UpgradeRecipe fromItemstacks(Level level, final ItemStack input1, final ItemStack input2) {
        UpgradeRecipe ur = level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING).stream()
                .filter((upgradeRecipe -> upgradeRecipe.getIngredients().get(0).test(input1) && upgradeRecipe.isAdditionIngredient(input2))).findFirst().orElse(null);

        return ur;
    }

}
