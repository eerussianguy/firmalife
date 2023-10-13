package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.SquirtingMoistureTransducerBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;

public class SquirtingMoistureTransducerBlock extends BottomSupportedDeviceBlock implements HoeOverlayBlock
{
    public static final BooleanProperty STASIS = FLStateProperties.STASIS;

    public SquirtingMoistureTransducerBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP, Shapes.block());
        registerDefaultState(getStateDefinition().any().setValue(STASIS, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(STASIS));
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        ItemStack held = player.getItemInHand(hand);
        if (held.getItem() == FLBlocks.EMBEDDED_PIPE.get().asItem())
        {
            FLHelpers.consumeInventory(level, pos, FLBlockEntities.SQUIRTING_MOISTURE_TRANSDUCER, (transducer, inv) -> {
                return FLHelpers.insertOne(level, held, 0, inv, player);
            });
        }
        return InteractionResult.PASS;
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState blockState, List<Component> text, boolean debug)
    {
        FLHelpers.readInventory(level, pos, FLBlockEntities.SQUIRTING_MOISTURE_TRANSDUCER, (transducer, inv) -> {
            ItemStack item = inv.getStackInSlot(0);
            if (item.isEmpty())
            {
                text.add(Component.translatable("firmalife.transducer.no_pipes"));
            }
            else
            {
                text.add(Component.translatable("firmalife.transducer.current_pipes", item.getCount()));
            }
            text.add(Component.translatable("firmalife.transducer.pipe_length", transducer.getCachedMoisture()));
            text.add(Component.translatable("firmalife.transducer.pipe_wanted", SquirtingMoistureTransducerBlockEntity.getMinPipes(level, pos)));
        });
    }

}
