package com.eerussianguy.firmalife.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import com.eerussianguy.firmalife.common.FLTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;
import net.dries007.tfc.util.Helpers;

public class GlassSlabBlock extends SlabBlock implements IForgeBlockExtension
{
    private final ExtendedProperties properties;

    public GlassSlabBlock(ExtendedProperties properties)
    {
        super(properties.properties());
        this.properties = properties;
    }

    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader, BlockPos pPos, CollisionContext pContext)
    {
        return Shapes.empty();
    }

    @Override
    @SuppressWarnings("deprecation")
    public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos)
    {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean skipRendering(BlockState state, BlockState adjacent, Direction side)
    {
        return Helpers.isBlock(adjacent, FLTags.Blocks.GREENHOUSE) || super.skipRendering(state, adjacent, side);
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
