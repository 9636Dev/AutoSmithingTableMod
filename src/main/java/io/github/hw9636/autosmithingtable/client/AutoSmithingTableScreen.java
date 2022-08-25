package io.github.hw9636.autosmithingtable.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import io.github.hw9636.autosmithingtable.common.ASTConfig;
import io.github.hw9636.autosmithingtable.common.AutoSmithingContainer;
import io.github.hw9636.autosmithingtable.common.AutoSmithingTableBlock;
import io.github.hw9636.autosmithingtable.common.AutoSmithingTableBlockEntity;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IRecipesGui;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.gui.GuiUtils;

import java.util.List;

public class AutoSmithingTableScreen extends AbstractContainerScreen<AutoSmithingContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(AutoSmithingTableMod.MOD_ID, "textures/gui/auto_smithing_table.png");

    public AutoSmithingTableScreen(AutoSmithingContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    private int mapNum(int toMap, int maxToMap, int maxMapped) {
        if (maxToMap == 0) return maxMapped;
        return (int)(toMap / (double)maxToMap * maxMapped + 0.5);
    }

    private boolean isIn(int x, int y, int minX, int minY, int maxX, int maxY) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    @Override
    public void render(PoseStack stack, int mx, int my, float pPartialTick) {
        super.render(stack, mx, my, pPartialTick);

        this.renderTooltip(stack, mx, my);

        int i = this.getGuiLeft();
        int j = this.getGuiTop();

        if (isIn(mx, my, i + 164,j + 8, i + 168, j + 79)) {
            renderComponentTooltip(stack, List.of(new TextComponent(this.menu.data.get(0) + "/" + AutoSmithingTableBlockEntity.MAX_ENERGY_STORED)), mx, my);
        }
    }

    @Override
    protected void renderBg(PoseStack stack, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(stack);

        int i = this.getGuiLeft();
        int j = this.getGuiTop();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        blit(stack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        this.font.draw(stack, this.playerInventoryTitle, this.leftPos + 8, this.topPos + 75, 0x404040);

        // Energy bar: 164,8 to 168, 79
        // Full: 177,22 to 181, 93

        // Arrow: 100, 46 to 127, 66
        // Full: 177, 1 to 204, 21

        int mappedY = mapNum(menu.data.get(0), AutoSmithingTableBlockEntity.MAX_ENERGY_STORED, 79 - 8);
        blit(stack, i + 165, j + 79 - mappedY, 177 - this.imageHeight, 93 - mappedY, 181 - 177, mappedY);
    }
}
