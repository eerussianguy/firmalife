package com.eerussianguy.firmalife.common.blocks.greenhouse;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraftforge.items.CapabilityItemHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.util.Plantable;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;

public class LargePlanterBlock extends DeviceBlock
{
    public static final BooleanProperty WATERED = FLStateProperties.WATERED;

    private static final VoxelShape LARGE_SHAPE = box(0, 0, 0, 16, 8, 16);

    public LargePlanterBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(WATERED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final ItemStack held = player.getItemInHand(hand);
        final Plantable plant = Plantable.get(held);
        final int slot = getUseSlot(hit, pos);
        if (plant != null && plant.isLarge())
        {
            return insertSlot(level, pos, held, player, slot);
        }
        else if (player.isShiftKeyDown() && held.isEmpty())
        {
            return takeSlot(level, pos, player, slot);
        }
        return InteractionResult.PASS;
    }

    protected int getUseSlot(BlockHitResult hit, BlockPos pos)
    {
        return 0;
    }

    private InteractionResult insertSlot(Level level, BlockPos pos, ItemStack held, Player player, int slot)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LargePlanterBlockEntity planter)
        {
            return planter.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inv ->
                FLHelpers.insertOne(level, held, slot, inv, player)
            ).orElse(InteractionResult.PASS);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult takeSlot(Level level, BlockPos pos, Player player, int slot)
    {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LargePlanterBlockEntity planter)
        {
            return planter.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).map(inv -> {
                Plantable plant = planter.getPlantable(slot);
                if (plant != null && planter.getGrowth(slot) >= 1)
                {
                    return FLHelpers.takeOne(level, slot, inv, player);
                }
                return InteractionResult.PASS;
            }).orElse(InteractionResult.PASS);
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(WATERED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return LARGE_SHAPE;
    }
}
