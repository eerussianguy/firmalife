package com.eerussianguy.firmalife.common.blocks;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.misc.FLDamageSources;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blockentities.AbstractFirepitBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.items.Powder;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;

public class AshtrayBlock extends DeviceBlock
{
    public static final IntegerProperty STAGE = TFCBlockStateProperties.STAGE_10;

    public AshtrayBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(STAGE, 0));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        return FLHelpers.consumeInventory(level, pos, FLBlockEntities.ASHTRAY, (ashtray, inv) -> {
            final ItemStack stack = inv.extractItem(0, player.isShiftKeyDown() ? Integer.MAX_VALUE : 1, false);
            if (!stack.isEmpty())
            {
                ItemHandlerHelper.giveItemToPlayer(player, stack);
                ashtray.updateBlockState();
                Helpers.playSound(level, pos, SoundEvents.SAND_PLACE);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            return InteractionResult.PASS;
        });
    }

    @Override
    @SuppressWarnings("deprecation")
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (level.getBlockEntity(pos.below()) instanceof AbstractFirepitBlockEntity<?> firepit && firepit.getTemperature() > 0 && state.getValue(STAGE) > 0)
        {
            level.explode(null, FLDamageSources.ASH, null, pos.getX(), pos.getY(), pos.getZ(), 10f, true, Level.ExplosionInteraction.BLOCK);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void attack(BlockState state, Level level, BlockPos pos, Player player)
    {
        final ItemStack held = player.getMainHandItem();
        if (Helpers.isItem(held, TFCItems.POWDERS.get(Powder.WOOD_ASH).get()))
        {
            FLHelpers.readInventory(level, pos, FLBlockEntities.ASHTRAY, (ashtray, inv) -> {
                var res = FLHelpers.insertOne(level, held, 0, inv, player);
                if (res.consumesAction())
                {
                    Helpers.playSound(level, pos, SoundEvents.SAND_PLACE);
                }
                ashtray.updateBlockState();
            });
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STAGE));
    }
}
