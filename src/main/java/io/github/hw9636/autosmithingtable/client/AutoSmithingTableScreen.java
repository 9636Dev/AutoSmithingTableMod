/*
    Copyright (C) 2023 9636Dev
    This file is part of 9636Dev's AutoSmithingTableMod.

    AutoSmithingTableMod is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    AutoSmithingTableMod is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with AutoSmithingTableMod.  If not, see <https://www.gnu.org/licenses/>.
*/
package io.github.hw9636.autosmithingtable.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import io.github.hw9636.autosmithingtable.common.AutoSmithingContainer;
import io.github.hw9636.autosmithingtable.common.config.ASTConfig;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoSmithingTableScreen extends AbstractContainerScreen<AutoSmithingContainer> {
    private static final int PROGRESS_BAR_ONS_LEFT = 100;
    private static final int PROGRESS_BAR_ONS_TOP = 46;
    private static final int PROGRESS_BAR_OFS_LEFT = 177;
    private static final int PROGRESS_BAR_OFS_TOP = 1;
    private static final int ENERGY_BAR_ONS_LEFT = 164;
    private static final int ENERGY_BAR_ONS_TOP = 8;
    private static final int ENERGY_BAR_OFS_LEFT = 177;
    private static final int ENERGY_BAR_OFS_TOP = 22;

    public static final int PROGRESS_BAR_WIDTH = 27;
    public static final int PROGRESS_BAR_HEIGHT = 20;
    private static final int ENERGY_BAR_WIDTH = 4;
    private static final int ENERGY_BAR_HEIGHT = 71;

    private static final ResourceLocation TEXTURE = new ResourceLocation(AutoSmithingTableMod.MOD_ID, "textures/gui/auto_smithing_table.png");

    public AutoSmithingTableScreen(AutoSmithingContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    private int mapNum(int toMap, int maxToMap, int maxMapped) {
        if (toMap < 0 || toMap > maxToMap) {
            LogUtils.getLogger().error("Argument 'toMap' is too big or too small ({} > {})", toMap, maxToMap);
            return 0;
        }
        if (maxToMap == 0) return maxMapped;
        return (int)(toMap / (double)maxToMap * maxMapped + 0.5);
    }

    private boolean isIn(int x, int y, int minX, int minY, int maxX, int maxY) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    @Override
    public void render(@NotNull PoseStack stack, int mx, int my, float pPartialTick) {
        super.render(stack, mx, my, pPartialTick);


        int i = this.getGuiLeft();
        int j = this.getGuiTop();

        this.renderTooltip(stack, mx, my);

        if (isIn(mx, my, i + ENERGY_BAR_ONS_LEFT,j + ENERGY_BAR_ONS_TOP,
                i + ENERGY_BAR_ONS_LEFT + ENERGY_BAR_WIDTH, j + ENERGY_BAR_ONS_TOP + ENERGY_BAR_HEIGHT)) {
            renderComponentTooltip(stack, List.of(new TextComponent((this.menu.data.get(0) << 16 | Short.toUnsignedInt((short)menu.data.get(1))) + "/" + ASTConfig.COMMON.maxEnergyStored.get())), mx, my);
        }
    }


    @Override
    protected void renderBg(@NotNull PoseStack stack, float pPartialTick, int pMouseX, int pMouseY) {
        renderBackground(stack);

        int i = this.getGuiLeft();
        int j = this.getGuiTop();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);

        this.blit(stack, i + PROGRESS_BAR_ONS_LEFT, j + PROGRESS_BAR_ONS_TOP, PROGRESS_BAR_OFS_LEFT,
                PROGRESS_BAR_OFS_TOP, mapNum(menu.data.get(2) ,ASTConfig.COMMON.ticksPerCraft.get(),
                        PROGRESS_BAR_WIDTH),PROGRESS_BAR_HEIGHT);

        int mappedY = mapNum((menu.data.get(0) << 16) | Short.toUnsignedInt((short)menu.data.get(1)), ASTConfig.COMMON.maxEnergyStored.get(), ENERGY_BAR_HEIGHT);
        this.blit(stack, i + ENERGY_BAR_ONS_LEFT, j + ENERGY_BAR_ONS_TOP + ENERGY_BAR_HEIGHT - mappedY,
                ENERGY_BAR_OFS_LEFT, ENERGY_BAR_OFS_TOP + ENERGY_BAR_HEIGHT - mappedY, ENERGY_BAR_WIDTH, mappedY);
    }
}
