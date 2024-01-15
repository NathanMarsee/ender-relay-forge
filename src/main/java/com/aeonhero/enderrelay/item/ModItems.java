package com.aeonhero.enderrelay.item;

import com.aeonhero.enderrelay.EnderRelay;
import com.aeonhero.enderrelay.block.EnderRelayBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static com.aeonhero.enderrelay.block.ModBlocks.ENDER_RELAY_BLOCK;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EnderRelay.MODID);

    private static final Item.Properties erProps = new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS);
    //erProps.category = TAB_DECORATIONS;
    public static final RegistryObject<BlockItem> ENDER_RELAY_ITEM = ITEMS.register("ender_relay", () -> new BlockItem(ENDER_RELAY_BLOCK.get(), erProps));

    /*private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item, CreativeModeTab tab)
    {
        return ModItems.ITEMS.register(name, () -> new Item(item.get(), new Item.Properties().tab(tab)));
    }*/
    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
