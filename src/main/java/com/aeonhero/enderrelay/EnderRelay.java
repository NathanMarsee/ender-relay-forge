package com.aeonhero.enderrelay;

/*import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;*/
import com.aeonhero.enderrelay.block.*;
import com.aeonhero.enderrelay.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
//import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
//import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
//import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;
import com.aeonhero.enderrelay.recipe.EnderRelayRecipe;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import static com.aeonhero.enderrelay.block.ModBlocks.ENDER_RELAY_BLOCK;

@Mod(EnderRelay.MODID)
public class EnderRelay {
    public static final String MODID = "enderrelay";
    private static final Logger LOGGER = LogUtils.getLogger();

    public EnderRelay()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipes.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);

        DispenserBlock.registerBehavior(Items.END_CRYSTAL, new OptionalDispenseItemBehavior(){

            //@Override
            public @NotNull ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.getBlockState().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.getPos().relative(direction);
                ServerLevel level = blockSource.getLevel();
                BlockState blockState = level.getBlockState(blockPos);
                this.setSuccess(true);
                if (blockState.is(ENDER_RELAY_BLOCK.get())) {
                    if (!blockState.getValue(EnderRelayBlock.CHARGED)) {
                        EnderRelayBlock.light(null, level, blockPos, blockState);
                        itemStack.shrink(1);
                    } else {
                        this.setSuccess(false);
                    }
                    return itemStack;
                }
                return super.execute(blockSource, itemStack);
            }
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        //Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(MOD_ID, "ender_relay"), EnderRelayRecipe.SERIALIZER);
    }

    /*public static final Block ENDER_RELAY_BLOCK = new EnderRelayBlock(FabricBlockSettings.create()
            .mapColor(MapColor.COLOR_BLACK)
            .instrument(NoteBlockInstrument.BASEDRUM)
            .requiresCorrectToolForDrops()
            .strength(50.0f, 1200.0f)
            .lightLevel(blockState -> blockState.getValue(EnderRelayBlock.CHARGED) ? 15 : 0)
    );
    public static final BlockItem ENDER_RELAY_ITEM = new BlockItem(ENDER_RELAY_BLOCK, new Item.Properties());
    public static final BlockEntityType<EnderRelayBlockEntity> ENDER_RELAY_BLOCK_ENTITY = Registry.register(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "ender_relay"),
            FabricBlockEntityTypeBuilder.create(EnderRelayBlockEntity::new, ENDER_RELAY_BLOCK).build()
    );*/

    //@Override
    /*public void onInitialize() {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(MOD_ID, "ender_relay"), ENDER_RELAY_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "ender_relay"), ENDER_RELAY_ITEM);
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER, new ResourceLocation(MOD_ID, "ender_relay"), EnderRelayRecipe.SERIALIZER);

        // Mimic respawn anchor functionality
        DispenserBlock.registerBehavior(Items.END_CRYSTAL, new OptionalDispenseItemBehavior(){

            @Override
            public @NotNull ItemStack execute(BlockSource blockSource, ItemStack itemStack) {
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockPos = blockSource.pos().relative(direction);
                ServerLevel level = blockSource.level();
                BlockState blockState = level.getBlockState(blockPos);
                this.setSuccess(true);
                if (blockState.is(ENDER_RELAY_BLOCK)) {
                    if (!blockState.getValue(EnderRelayBlock.CHARGED)) {
                        EnderRelayBlock.light(null, level, blockPos, blockState);
                        itemStack.shrink(1);
                    } else {
                        this.setSuccess(false);
                    }
                    return itemStack;
                }
                return super.execute(blockSource, itemStack);
            }
        });

    }*/
}
