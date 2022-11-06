package io.github.hw9636.autosmithingtable.common.integration;

import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import io.github.hw9636.autosmithingtable.common.Registries;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(AutoSmithingTableMod.MOD_ID, "jei");

    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(Registries.AUTO_SMITHING_TABLE_ITEM.get().getDefaultInstance(), RecipeTypes.SMITHING);
    }
}
