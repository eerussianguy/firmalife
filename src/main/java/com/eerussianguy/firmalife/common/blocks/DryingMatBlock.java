package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.DryingMatBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.SimpleItemRecipeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.util.Helpers;

public class DryingMatBlock extends BottomSupportedDeviceBlock
{
    public static ItemInteractionResult use(Level level, BlockPos pos, Player player, ItemStack held)
    {
        if (level.getBlockEntity(pos) instanceof SimpleItemRecipeBlockEntity<?> mat)
        {
            final IItemHandler inv = Helpers.getCapability(Capabilities.ItemHandler.BLOCK, mat);
            if (inv != null)
            {
                if (inv.getStackInSlot(0).isEmpty() && !held.isEmpty())
                {
                    ItemInteractionResult res = FLHelpers.insertOne(level, held, 0, inv, player);
                    if (res.consumesAction())
                    {
                        mat.markForSync();
                        mat.start();
                    }
                    return res;
                }
                else if (!inv.getStackInSlot(0).isEmpty() && held.isEmpty() && player.isShiftKeyDown())
                {
                    mat.markForSync();
                    FLHelpers.roundCreationDate(inv.getStackInSlot(0));
                    return FLHelpers.takeOne(level, 0, inv, player);
                }
            }
            mat.markForSync();
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 2, 16);

    public DryingMatBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP, SHAPE);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos)
    {
        if (facingState.getBlock() instanceof PistonHeadBlock && facingState.getValue(PistonHeadBlock.FACING) == facing.getOpposite() && facing.getAxis().isHorizontal())
        {
            if (level.getBlockEntity(currentPos) instanceof DryingMatBlockEntity mat)
            {
                mat.ejectItem(facing.getOpposite());
            }
        }
        return super.updateShape(state, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return DryingMatBlock.use(level, pos, player, held);
    }
}
