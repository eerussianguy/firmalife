package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import com.eerussianguy.firmalife.common.blockentities.ClimateStationBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.ClimateType;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.util.FLAdvancements;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import com.eerussianguy.firmalife.common.util.Mechanics;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;

import static com.eerussianguy.firmalife.FirmaLife.*;

public class ClimateStationBlock extends DeviceBlock implements HoeOverlayBlock
{
    private static void denyAll(Level level, BlockPos pos)
    {
        level.getBlockEntity(pos, FLBlockEntities.CLIMATE_STATION.get()).ifPresent(station -> station.updateValidity(false, 0));
    }

    @Nullable
    private static Either<Mechanics.GreenhouseInfo, Set<BlockPos>> check(Level level, BlockPos pos, BlockState state)
    {
        final Mechanics.GreenhouseInfo info = Mechanics.getGreenhouse(level, pos, state);
        if (info != null)
        {
            final Set<BlockPos> positions = info.positions();
            level.getBlockEntity(pos, FLBlockEntities.CLIMATE_STATION.get()).ifPresent(station -> {
                station.setPositions(positions);
                station.updateValidity(true, info.type().tier());
                station.setType(ClimateType.GREENHOUSE);
            });
            updateState(level, pos, state, true);
            return Either.left(info);
        }
        else
        {
            Set<BlockPos> cellarPositions = Mechanics.getCellar(level, pos, state);
            if (cellarPositions != null)
            {
                level.getBlockEntity(pos, FLBlockEntities.CLIMATE_STATION.get()).ifPresent(station -> {
                    station.setPositions(cellarPositions);
                    station.updateValidity(true, 0);
                    station.setType(ClimateType.CELLAR);
                });
                updateState(level, pos, state, true);
                return Either.right(cellarPositions);
            }
            else
            {
                denyAll(level, pos);
                updateState(level, pos, state, false);
                return null;
            }
        }
    }

    private static void updateState(Level level, BlockPos pos, BlockState state, boolean valid)
    {
        if (state.getValue(STASIS) != valid)
        {
            level.setBlockAndUpdate(pos, state.setValue(STASIS, valid));
        }
    }

    public static final BooleanProperty STASIS = FLStateProperties.STASIS;

    public ClimateStationBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(STASIS, false));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        // set the fallback type if we can
        final boolean willConsumeAction = level.getBlockEntity(pos) instanceof ClimateStationBlockEntity station && station.setFavorite(held);
        final Either<Mechanics.GreenhouseInfo, Set<BlockPos>> either = check(level, pos, state);
        if (either == null)
        {
            return willConsumeAction ? ItemInteractionResult.sidedSuccess(level.isClientSide) : ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        else
        {
            either.ifLeft(info -> {
                if (info.positions().size() > 200 && player instanceof ServerPlayer server && Objects.requireNonNull(GreenhouseType.MANAGER.getId(info.type())).toString().contains("stainless_steel"))
                {
                    FLAdvancements.BIG_STAINLESS_GREENHOUSE.trigger(server);
                }
                player.displayClientMessage(Component.translatable(MOD_ID + ".greenhouse.found", info.type().getTitle(), info.positions().size()), true);
            });
            either.ifRight(positions -> {
                if (positions.size() > 200 && player instanceof ServerPlayer server)
                {
                    FLAdvancements.BIG_CELLAR.trigger(server);
                }
                player.displayClientMessage(Component.translatable(MOD_ID + ".cellar.found", positions.size()), true);
            });
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (random.nextInt(4) == 0)
        {
            super.randomTick(state, level, pos, random); // causes a block tick
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        check(level, pos, state);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);
        level.scheduleTick(pos, this, 1);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        denyAll(level, pos);
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(STASIS);
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> tooltip, boolean debug)
    {
        if (level.getBlockEntity(pos) instanceof ClimateStationBlockEntity station)
        {
            if (station.getFavoriteType() != null)
            {
                tooltip.accept(Component.translatable("firmalife.greenhouse.expects", station.getFavoriteType().getTitle()));
            }
            else if (station.favoriteIsCellar())
            {
                tooltip.accept(Component.translatable("firmalife.cellar.expects"));
            }
        }
    }
}
