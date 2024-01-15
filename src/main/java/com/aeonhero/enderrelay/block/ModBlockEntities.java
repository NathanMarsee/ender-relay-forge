package com.aeonhero.enderrelay.block;

import com.aeonhero.enderrelay.EnderRelay;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities
{
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EnderRelay.MODID);

    public static final RegistryObject<BlockEntityType<EnderRelayBlockEntity>> ENDER_RELAY_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("ender_relay", () ->
                    BlockEntityType.Builder.of(EnderRelayBlockEntity::new,
                            ModBlocks.ENDER_RELAY_BLOCK.get()).build(null));

    public static void register(IEventBus eventBus)
    {
        BLOCK_ENTITIES.register(eventBus);
    }
}
