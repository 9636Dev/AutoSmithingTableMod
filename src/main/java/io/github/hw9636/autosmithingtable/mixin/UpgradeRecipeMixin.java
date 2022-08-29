package io.github.hw9636.autosmithingtable.mixin;

import io.github.hw9636.autosmithingtable.common.SmithingRecipeHelper;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(UpgradeRecipe.class)
public abstract class UpgradeRecipeMixin implements SmithingRecipeHelper {
    @Shadow
    @Final
    Ingredient base;

    @Override
    public Ingredient getBase() {
        return base;
    }
}
