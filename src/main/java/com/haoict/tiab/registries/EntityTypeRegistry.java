package com.haoict.tiab.registries;

import com.haoict.tiab.config.Constants;
import com.haoict.tiab.entities.TimeAcceleratorEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EntityTypeRegistry {
    public static final DeferredRegister<EntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Constants.MOD_ID);

    public static RegistryObject<EntityType<TimeAcceleratorEntity>> timeAcceleratorEntityType =
            TILE_ENTITIES.register("time_accelerator_entity_type", () -> EntityType.Builder.<TimeAcceleratorEntity>of(TimeAcceleratorEntity::new, MobCategory.MISC)
                    .sized(0.1F, 0.1F)
                    .build(new ResourceLocation(Constants.MOD_ID, "time_accelerator_entity_type").toString()));
}
