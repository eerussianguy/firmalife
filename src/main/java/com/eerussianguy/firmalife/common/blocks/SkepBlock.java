package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.blockentities.SkepBlockEntity;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.component.size.IItemSize;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;
import net.dries007.tfc.util.Helpers;

public class SkepBlock extends BaseBeehiveBlock implements IItemSize
{
    public static final VoxelShape SHAPE = Shapes.or(
        box(3, 0, 3, 13, 3, 13),
        box(4, 3, 4, 12, 6, 12),
        box(5, 6, 5, 11, 7, 11),
        box(7, 7, 7, 9, 8, 9)
    );

    public SkepBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (state.getValue(HONEY))
        {
            if (shouldAnger(level, pos) && !player.isCreative())
            {
                attack(player);
            }
            else
            {
                ItemHandlerHelper.giveItemToPlayer(player, FLItems.FOODS.get(FLFood.RAW_HONEY).get().getDefaultInstance());
                Helpers.playSound(level, pos, SoundEvents.BOTTLE_FILL);
                level.setBlockAndUpdate(pos, state.cycle(HONEY));
            }
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        else if (held.getItem() == FLItems.FOODS.get(FLFood.RAW_HONEY).get())
        {
            Helpers.playSound(level, pos, SoundEvents.HONEY_BLOCK_PLACE);
            level.setBlockAndUpdate(pos, state.cycle(HONEY));
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public Size getSize(ItemStack itemStack)
    {
        return Size.HUGE;
    }

    @Override
    public Weight getWeight(ItemStack itemStack)
    {
        return isFilled(itemStack) ? Weight.VERY_HEAVY : Weight.HEAVY;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader level, BlockPos pos, Player player)
    {
        final ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        final BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof SkepBlockEntity skep && skep.getBee().hasQueen())
        {
            skep.saveToItem(stack, level.registryAccess());
            modifyWeight(stack);
        }
        return stack;
    }

    public boolean isFilled(ItemStack stack)
    {
        return stack.has(FLComponents.BEE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}
