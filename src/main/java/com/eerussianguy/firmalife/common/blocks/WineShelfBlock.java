package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class WineShelfBlock extends FourWayDeviceBlock
{
    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Shapes.join(Shapes.block(), Helpers.rotateShape(dir, 1, 1, 0, 15, 15, 15), BooleanOp.ONLY_FIRST)
    ));

    public WineShelfBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.WINE_SHELF, (shelf, inv) -> {
            if (Helpers.isItem(held, FLTags.Items.WINE_BOTTLES))
            {
                Helpers.playSound(level, pos, SoundEvents.GLASS_PLACE);
                return FLHelpers.insertOneAny(level, held, 0, 3, shelf, player);
            }
            else if (held.isEmpty())
            {
                Helpers.playSound(level, pos, SoundEvents.GLASS_PLACE);
                return FLHelpers.takeOneAny(level, 0, 3, shelf, player);
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        });
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPES[state.getValue(FACING).get2DDataValue()];
    }

    @Override
    public int getLightBlock(BlockState pState, BlockGetter pLevel, BlockPos pPos)
    {
        return 0;
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos)
    {
        return 1f;
    }
}
