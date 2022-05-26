package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.Random;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.util.GreenhouseType;
import com.eerussianguy.firmalife.common.util.Mechanics;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import org.jetbrains.annotations.Nullable;

import static com.eerussianguy.firmalife.Firmalife.MOD_ID;

public class ClimateStationBlock extends DeviceBlock
{
    private static void denyAll(Level level, BlockPos pos)
    {
        level.getBlockEntity(pos, FLBlockEntities.CLIMATE_STATION.get()).ifPresent(station -> station.updateValidity(false, 0));
    }

    @Nullable
    private static Mechanics.GreenhouseInfo check(Level level, BlockPos pos, BlockState state)
    {
        final Mechanics.GreenhouseInfo info = Mechanics.getGreenhouse(level, pos, state);
        if (info != null)
        {
            final Set<BlockPos> positions = info.positions();
            level.getBlockEntity(pos, FLBlockEntities.CLIMATE_STATION.get()).ifPresent(station -> {
                station.setPositions(positions);
                station.updateValidity(true, info.type().tier);
            });
            updateState(level, pos, state, true);
            return info;
        }
        else
        {
            denyAll(level, pos);
            updateState(level, pos, state, false);
            return null;
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
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        Mechanics.GreenhouseInfo info = check(level, pos, state);
        if (info == null)
        {
            return InteractionResult.PASS;
        }
        else
        {
            Set<BlockPos> positions = info.positions();
            player.displayClientMessage(new TranslatableComponent(MOD_ID + ".greenhouse.found", positions.size()), true);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, Random random)
    {
        if (random.nextInt(5) == 0)
        {
            super.randomTick(state, level, pos, random); // causes a block tick
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void tick(BlockState state, ServerLevel level, BlockPos pos, Random random)
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
}
