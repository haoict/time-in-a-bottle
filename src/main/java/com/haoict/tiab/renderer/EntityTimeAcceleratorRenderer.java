package com.haoict.tiab.renderer;

import com.haoict.tiab.entities.EntityTimeAccelerator;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class EntityTimeAcceleratorRenderer extends EntityRenderer<EntityTimeAccelerator> {
  public EntityTimeAcceleratorRenderer(EntityRendererManager erm) {
    super(erm);
  }

  public void render(EntityTimeAccelerator entity, float entitdisableLightmapyYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer renderBuffers, int packedLightIn) {
    super.render(entity, entitdisableLightmapyYaw, partialTicks, matrixStack, renderBuffers, packedLightIn);
  }

  @Override
  public ResourceLocation getEntityTexture(EntityTimeAccelerator entity) {
    return null;
  }
}
