package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.blockentities.VatBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.SealableDeviceBlock;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.util.Helpers;

public class VatBlock extends SealableDeviceBlock
{
    public static void toggleSeal(Level level, BlockPos pos, BlockState state)
    {
        if (level.getBlockEntity(pos) instanceof VatBlockEntity vat)
        {
            Helpers.playSound(level, pos, SoundEvents.STONE_PLACE);
            level.setBlockAndUpdate(pos, state.setValue(SEALED, !state.getValue(SEALED)));
            vat.markForSync();
        }
    }

    public static final VoxelShape SHAPE = Shapes.or(
        box(1, 0, 1, 15, 1, 15),
        box(1, 1, 1, 15, 12, 2),
        box(1, 1, 14, 15, 12, 15),
        box(1, 1, 1, 2, 12, 14),
        box(14, 1, 1, 15, 12, 14)
    );

    public static final VoxelShape SEALED_SHAPE = Shapes.or(
        SHAPE,
        box(0, 12, 0, 16, 13, 16),
        box(5, 13, 5, 11, 14, 11)
    );

    public VatBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (level.getBlockEntity(pos) instanceof VatBlockEntity vat)
        {
            handleNeighborChanged(state, level, pos, () -> toggleSeal(level, pos, state), () -> toggleSeal(level, pos, state));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext)
    {
        return state.getValue(SEALED) ? SEALED_SHAPE : SHAPE;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand)
    {
        if (level.getBlockEntity(pos) instanceof VatBlockEntity vat && vat.isBoiling())
        {
            final double x = pos.getX() + 0.5;
            final double y = pos.getY();
            final double z = pos.getZ() + 0.5;
            for (int i = 0; i < rand.nextInt(5) + 4; i++)
            {
                level.addParticle(TFCParticles.BUBBLE.get(), false, x + rand.nextFloat() * 0.375 - 0.1875, y + 0.751f, z + rand.nextFloat() * 0.375 - 0.1875, 0, 0.05D, 0);
            }
            level.addParticle(TFCParticles.STEAM.get(), false, x, y + 0.8, z, Helpers.triangle(rand), 0.5, Helpers.triangle(rand));
            level.playLocalSound(x, y, z, SoundEvents.WATER_AMBIENT, SoundSource.BLOCKS, 1.0F, rand.nextFloat() * 0.7F + 0.4F, false);
        }
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (level.getBlockEntity(pos) instanceof VatBlockEntity vat)
        {
            if (!vat.isBoiling())
            {
                if (vat.hasOutput())
                {
                    if (Helpers.isItem(stack, TFCTags.Items.EMPTY_JARS))
                    {
                        stack.shrink(1);
                        ItemHandlerHelper.giveItemToPlayer(player, vat.takeOutput());
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                    return ItemInteractionResult.FAIL;
                }
                if (stack.isEmpty() && player.isShiftKeyDown())
                {
                    toggleSeal(level, pos, state);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                if (!state.getValue(SEALED))
                {
                    if (FluidHelpers.transferBetweenBlockEntityAndItem(stack, vat, player, hand))
                    {
                        vat.markForSync();
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                    else
                    {
                        final VatBlockEntity.VatInventory inventory = vat.getInventory();
                        if (inventory.isItemValid(0, stack) && !stack.isEmpty())
                        {
                            player.setItemInHand(hand, inventory.insertItem(0, stack, false));
                            vat.markForSync();
                        }
                        else
                        {
                            final ItemStack give = inventory.extractItem(0, 64, false);
                            vat.markForSync();
                            if (give.isEmpty()) return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
                            ItemHandlerHelper.giveItemToPlayer(player, give);
                        }
                        return ItemInteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

}
