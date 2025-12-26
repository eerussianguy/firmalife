package com.eerussianguy.firmalife.common.blocks.bee;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;

import net.dries007.tfc.common.blocks.ExtendedProperties;

public class WoodenBeehiveBlock extends BaseBeehiveBlock
{
    public static final BooleanProperty OPEN = BlockStateProperties.OPEN;

    public WoodenBeehiveBlock(ExtendedProperties properties)
    {
        super(properties);
        registerDefaultState(getStateDefinition().any().setValue(HONEY, false).setValue(BEES, false).setValue(OPEN, false));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (player.isCreative() && held.getItem() == Items.BEDROCK && !level.isClientSide)
        {
            level.getBlockEntity(pos, FLBlockEntities.BEEHIVE.get()).ifPresent(hive -> hive.setBeeData(BeeComponent.getWildBee(level, pos)));
            return ItemInteractionResult.SUCCESS;
        }
        if (!state.getValue(OPEN) || (player.isShiftKeyDown() && held.isEmpty()))
        {
            if (!level.getBlockState(pos.above()).isAir())
            {
                return ItemInteractionResult.FAIL;
            }
            level.setBlock(pos, state.setValue(OPEN, !state.getValue(OPEN)), 3);
            level.getBlockEntity(pos, FLBlockEntities.BEEHIVE.get()).ifPresent(IBlockEntityExtension::requestModelDataUpdate);
            return ItemInteractionResult.SUCCESS;
        }
        if (state.getValue(OPEN))
        {
            return super.useItemOn(held, state, level, pos, player, hand, result);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(OPEN));
    }

}
