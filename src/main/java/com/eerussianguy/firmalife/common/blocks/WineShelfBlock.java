package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;

public class WineShelfBlock extends FourWayDeviceBlock
{
    public static final VoxelShape[] SHAPES = Helpers.computeHorizontalShapes(dir -> Shapes.or(
        Shapes.join(
            Shapes.block(),
            Helpers.rotateShape(dir, 1, 1, 0, 15, 15, 15), BooleanOp.ONLY_FIRST
        ),
        // Middle shelf
        box(0, 7, 0, 16, 9, 16),
        // Vertical seperator
        box(7, 0, 1, 9, 16, 16)
    ));

    public WineShelfBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.WINE_SHELF, (shelf, inv) -> {
            int slot = getSlotFromPos(state, result.getLocation().subtract(pos.getX(), pos.getY(), pos.getZ()));
            ItemStack slotItem = inv.getStackInSlot(slot);
            if (Helpers.isItem(held, FLTags.Items.WINE_BOTTLES))
            {
                Helpers.playSound(level, pos, SoundEvents.GLASS_PLACE);
                if (slotItem.isEmpty())
                {
                    //Insert
                    return FLHelpers.insertOne(level, held, slot, inv, player);
                }
                else
                {
                    //Swap with hand
                    //TODO this could be optimized
                    FLHelpers.takeOne(level, slot, inv, player);
                    return FLHelpers.insertOne(level, held, slot, inv, player);
                }
            }
            else if (held.isEmpty() && !slotItem.isEmpty())
            {
                //Extract
                Helpers.playSound(level, pos, SoundEvents.GLASS_PLACE);
                return FLHelpers.takeOne(level, slot, inv, player);
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

    private int getSlotFromPos(BlockState state, Vec3 pos)
    {
        int slot = 0;
        if ((state.getValue(FACING).getAxis().equals(Direction.Axis.Z) ? pos.x : pos.z) < .5f)
        {
            slot += 2;
        }
        if (pos.y < 0.5f)
        {
            slot += 1;
        }
        return slot;
    }
}
