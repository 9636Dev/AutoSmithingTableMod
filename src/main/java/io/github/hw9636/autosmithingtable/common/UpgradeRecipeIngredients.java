package io.github.hw9636.autosmithingtable.common;

import mezz.jei.api.constants.RecipeTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
public class UpgradeRecipeIngredients {

    private static final Logger logger = LogManager.getLogger();
    public final Ingredient base, addition;
    public final ItemStack result;
    private static Field baseField,additionField;

    public UpgradeRecipeIngredients(Ingredient base, Ingredient addition, ItemStack result) {
        this.base = base;
        this.addition = addition;
        this.result = result;

        init();
    }

    private static void init() {
        if (baseField == null || additionField == null) {
            try {
                baseField = UpgradeRecipe.class.getField("base");
                additionField = UpgradeRecipe.class.getField("addition");

                baseField.setAccessible(true);
                additionField.setAccessible(true);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static UpgradeRecipeIngredients fromItemstacks(Level level, final ItemStack input1, final ItemStack input2) {
        init();
        UpgradeRecipe ur = level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING).stream()
                .filter((upgradeRecipe -> {
                    try {
                        return ((Ingredient) baseField.get(upgradeRecipe)).test(input1) && upgradeRecipe.isAdditionIngredient(input2);
                    } catch (IllegalAccessException e) {
                        logger.error(e);
                        return false;
                    }
                })).findFirst().orElse(null);

        if (ur != null) {
            try {
                return new UpgradeRecipeIngredients((Ingredient) baseField.get(ur), (Ingredient) additionField.get(ur), ur.getResultItem());
            } catch (IllegalAccessException e) {
                logger.error(e);
            }
        }

        return null;
    }

    public boolean isBaseIngredient(ItemStack itemstack) {
        return this.base.test(itemstack);
    }

    public boolean isAdditionalIngredient(ItemStack itemstack) {
        return this.addition.test(itemstack);
    }

    public ItemStack getResult() {
        return result;
    }

    @Override
    public String toString() {
        return "UpgradeRecipeIngredients{" +
                "base=" + base +
                ", addition=" + addition +
                ", result=" + result +
                '}';
    }
}
