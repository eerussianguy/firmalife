package com.eerussianguy.firmalife.compat.tooltip;

import java.util.function.BiConsumer;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.*;
import com.eerussianguy.firmalife.common.blocks.*;
import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltip;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltips;
import net.dries007.tfc.compat.jade.common.EntityTooltip;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

public final class FLTooltips
{
    public static final class BlockEntities
    {
        public static void register(BiConsumer<BlockEntityTooltip, Class<? extends Block>> r)
        {
            r.accept(PLANTERS, LargePlanterBlock.class);
            r.accept(BEEHIVE, FLBeehiveBlock.class);
            r.accept(DRYING_MAT, DryingMatBlock.class);
            r.accept(STRING, StringBlock.class);
            r.accept(CHEESE, CheeseWheelBlock.class);
            r.accept(OVEN_BOTTOM, OvenBottomBlock.class);
            r.accept(OVEN_TOP, OvenTopBlock.class);
        }

        public static final BlockEntityTooltip PLANTERS = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof LargePlanterBlock block && entity instanceof LargePlanterBlockEntity planter)
            {
                BlockEntityTooltips.hoeOverlay(level, block, planter, tooltip);
            }
        };

        public static final BlockEntityTooltip BEEHIVE = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof FLBeehiveBlock block && entity instanceof FLBeehiveBlockEntity hive)
            {
                BlockEntityTooltips.hoeOverlay(level, block, hive, tooltip);
            }
        };

        public static final BlockEntityTooltip DRYING_MAT = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof DryingMatBlock block && entity instanceof DryingMatBlockEntity mat)
            {
                tooltip.accept(Helpers.translatable("tfc.jade.time_left", delta(level, mat.getTicksLeft())));
            }
        };

        public static final BlockEntityTooltip STRING = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof StringBlock block && entity instanceof StringBlockEntity mat)
            {
                tooltip.accept(Helpers.translatable("tfc.jade.time_left", delta(level, mat.getTicksLeft())));
            }
        };

        public static final BlockEntityTooltip CHEESE = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof CheeseWheelBlock block)
            {
                if (state.getValue(CheeseWheelBlock.AGING))
                {
                    tooltip.accept(Helpers.translatable("firmalife.jade.aging"));
                }
                else
                {
                    tooltip.accept(Helpers.translatable("firmalife.jade.not_aging"));
                }
                tooltip.accept(Helpers.translatable("firmalife.jade.slices", state.getValue(CheeseWheelBlock.COUNT)));
                tooltip.accept(Helpers.translatable("firmalife.jade.food_age", FLHelpers.translateEnum(state.getValue(CheeseWheelBlock.AGE))));
            }
        };

        public static final BlockEntityTooltip OVEN_BOTTOM = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof OvenBottomBlock block && entity instanceof OvenBottomBlockEntity oven)
            {
                BlockEntityTooltips.heat(tooltip, oven.getTemperature());
                if (state.getBlock() instanceof ICure cure && !cure.isCured())
                {
                    tooltip.accept(Helpers.translatable("firmalife.jade.cure_time_left", delta(level, OvenBottomBlockEntity.CURE_TICKS - oven.getCureTicks())));
                }
            }
        };

        public static final BlockEntityTooltip OVEN_TOP = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof OvenTopBlock block && entity instanceof OvenTopBlockEntity oven)
            {
                BlockEntityTooltips.heat(tooltip, oven.getTemperature());
            }
        };

    }

    public static final class Entities
    {
        public static void register(BiConsumer<EntityTooltip, Class<? extends Entity>> r)
        {

        }
    }

    private static MutableComponent delta(Level level, long ticks)
    {
        return Calendars.get(level).getTimeDelta(ticks);
    }
}
