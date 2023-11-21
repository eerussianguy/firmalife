package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.common.blocks.IWeatherable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.IForgeBlockExtension;

public class GreenhouseTrapDoorBlock extends TrapDoorBlock implements IWeatherable, IForgeBlockExtension
{
    private final ExtendedProperties properties;
    @Nullable private final Supplier<? extends Block> next;

    public GreenhouseTrapDoorBlock(ExtendedProperties properties, BlockSetType type, @Nullable Supplier<? extends Block> next)
    {
        super(properties.properties(), type);
        this.properties = properties;
        this.next = next;
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
        return adjacent.getBlock() instanceof GreenhouseTrapDoorBlock
            && !state.getValue(OPEN) && !adjacent.getValue(OPEN)
            && adjacent.getValue(FACING) == state.getValue(FACING)
            && adjacent.getValue(HALF) == state.getValue(HALF);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState)
    {
        return hasNext();
    }

    @Override
    public @Nullable Supplier<? extends Block> getNext()
    {
        return next;
    }

    @Override
    public ExtendedProperties getExtendedProperties()
    {
        return properties;
    }
}
