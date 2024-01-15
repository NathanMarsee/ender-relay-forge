package com.aeonhero.enderrelay.block;

import com.aeonhero.enderrelay.EnderRelay;
import com.aeonhero.enderrelay.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, EnderRelay.MODID);

    public static final RegistryObject<Block> ENDER_RELAY_BLOCK = registerBlock("ender_relay", () -> new EnderRelayBlock(BlockBehaviour.Properties.of(Material.STONE)
            .strength(50.0f, 1200.0f).requiresCorrectToolForDrops()), CreativeModeTab.TAB_TRANSPORTATION);
/*public static final BlockEntityType<EnderRelayBlockEntity> ENDER_RELAY_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "ender_relay"),
            FabricBlockEntityTypeBuilder.create(EnderRelayBlockEntity::new, ENDER_RELAY_BLOCK).build()
    );*/

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab)
    {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        //registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    /*private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab)
    {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }*/

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}
