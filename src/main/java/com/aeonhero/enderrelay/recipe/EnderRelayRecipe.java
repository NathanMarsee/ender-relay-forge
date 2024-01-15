package com.aeonhero.enderrelay.recipe;

import com.aeonhero.enderrelay.block.ModBlockEntities;
import com.aeonhero.enderrelay.item.ModItems;
import com.google.gson.JsonObject;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
//import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import com.aeonhero.enderrelay.EnderRelay;
import com.aeonhero.enderrelay.block.EnderRelayBlockEntity;
import org.jetbrains.annotations.Nullable;

public class EnderRelayRecipe extends CustomRecipe
{
    public static final Item[][] RECIPE = new Item[][] {
            {Items.OBSIDIAN, Items.POPPED_CHORUS_FRUIT, Items.OBSIDIAN},
            {Items.POPPED_CHORUS_FRUIT, Items.COMPASS, Items.POPPED_CHORUS_FRUIT},
            {Items.OBSIDIAN, Items.POPPED_CHORUS_FRUIT, Items.OBSIDIAN}
    };

    /*public static final SimpleRecipeSerializer<EnderRelayRecipe> SERIALIZER = new SimpleRecipeSerializer<EnderRelayRecipe>(EnderRelayRecipe::new);
    public EnderRelayRecipe(CraftingBookCategory craftingBookCategory) {
        super(craftingBookCategory);
    }*/
    public static final SimpleRecipeSerializer<EnderRelayRecipe> SERIALIZER = new SimpleRecipeSerializer<>(EnderRelayRecipe::new);
    public EnderRelayRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingContainer container, Level level) {
        int i = 0;
        for (Item[] row : RECIPE) {
            for (Item item : row) {
                ItemStack gotItem = container.getItem(i);
                i++;
                if (item == Items.COMPASS) {
                    if (!gotItem.is(Items.COMPASS)) return false;
                    if (!CompassItem.isLodestoneCompass(gotItem)) return false;
                    if (CompassItem.getLodestonePosition(gotItem.getTag()) == null) return false;
                    if (level.isClientSide) continue;
                    GlobalPos pos = CompassItem.getLodestonePosition(gotItem.getTag());
                    Level lodedstoneLevel = level.getServer().getLevel(pos.dimension());
                    if (lodedstoneLevel.dimensionTypeId() != BuiltinDimensionTypes.END) return false;

                    continue;
                }
                if (!gotItem.is(item)) {
                    return false;
                }

            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingContainer container)
    {
        ItemStack compass = container.getItem(4);
        GlobalPos pos = CompassItem.getLodestonePosition(compass.getTag());
        ItemStack relay = new ItemStack(ModItems.ENDER_RELAY_ITEM.get(), 1);
        CompoundTag blockTag = new CompoundTag();

        blockTag.putString(EnderRelayBlockEntity.DIMENSION_ID_KEY, pos.dimension().location().toString());
        blockTag.putIntArray(EnderRelayBlockEntity.POSITION_KEY, new int[] { pos.pos().getX(), pos.pos().getY(), pos.pos().getZ() });
        BlockItem.setBlockEntityData(relay, ModBlockEntities.ENDER_RELAY_BLOCK_ENTITY.get(), blockTag);
        return relay;
    }

    /*public ItemStack assemble(CraftingContainer container, RegistryAccess registryAccess) {
        ItemStack compass = container.getItem(4);
        GlobalPos pos = CompassItem.getLodestonePosition(compass.getTag());
        ItemStack relay = new ItemStack(EnderRelay.ENDER_RELAY_ITEM, 1);
        CompoundTag blockTag = new CompoundTag();

        blockTag.putString(EnderRelayBlockEntity.DIMENSION_ID_KEY, pos.dimension().location().toString());
        blockTag.putIntArray(EnderRelayBlockEntity.POSITION_KEY, new int[] { pos.pos().getX(), pos.pos().getY(), pos.pos().getZ() });
        BlockItem.setBlockEntityData(relay, EnderRelay.ENDER_RELAY_BLOCK_ENTITY, blockTag);
        return relay;
    }*/

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return x == 3 && y == 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    public ItemStack getResultItem() {
        return new ItemStack(ModItems.ENDER_RELAY_ITEM.get(), 1);
    }
}
