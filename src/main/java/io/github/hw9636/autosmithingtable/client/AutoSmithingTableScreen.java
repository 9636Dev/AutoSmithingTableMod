package io.github.hw9636.autosmithingtable.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import io.github.hw9636.autosmithingtable.AutoSmithingTableMod;
import io.github.hw9636.autosmithingtable.common.AutoSmithingContainer;
import io.github.hw9636.autosmithingtable.common.AutoSmithingTableBlockEntity;
import io.github.hw9636.autosmithingtable.common.config.ASTConfig;
import io.github.hw9636.autosmithingtable.common.network.ASTPacketHandler;
import io.github.hw9636.autosmithingtable.common.network.SideChangeMSG;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;

public class AutoSmithingTableScreen extends AbstractContainerScreen<AutoSmithingContainer> {

    private static final int INPUT_1_OFS_LEFT = 27;
    private static final int INPUT_1_OFS_TOP = 172;
    private static final int INPUT_2_OFS_LEFT = 93;
    private static final int INPUT_2_OFS_TOP = 172;
    private static final int OUTPUT_OFS_LEFT = 153;
    private static final int OUTPUT_OFS_TOP = 172;
    private static final int EMPTY_OFS_LEFT = 212;
    private static final int EMPTY_OFS_TOP = 172;
    private static final int BUTTON_WIDGET_WIDTH = 17;
    private static final int BUTTON_WIDGET_HEIGHT = 17;


    private static final int PROGRESS_BAR_ONS_LEFT = 100;
    private static final int PROGRESS_BAR_ONS_TOP = 46;
    private static final int PROGRESS_BAR_OFS_LEFT = 177;
    private static final int PROGRESS_BAR_OFS_TOP = 1;
    private static final int ENERGY_BAR_ONS_LEFT = 164;
    private static final int ENERGY_BAR_ONS_TOP = 8;
    private static final int ENERGY_BAR_OFS_LEFT = 177;
    private static final int ENERGY_BAR_OFS_TOP = 22;
    private static final int SIDES_CONFIG_CL_ONS_LEFT = -32;
    private static final int SIDES_CONFIG_CL_ONS_TOP = 10;
    private static final int SIDES_CONFIG_CL_OFS_LEFT = 218;
    private static final int SIDES_CONFIG_CL_OFS_TOP = 1;
    private static final int SIDES_CONFIG_OP_OFS_LEFT = 193;
    private static final int SIDES_CONFIG_OP_OFS_TOP = 57;

    private static final int SIDES_CONFIG_OP_ONS_LEFT = -58;
    private static final int SIDES_CONFIG_OP_ONS_TOP = 10;

    public static final int PROGRESS_BAR_WIDTH = 27;
    public static final int PROGRESS_BAR_HEIGHT = 20;
    private static final int ENERGY_BAR_WIDTH = 4;
    private static final int ENERGY_BAR_HEIGHT = 71;
    private static final int SIDES_CONFIG_CL_WIDTH = 28;
    private static final int SIDES_CONFIG_CL_HEIGHT = 28;
    private static final int SIDES_CONFIG_OP_WIDTH = 54;
    private static final int SIDES_CONFIG_OP_HEIGHT = 53;

    private static final int[] BUTTON_WIDGET_ONS_POSITIONS = {
            SIDES_CONFIG_OP_ONS_LEFT + SIDES_CONFIG_OP_WIDTH / 2 - BUTTON_WIDGET_WIDTH / 2,
            SIDES_CONFIG_OP_ONS_TOP + SIDES_CONFIG_OP_HEIGHT / 4 - BUTTON_WIDGET_HEIGHT / 2,

            SIDES_CONFIG_OP_ONS_LEFT + SIDES_CONFIG_OP_WIDTH / 4 - BUTTON_WIDGET_WIDTH / 2,
            SIDES_CONFIG_OP_ONS_TOP + SIDES_CONFIG_OP_HEIGHT / 2 - BUTTON_WIDGET_HEIGHT / 2,

            SIDES_CONFIG_OP_ONS_LEFT + SIDES_CONFIG_OP_WIDTH / 2 - BUTTON_WIDGET_WIDTH / 2,
            SIDES_CONFIG_OP_ONS_TOP + SIDES_CONFIG_OP_HEIGHT / 2 - BUTTON_WIDGET_HEIGHT / 2,

            SIDES_CONFIG_OP_ONS_LEFT + SIDES_CONFIG_OP_WIDTH / 4 * 3 - BUTTON_WIDGET_WIDTH / 2,
            SIDES_CONFIG_OP_ONS_TOP + SIDES_CONFIG_OP_HEIGHT / 2 - BUTTON_WIDGET_HEIGHT / 2,

            SIDES_CONFIG_OP_ONS_LEFT + SIDES_CONFIG_OP_WIDTH / 2 - BUTTON_WIDGET_WIDTH / 2,
            SIDES_CONFIG_OP_ONS_TOP + SIDES_CONFIG_OP_HEIGHT / 4 * 3 - BUTTON_WIDGET_HEIGHT / 2,

            SIDES_CONFIG_OP_ONS_LEFT + SIDES_CONFIG_OP_WIDTH / 4 * 3 - BUTTON_WIDGET_WIDTH / 2,
            SIDES_CONFIG_OP_ONS_TOP + SIDES_CONFIG_OP_HEIGHT / 4 * 3 - BUTTON_WIDGET_HEIGHT / 2
    };

    private static final int[] BUTTON_WIDGET_TEXTURE_OFS_POSITIONS = {
            EMPTY_OFS_LEFT, EMPTY_OFS_TOP,
            INPUT_1_OFS_LEFT, INPUT_1_OFS_TOP,
            INPUT_2_OFS_LEFT, INPUT_2_OFS_TOP,
            OUTPUT_OFS_LEFT, OUTPUT_OFS_TOP
    };

    private static final ResourceLocation TEXTURE = new ResourceLocation(AutoSmithingTableMod.MOD_ID, "textures/gui/auto_smithing_table.png");
    //private static final Logger LOGGER = LogUtils.getLogger();

    //private boolean isHoveringOverButton, isOpen;
    //private final Direction[] relativeDirections;

    public AutoSmithingTableScreen(AutoSmithingContainer pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);

        //this.isHoveringOverButton = false;
        //this.isOpen = false;
        //this.relativeDirections = generationDirections(this.menu.data.get(3));
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

        //this.isHoveringOverButton = isIn(mx, my, i + SIDES_CONFIG_CL_ONS_LEFT, j + SIDES_CONFIG_CL_ONS_TOP,
        //        i + SIDES_CONFIG_CL_ONS_LEFT + SIDES_CONFIG_CL_WIDTH, j + SIDES_CONFIG_CL_ONS_TOP + SIDES_CONFIG_CL_HEIGHT);

        this.renderTooltip(stack, mx, my);

        if (isIn(mx, my, i + ENERGY_BAR_ONS_LEFT,j + ENERGY_BAR_ONS_TOP,
                i + ENERGY_BAR_ONS_LEFT + ENERGY_BAR_WIDTH, j + ENERGY_BAR_ONS_TOP + ENERGY_BAR_HEIGHT)) {
            renderComponentTooltip(stack, List.of(Component.literal((this.menu.data.get(0) << 16 | this.menu.data.get(1)) + "/" + ASTConfig.COMMON.maxEnergyStored.get())), mx, my);
        }

        this.renderables.forEach((w) -> w.render(stack, mx, my, pPartialTick));

    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton) {
        /* int i = this.getGuiLeft();
        int j = this.getGuiTop();

        if (this.isOpen) {
            if (isIn((int) pMouseX, (int) pMouseY, i + SIDES_CONFIG_OP_ONS_LEFT, j + SIDES_CONFIG_OP_ONS_TOP,
                    i + SIDES_CONFIG_OP_ONS_LEFT + SIDES_CONFIG_OP_WIDTH, j + SIDES_CONFIG_OP_ONS_TOP + SIDES_CONFIG_OP_HEIGHT)) {

                int sidesConfig = this.menu.data.get(4);

                for (int k = 0;k < 6;k++) {
                    if (isIn((int) pMouseX - i, (int) pMouseY - j, BUTTON_WIDGET_ONS_POSITIONS[k * 2],
                            BUTTON_WIDGET_ONS_POSITIONS[k * 2 + 1], BUTTON_WIDGET_WIDTH, BUTTON_WIDGET_HEIGHT)) {
                        int sideInfo = AutoSmithingTableBlockEntity.getSide(sidesConfig, relativeDirections[k]);

                        if (sideInfo < AutoSmithingTableBlockEntity.SIDE_OUTPUT) sideInfo += 1;
                        else sideInfo = AutoSmithingTableBlockEntity.SIDE_NONE;

                        int newSidesConfig = AutoSmithingTableBlockEntity.setSide(relativeDirections[k], sideInfo, sidesConfig);
                        this.menu.data.set(4, newSidesConfig);
                        ASTPacketHandler.INSTANCE.sendToServer(new SideChangeMSG(this.menu.containerId, newSidesConfig));

                        return true;
                    }
                }
                return true;
            }
            else this.isOpen = false;
        }
        else {
            if (isIn((int) pMouseX, (int) pMouseY, i + SIDES_CONFIG_CL_ONS_LEFT, j + SIDES_CONFIG_CL_ONS_TOP,
                    i + SIDES_CONFIG_CL_ONS_LEFT + SIDES_CONFIG_CL_WIDTH, j + SIDES_CONFIG_CL_ONS_TOP + SIDES_CONFIG_CL_HEIGHT)) {
                this.isOpen = true;
                return true;
            }
        } */

        return super.mouseClicked(pMouseX, pMouseY, pButton);
    }



    /*private Direction[] generationDirections(int directionInt) {
        // Up, Left, Front, Right, Down, Back
        Direction direction = Direction.from2DDataValue(directionInt).getOpposite();

        return new Direction[] {
            Direction.UP, direction.getClockWise(), direction, direction.getClockWise(), Direction.DOWN, direction.getOpposite()
        };
    } */

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

        int mappedY = mapNum(menu.data.get(0), ASTConfig.COMMON.maxEnergyStored.get(), ENERGY_BAR_HEIGHT);
        this.blit(stack, i + ENERGY_BAR_ONS_LEFT, j + ENERGY_BAR_ONS_TOP + ENERGY_BAR_HEIGHT - mappedY,
                ENERGY_BAR_OFS_LEFT, ENERGY_BAR_OFS_TOP + ENERGY_BAR_HEIGHT - mappedY, ENERGY_BAR_WIDTH, mappedY);

        /* if (!this.isOpen) {
            if (this.isHoveringOverButton)
                RenderSystem.setShaderColor(0.8f, 0.8f, 0.8f, 1.0f); // Render the Button darker
            this.blit(stack, i + SIDES_CONFIG_CL_ONS_LEFT, j + SIDES_CONFIG_CL_ONS_TOP, SIDES_CONFIG_CL_OFS_LEFT,
                    SIDES_CONFIG_CL_OFS_TOP, SIDES_CONFIG_CL_WIDTH, SIDES_CONFIG_CL_HEIGHT);
        }
        else {
            this.blit(stack, i + SIDES_CONFIG_OP_ONS_LEFT, j + SIDES_CONFIG_OP_ONS_TOP, SIDES_CONFIG_OP_OFS_LEFT,
                    SIDES_CONFIG_OP_OFS_TOP, SIDES_CONFIG_OP_WIDTH, SIDES_CONFIG_OP_HEIGHT);

            int sidesConfig = this.menu.data.get(4);
            for (int k = 0;k < 6;k++) {
                int sideInfo = AutoSmithingTableBlockEntity.getSide(sidesConfig, relativeDirections[k]);

                int ofsX = BUTTON_WIDGET_TEXTURE_OFS_POSITIONS[sideInfo * 2];
                int ofsY = BUTTON_WIDGET_TEXTURE_OFS_POSITIONS[sideInfo * 2 + 1];
                this.blit(stack, i + BUTTON_WIDGET_ONS_POSITIONS[k * 2], j + BUTTON_WIDGET_ONS_POSITIONS[k * 2 + 1],
                        ofsX, ofsY, BUTTON_WIDGET_WIDTH, BUTTON_WIDGET_HEIGHT);
            }
        } */
    }
}
