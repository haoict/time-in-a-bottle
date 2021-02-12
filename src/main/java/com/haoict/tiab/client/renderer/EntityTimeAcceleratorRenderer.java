package com.haoict.tiab.client.renderer;

import com.haoict.tiab.entities.EntityTimeAccelerator;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;

@OnlyIn(Dist.CLIENT)
public class EntityTimeAcceleratorRenderer extends EntityRenderer<EntityTimeAccelerator> {
  public EntityTimeAcceleratorRenderer(EntityRendererManager erm) {
    super(erm);
  }

  private static void drawLine(Matrix4f matrixPos, IVertexBuilder vertexBuffer, float x1, float y1, float z1, float x2, float y2, float z2, Color colour) {
    vertexBuffer.pos(matrixPos, x1, y1, z1).color(colour.getRed(), colour.getGreen(), colour.getBlue(), 255).endVertex();
    vertexBuffer.pos(matrixPos, x2, y2, z2).color(colour.getRed(), colour.getGreen(), colour.getBlue(), 255).endVertex();
  }

  private void drawCrossHair(MatrixStack matrixStack, IRenderTypeBuffer renderBuffers) {
    matrixStack.push();
    Matrix4f matrixPos = matrixStack.getLast().getMatrix();
    IVertexBuilder vertexBuilderLines = renderBuffers.getBuffer(RenderType.LINES);

    float cx = 0;
    float cy = 0;
    float cz = 0;
    final float CROSSHAIR_RADIUS = 3.0F;
    drawLine(matrixPos, vertexBuilderLines, cx - CROSSHAIR_RADIUS, cy, cz, cx + CROSSHAIR_RADIUS, cy, cz, Color.RED);
    drawLine(matrixPos, vertexBuilderLines, cx, cy - CROSSHAIR_RADIUS, cz, cx, cy + CROSSHAIR_RADIUS, cz, Color.RED);
    drawLine(matrixPos, vertexBuilderLines, cx, cy, cz - CROSSHAIR_RADIUS, cx, cy, cz + CROSSHAIR_RADIUS, Color.RED);

    matrixStack.pop();
  }

  private void drawText(MatrixStack matrixStack, String text, Vector3f translateVector, Quaternion rotate, int color) {
    matrixStack.push();
    matrixStack.translate(translateVector.getX(), translateVector.getY(), translateVector.getZ());
    matrixStack.scale(0.02F, -0.02F, 0.02F);
    matrixStack.rotate(rotate);
    FontRenderer fontRenderer = Minecraft.getInstance().fontRenderer;
    fontRenderer.drawString(matrixStack, text, 0, 0, color);

    /* Use this when you want color changes based on TileEntity background color
    IReorderingProcessor irp = new StringTextComponent("x32").func_241878_f();
    fontRenderer.func_238416_a_(irp, 0, 0, color, false, matrixStack.getLast().getMatrix(), renderBuffers, false, 0, packedLightIn);*/

    matrixStack.pop();
  }

  @Override
  public void render(EntityTimeAccelerator entity, float entitdisableLightmapyYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderBuffers, int packedLightIn) {
    String timeRate = "x" + 2 * entity.getTimeRate();
    float paddingLeftRight = 2 * entity.getTimeRate() < 10 ? 0.11F : 0.19F;
    drawText(matrixStack, timeRate, new Vector3f(-paddingLeftRight, 0.064F, 0.51F), Vector3f.YP.rotationDegrees(0), TextFormatting.WHITE.getColor()); // Front
    drawText(matrixStack, timeRate, new Vector3f(paddingLeftRight, 0.064F, -0.51F), Vector3f.YP.rotationDegrees(180F), TextFormatting.WHITE.getColor()); // Back
    drawText(matrixStack, timeRate, new Vector3f(0.51F, 0.064F, paddingLeftRight), Vector3f.YP.rotationDegrees(90F), TextFormatting.WHITE.getColor()); // Right
    drawText(matrixStack, timeRate, new Vector3f(-0.51F, 0.064F, -paddingLeftRight), Vector3f.YP.rotationDegrees(-90F), TextFormatting.WHITE.getColor()); // Left
    drawText(matrixStack, timeRate, new Vector3f(-paddingLeftRight, 0.51F, -0.064F), Vector3f.XP.rotationDegrees(90F), TextFormatting.WHITE.getColor()); // Top
    drawText(matrixStack, timeRate, new Vector3f(-paddingLeftRight, -0.51F, 0.064F), Vector3f.XP.rotationDegrees(-90F), TextFormatting.WHITE.getColor()); // Bottom
  }

  @Override
  public ResourceLocation getEntityTexture(EntityTimeAccelerator entity) {
    return null;
  }
}
