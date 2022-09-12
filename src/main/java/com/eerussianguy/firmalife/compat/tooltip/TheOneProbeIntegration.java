package com.eerussianguy.firmalife.compat.tooltip;

import java.util.Locale;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.common.FLHelpers;
import mcjty.theoneprobe.api.*;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltip;
import net.dries007.tfc.compat.jade.common.EntityTooltip;

public class TheOneProbeIntegration implements Function<ITheOneProbe, Void>
{
    @Override
    public Void apply(ITheOneProbe registry)
    {
        FLTooltips.BlockEntities.register((tooltip, aClass) -> register(registry, tooltip, aClass));
        FLTooltips.Entities.register((tooltip, aClass) -> register(registry, tooltip, aClass));
        return null;
    }

    private void register(ITheOneProbe top, BlockEntityTooltip tooltip, Class<? extends Block> blockClass)
    {
        top.registerProvider(new IProbeInfoProvider() {
            @Override
            public ResourceLocation getID()
            {
                return FLHelpers.identifier(blockClass.getSimpleName().toLowerCase(Locale.ROOT));
            }

            @Override
            public void addProbeInfo(ProbeMode probeMode, IProbeInfo info, Player player, Level level, BlockState blockState, IProbeHitData data)
            {
                if (data.getPos() != null && blockClass.isInstance(blockState.getBlock()))
                {
                    tooltip.display(level, blockState, data.getPos(), level.getBlockEntity(data.getPos()), info::text);
                }
            }
        });
    }

    private void register(ITheOneProbe top, EntityTooltip tooltip, Class<? extends Entity> entityClass)
    {
        top.registerEntityProvider(new IProbeInfoEntityProvider() {
            @Override
            public String getID()
            {
                return FLHelpers.identifier(entityClass.getSimpleName().toLowerCase(Locale.ROOT)).toString();
            }

            @Override
            public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo info, Player player, Level level, Entity entity, IProbeHitEntityData data)
            {
                if (entityClass.isInstance(entity))
                {
                    tooltip.display(level, entity, info::text);
                }
            }
        });
    }
}
