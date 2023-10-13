package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.JarringStationBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class JarringStationBlock extends FourWayDeviceBlock
{
    public static final VoxelShape SHAPE = box(0, 0, 0, 16, 12, 16);

    public JarringStationBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return FLHelpers.consumeInventory(level, pos, FLBlockEntities.JARRING_STATION, (type, inv) -> {
            final ItemStack item = player.getItemInHand(hand);
            if (item.isEmpty())
            {
                return FLHelpers.takeOneAny(level, 0, JarringStationBlockEntity.SLOTS - 1, inv, player);
            }
            else if (Helpers.isItem(item, TFCItems.EMPTY_JAR.get()))
            {
                return FLHelpers.insertOneAny(level, item, 0, JarringStationBlockEntity.SLOTS - 1, inv, player);
            }
            return InteractionResult.PASS;
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
