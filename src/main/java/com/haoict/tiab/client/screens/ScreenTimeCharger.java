package com.haoict.tiab.client.screens;

import com.haoict.tiab.common.container.ContainerTimeCharger;
import com.haoict.tiab.common.utils.Helpers;
import com.haoict.tiab.config.Constants;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.TranslationTextComponent;

import java.awt.*;
import java.util.Arrays;

/*
 * This is a gui similar to that of a furnace. It has a progress bar and a burn time indicator.
 * Both indicators have mouse over text
 *
 * The Screen is drawn in several layers, most importantly:
 * Background - renderBackground() - eg a grey fill
 * Background texture - drawGuiContainerBackgroundLayer() (eg the frames for the slots)
 * Foreground layer - typically text labels
 * renderHoveredToolTip - for tool tips when the mouse is hovering over something of interest
 */
public class ScreenTimeCharger extends ContainerScreen<ContainerTimeCharger> {
  private static final ResourceLocation background = new ResourceLocation(Constants.MOD_ID, "textures/gui/timecharger.png");
  private final ContainerTimeCharger container;

  public ScreenTimeCharger(ContainerTimeCharger container, PlayerInventory inv, ITextComponent titleIn) {
    super(container, inv, titleIn);
    this.container = container;
  }

  @Override
  public void init() {
    super.init();
  }

  @Override
  public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
    this.renderBackground(stack);
    super.render(stack, mouseX, mouseY, partialTicks);

    this.renderHoveredTooltip(stack, mouseX, mouseY); // @mcp: func_230459_a_ = renderHoveredToolTip
    if (mouseX > (guiLeft + 7) && mouseX < (guiLeft + 7) + 18 && mouseY > (guiTop + 7) && mouseY < (guiTop + 7) + 71)
      this.renderTooltip(stack, LanguageMap.getInstance().func_244260_a(Arrays.asList(
          new TranslationTextComponent("screen.tiab.timecharger.energy", Helpers.withSuffix(this.container.getEnergy()), Helpers.withSuffix(this.container.getMaxPower()))
      )), mouseX, mouseY);
  }


  @Override
  protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int x, int y) {
    /*
     * width and height are the size provided to the window when initialised after creation.
     * xSize, ySize are the expected size of the texture-? usually seems to be left as a default.
     * The code below is typical for vanilla containers, so I've just copied that - it appears to centre the texture within the available window
    */
    RenderSystem.color4f(1, 1, 1, 1);
    getMinecraft().getTextureManager().bindTexture(background);
    this.blit(stack, guiLeft, guiTop, 0, 0, xSize, ySize);

    int maxEnergy = this.container.getMaxPower();
    int energyBarHeight = 69;
    if (maxEnergy > 0) {
      int remainingHeight = energyBarHeight * this.container.getEnergy() / maxEnergy;
      this.blit(stack, guiLeft + 8, guiTop + 77 - remainingHeight, 176, 69 - remainingHeight, 16, remainingHeight);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(MatrixStack stack, int mouseX, int mouseY) {
    Minecraft.getInstance().fontRenderer.drawString(stack, I18n.format("block.tiab.timecharger"), 65, 8, Color.DARK_GRAY.getRGB());
  }
}
