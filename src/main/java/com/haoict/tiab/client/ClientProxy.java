package com.haoict.tiab.client;

import com.haoict.tiab.client.renderer.EntityTimeAcceleratorRenderer;
import com.haoict.tiab.common.entities.TiabEntityTypes;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy {
  public ClientProxy() {
    RenderingRegistry.registerEntityRenderingHandler(TiabEntityTypes.timeAcceleratorEntityType, erm -> new EntityTimeAcceleratorRenderer(erm));
  }
}
