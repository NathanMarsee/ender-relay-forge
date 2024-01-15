package com.aeonhero.enderrelay.item;

import com.aeonhero.enderrelay.EnderRelay;
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

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, EnderRelay.MODID);

    public static final RegistryObject<Item> ENDER_RELAY_ITEM = ITEMS.register("ender_relay", () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_TRANSPORTATION)));

    /*private static <T extends Item> RegistryObject<T> registerItem(String name, Supplier<T> item, CreativeModeTab tab)
    {
        return ModItems.ITEMS.register(name, () -> new Item(item.get(), new Item.Properties().tab(tab)));
    }*/
    public static void register(IEventBus eventBus)
    {
        ITEMS.register(eventBus);
    }
}
