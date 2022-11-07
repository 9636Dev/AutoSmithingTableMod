package io.github.hw9636.autosmithingtable.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import io.github.hw9636.autosmithingtable.common.AutoSmithingContainer;
import io.github.hw9636.autosmithingtable.common.config.AutoSmithingTableConfig;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
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
        if (toMap < 0 || toMap > maxToMap) throw new IllegalArgumentException("Argument 'toMap' is too big or too small to map");
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
            renderComponentTooltip(stack, List.of(Component.literal((this.menu.data.get(0) << 16 | this.menu.data.get(1)) + "/" + AutoSmithingTableConfig.COMMON.maxEnergyStored.get())), mx, my);
        }

        this.renderables.forEach((w) -> w.render(stack, mx, my, pPartialTick));

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
                PROGRESS_BAR_OFS_TOP, mapNum(menu.data.get(2) , AutoSmithingTableConfig.COMMON.ticksPerCraft.get(),
                        PROGRESS_BAR_WIDTH),PROGRESS_BAR_HEIGHT);

        int mappedY = mapNum((menu.data.get(0) << 16) | menu.data.get(1), AutoSmithingTableConfig.COMMON.maxEnergyStored.get(), ENERGY_BAR_HEIGHT);
        this.blit(stack, i + ENERGY_BAR_ONS_LEFT, j + ENERGY_BAR_ONS_TOP + ENERGY_BAR_HEIGHT - mappedY,
                ENERGY_BAR_OFS_LEFT, ENERGY_BAR_OFS_TOP + ENERGY_BAR_HEIGHT - mappedY, ENERGY_BAR_WIDTH, mappedY);
    }
}
