package com.eerussianguy.firmalife.common.items;

import java.util.Map;
import com.eerussianguy.firmalife.common.blocks.greenhouse.SprinklerPipeBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SprinklerItem extends BlockItem
{
    private final Block floorBlock;

    public SprinklerItem(Block block, Block floorBlock, Properties properties)
    {
        super(block, properties);
        this.floorBlock = floorBlock;
    }

    @Nullable
    @Override
    protected BlockState getPlacementState(BlockPlaceContext context)
    {
        final Level level = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (Direction dir : context.getNearestLookingDirections())
        {
            cursor.setWithOffset(pos, dir);
            if (isPipe(level, cursor))
            {
                if (dir == Direction.UP)
                {
                    return super.getPlacementState(context);
                }
                else if (dir == Direction.DOWN)
                {
                    return getPlacementState(floorBlock, context);
                }
            }
        }
        return null;
    }


    @Override
    public void registerBlocks(Map<Block, Item> map, Item item)
    {
        super.registerBlocks(map, item);
        map.put(this.floorBlock, item);
    }

    @Override
    public void removeFromBlockToItemMap(Map<Block, Item> map, Item item)
    {
        super.removeFromBlockToItemMap(map, item);
        map.remove(this.floorBlock);
    }

    @Nullable
    private BlockState getPlacementState(Block block, BlockPlaceContext context)
    {
        final BlockState state = block.getStateForPlacement(context);
        return state != null && this.canPlace(context, state) ? state : null;
    }

    private boolean isPipe(Level level, BlockPos pos)
    {
        return level.getBlockState(pos).getBlock() instanceof SprinklerPipeBlock;
    }
}
