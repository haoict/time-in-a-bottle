package com.haoict.tiab.entities;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;

public class TiabEntityTypes {
  public static EntityType<EntityTimeAccelerator> timeAcceleratorEntityType
      = EntityType.Builder.<EntityTimeAccelerator>create(EntityTimeAccelerator::new, EntityClassification.MISC)
      .size(0.1F, 0.1F)
      .build("");
}
