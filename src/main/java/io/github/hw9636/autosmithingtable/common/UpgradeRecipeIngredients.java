package io.github.hw9636.autosmithingtable.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;

public class UpgradeRecipeIngredients {

    public final Ingredient base, addition;
    public final ItemStack result;
    private static Field baseField,additionField;

    public UpgradeRecipeIngredients(Ingredient base, Ingredient addition, ItemStack result) {
        this.base = base;
        this.addition = addition;
        this.result = result;

        if (base == null || addition == null) {
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

    public static UpgradeRecipeIngredients fromItemstacks(Level level, final ItemStack input1, final ItemStack input) {
        return null;
    }
}
