package com.eerussianguy.firmalife.common.blocks;

import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import com.eerussianguy.firmalife.common.util.GreenhouseType;
import com.eerussianguy.firmalife.common.util.Mechanics;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;

public class ClimateStationBlock extends DeviceBlock
{
    public ClimateStationBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final Mechanics.GreenhouseInfo info = Mechanics.getGreenhouse(level, pos, state);
        if (info != null)
        {
            final Set<BlockPos> positions = info.positions();
            final GreenhouseType greenhouse = info.type();
            player.displayClientMessage(new TextComponent("Found " + greenhouse.id + " greenhouse of " + positions.size() + " blocks"), true);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
