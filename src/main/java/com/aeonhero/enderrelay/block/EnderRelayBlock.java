package com.aeonhero.enderrelay.block;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RespawnAnchorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.dimension.BuiltinDimensionTypes;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Optional;

public class EnderRelayBlock extends Block implements EntityBlock {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final BooleanProperty CHARGED = BooleanProperty.create("charged");

    public EnderRelayBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.defaultBlockState().setValue(CHARGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CHARGED);
    }

    @Override
    public @NotNull InteractionResult use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
        ItemStack itemInHand = player.getItemInHand(interactionHand);
        if (itemInHand.is(Items.ENDER_PEARL) && !blockState.getValue(CHARGED)) {
            light(player, level, blockPos, blockState);
            if (!player.getAbilities().instabuild) {
                itemInHand.shrink(1);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (level.dimensionTypeId() == BuiltinDimensionTypes.END) {
            BlockEntity blockEntity = level.getBlockEntity(blockPos);
            if (!(blockEntity instanceof EnderRelayBlockEntity enderRelayEntity)) return InteractionResult.FAIL;

            if (!blockState.getValue(CHARGED)) return InteractionResult.FAIL;
            BlockState newState = blockState.setValue(CHARGED, false);
            level.setBlock(blockPos, newState, 3);
            level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(player, newState));

            if(!level.isClientSide) {
                if (enderRelayEntity.hasNoLocation()) {
                    player.displayClientMessage(
                            Component.translatable("enderrelay.unknown_destination"),
                            false
                    );
                    return InteractionResult.FAIL;
                }
                sendToLocation((ServerPlayer) player, (ServerLevel) level, enderRelayEntity.getX(), enderRelayEntity.getY(), enderRelayEntity.getZ());
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        } else if (blockState.getValue(CHARGED) && !level.isClientSide) {
            explode(level, blockPos);
            return InteractionResult.sidedSuccess(false);
        }

        return InteractionResult.FAIL;
    }

    // Copied from RespawnAnchorBlock for exact same functionality
    private void explode(Level level, final BlockPos pos) {
        level.removeBlock(pos, false);
        boolean bl = Direction.Plane.HORIZONTAL.stream().map(pos::relative).anyMatch(blockPos -> myIsWaterThatWouldFlow(blockPos, level));
        final boolean bl2 = bl || level.getFluidState(pos.above()).is(FluidTags.WATER);
        ExplosionDamageCalculator explosionDamageCalculator = new ExplosionDamageCalculator(){

            @Override
            public Optional<Float> getBlockExplosionResistance(Explosion explosion, BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, FluidState fluidState) {
                if (blockPos.equals(pos) && bl2) {
                    return Optional.of(Float.valueOf(Blocks.WATER.getExplosionResistance()));
                }
                return super.getBlockExplosionResistance(explosion, blockGetter, blockPos, blockState, fluidState);
            }
        };
        Vec3 vec3 = Vec3.atCenterOf(pos);
        level.explode(
                null,
                DamageSource.badRespawnPointExplosion(),
                explosionDamageCalculator,
                vec3.x,
                vec3.y,
                vec3.z,
                5.0f,
                true, Explosion.BlockInteraction.BREAK);
    }

    public static void light(@Nullable Entity entity, Level level, BlockPos blockPos, BlockState blockState) {
        BlockState newState = blockState.setValue(CHARGED, true);
        level.setBlock(blockPos, newState, 3);
        level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, newState));
        level.playSound(null, blockPos, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.BLOCKS, 1.0f, 1.0f); // TODO: Better sound effects (if you want to do something and can do some sound effect stuff, dm me)
    }

    private static boolean myIsWaterThatWouldFlow(BlockPos p_55888_, Level p_55889_) {
        FluidState fluidstate = p_55889_.getFluidState(p_55888_);
        if (!fluidstate.is(FluidTags.WATER)) {
            return false;
        } else if (fluidstate.isSource()) {
            return true;
        } else {
            float f = (float)fluidstate.getAmount();
            if (f < 2.0F) {
                return false;
            } else {
                FluidState fluidstate1 = p_55889_.getFluidState(p_55888_.below());
                return !fluidstate1.is(FluidTags.WATER);
            }
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState blockState, Player player) {
        ItemStack itemInMainHand = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, itemInMainHand) == 0) {
            BlockEntity blockEntity = level.getBlockEntity(pos);

            if (!(blockEntity instanceof EnderRelayBlockEntity enderRelayEntity))
                super.playerWillDestroy(level, pos, blockState, player);
            else if(enderRelayEntity.hasNoLocation())
                super.playerWillDestroy(level, pos, blockState, player);
            else
            {
                ItemStack compass = new ItemStack(Items.COMPASS, 1);
                //TO FIX:
                myAddLodestoneTags(level.dimension(), new BlockPos(enderRelayEntity.getX(), enderRelayEntity.getY(), enderRelayEntity.getZ()), compass.getOrCreateTag());
                popResource(level, pos, compass);
                super.playerWillDestroy(level, pos, blockState, player);
            }
        }
        else super.playerWillDestroy(level, pos, blockState, player);
    }

    private void myAddLodestoneTags(ResourceKey<Level> p_40733_, BlockPos p_40734_, CompoundTag p_40735_) {
        p_40735_.put("LodestonePos", NbtUtils.writeBlockPos(p_40734_));
        Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, p_40733_).resultOrPartial(LOGGER::error).ifPresent((p_40731_) -> {
            p_40735_.put("LodestoneDimension", p_40731_);
        });
        p_40735_.putBoolean("LodestoneTracked", true);
    }

    public static void sendToLocation(ServerPlayer player, ServerLevel level, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        Optional<Vec3> pos = RespawnAnchorBlock.findStandUpPosition(EntityType.PLAYER, level, blockPos);

        if (pos.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("enderrelay.obstructed_destination"),
                    false
            );
            return;
        }

        // pasted code from ServerList#respawn to make it the most vanilla thing possible
        float g;
        BlockState blockState = level.getBlockState(blockPos);
        boolean isLodestone = blockState.is(Blocks.LODESTONE);
        Vec3 vec3 = pos.get();
        if (isLodestone) {
            Vec3 vec32 = Vec3.atBottomCenterOf(blockPos).subtract(vec3).normalize();
            g = (float) Mth.wrapDegrees(Mth.atan2(vec32.z, vec32.x) * 57.2957763671875 - 90.0);
        } else {
            player.displayClientMessage(
                    Component.translatable("enderrelay.no_lodestone"),
                    false
            );
            return;
        }


        level.playSound(null, player.getOnPos(), SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0f, 1.0f); // TODO: Better sound effects (if you want to do something and can do some sound effect stuff, dm me)

        player.teleportTo(level, vec3.x, vec3.y, vec3.z, g, 0.0f);

        // copied from PlayerList line 427
        while (!level.noCollision(player) && player.getY() < (double)level.getMaxBuildHeight()) {
            player.setPos(player.getX(), player.getY() + 1.0, player.getZ());
        }

        player.teleportTo(level, player.getX(), player.getY(), player.getZ(), g, 0.0f);

        level.playSound(null, vec3.x, vec3.y, vec3.z, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0f, 1.0f); // TODO: Better sound effects (if you want to do something and can do some sound effect stuff, dm me)
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EnderRelayBlockEntity(pos, state);
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos blockPos, RandomSource randomSource) {
        if (blockState.getValue(CHARGED)) {
            // Mostly copied & modified from NetherPortalBlock
            double d = (double)blockPos.getX() + randomSource.nextDouble();
            double e = (double)blockPos.getY() + randomSource.nextDouble();
            double f = (double)blockPos.getZ() + randomSource.nextDouble();
            double g = ((double)randomSource.nextFloat() - 0.5) * 0.5;
            double h = ((double)randomSource.nextFloat() - 0.5) * 0.5;
            double j = ((double)randomSource.nextFloat() - 0.5) * 0.5;

            level.addParticle(ParticleTypes.PORTAL, d, e, f, g, h, j);
        }
    }
}
