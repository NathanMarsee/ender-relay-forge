package com.aeonhero.enderrelay.block;

import com.aeonhero.enderrelay.EnderRelay;
import com.aeonhero.enderrelay.recipe.EnderRelayRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes
{
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EnderRelay.MODID);

    public static final RegistryObject<RecipeSerializer<EnderRelayRecipe>> ENDER_RELAY_RECIPE_SERIALIZER =
            SERIALIZERS.register("ender_relay_recipe", () -> EnderRelayRecipe.SERIALIZER);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
    }
}
