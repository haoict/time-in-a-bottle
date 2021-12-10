package com.haoict.tiab.client.renderer;

import com.haoict.tiab.entities.TimeAcceleratorEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TimeAcceleratorEntityRenderer extends EntityRenderer<TimeAcceleratorEntity> {
    public TimeAcceleratorEntityRenderer(EntityRendererProvider.Context erp) {
        super(erp);
    }

    @Override
    public void render(TimeAcceleratorEntity entity, float entityYaw, float partialTicks, PoseStack matrixStack, MultiBufferSource bufferIn, int packedLightIn) {
        String timeRate = "x" + 2 * entity.getTimeRate();
        float paddingLeftRight = 2 * entity.getTimeRate() < 10 ? 0.11F : 0.19F;
        drawText(matrixStack, timeRate, new Vector3f(-paddingLeftRight, 0.064F, 0.51F), Vector3f.YP.rotationDegrees(0), ChatFormatting.WHITE.getColor()); // Front
        drawText(matrixStack, timeRate, new Vector3f(paddingLeftRight, 0.064F, -0.51F), Vector3f.YP.rotationDegrees(180F), ChatFormatting.WHITE.getColor()); // Back
        drawText(matrixStack, timeRate, new Vector3f(0.51F, 0.064F, paddingLeftRight), Vector3f.YP.rotationDegrees(90F), ChatFormatting.WHITE.getColor()); // Right
        drawText(matrixStack, timeRate, new Vector3f(-0.51F, 0.064F, -paddingLeftRight), Vector3f.YP.rotationDegrees(-90F), ChatFormatting.WHITE.getColor()); // Left
        drawText(matrixStack, timeRate, new Vector3f(-paddingLeftRight, 0.51F, -0.064F), Vector3f.XP.rotationDegrees(90F), ChatFormatting.WHITE.getColor()); // Top
        drawText(matrixStack, timeRate, new Vector3f(-paddingLeftRight, -0.51F, 0.064F), Vector3f.XP.rotationDegrees(-90F), ChatFormatting.WHITE.getColor()); // Bottom

    }

    @Override
    public ResourceLocation getTextureLocation(TimeAcceleratorEntity entity) {
        return null;
    }

    private void drawText(PoseStack matrixStack, String text, Vector3f translateVector, Quaternion rotate, int color) {
        matrixStack.pushPose();
        matrixStack.translate(translateVector.x(), translateVector.y(), translateVector.z());
        matrixStack.scale(0.02F, -0.02F, 0.02F);
        matrixStack.mulPose(rotate);
        Font fontRenderer = Minecraft.getInstance().gui.getFont();
        fontRenderer.draw(matrixStack, text, 0, 0, color);
        matrixStack.popPose();
    }
}
