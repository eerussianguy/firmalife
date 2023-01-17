package com.eerussianguy.firmalife.compat.tooltip;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.*;
import com.eerussianguy.firmalife.common.blocks.*;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.config.FLConfig;

import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.blocks.plant.fruit.FruitTreeSaplingBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltip;
import net.dries007.tfc.compat.jade.common.BlockEntityTooltips;
import net.dries007.tfc.compat.jade.common.EntityTooltip;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.compat.jade.common.BlockEntityTooltips.*;

public final class FLTooltips
{
    public static final class BlockEntities
    {
        public static void register(BiConsumer<BlockEntityTooltip, Class<? extends Block>> r)
        {
            r.accept(DRYING_MAT, DryingMatBlock.class);
            r.accept(DRYING_MAT, SolarDrierBlock.class);
            r.accept(STRING, StringBlock.class);
            r.accept(CHEESE, CheeseWheelBlock.class);
            r.accept(OVEN, OvenBottomBlock.class);
            r.accept(OVEN, OvenTopBlock.class);
            r.accept(SHELF_OR_HANGER, FoodShelfBlock.class);
            r.accept(SHELF_OR_HANGER, HangerBlock.class);
        }

        public static final BlockEntityTooltip DRYING_MAT = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof DryingMatBlock block && entity instanceof DryingMatBlockEntity mat && !mat.viewStack().isEmpty())
            {
                if (mat.getCachedRecipe() != null)
                {
                    tooltip.accept(Helpers.translatable("tfc.jade.time_left", delta(level, mat.getTicksLeft())));
                }
            }
        };

        public static final BlockEntityTooltip STRING = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof StringBlock block && entity instanceof StringBlockEntity mat)
            {
                final ItemStack item = mat.viewStack();
                if (!item.isEmpty())
                {
                    final IFood food = item.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
                    final List<FoodTrait> traits = food == null ? null : food.getTraits();
                    if (mat.getCachedRecipe() != null)
                    {
                        tooltip.accept(Helpers.translatable("tfc.jade.time_left", delta(level, mat.getTicksLeft())));
                    }
                    else if (traits != null)
                    {
                        final List<Component> text = new ArrayList<>();
                        if (traits.contains(FLFoodTraits.SMOKED))
                        {
                            FLFoodTraits.SMOKED.addTooltipInfo(item, text);
                        }
                        if (traits.contains(FLFoodTraits.RANCID_SMOKED))
                        {
                            FLFoodTraits.RANCID_SMOKED.addTooltipInfo(item, text);
                        }
                        if (!text.isEmpty())
                        {
                            text.forEach(tooltip);
                        }
                    }

                }
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

        public static final BlockEntityTooltip SHELF_OR_HANGER = (level, state, pos, entity, tooltip) -> {
            if (level.getBlockEntity(pos) instanceof FoodShelfBlockEntity shelf)
            {
                if (shelf.isClimateValid())
                {
                    tooltip.accept(Helpers.translatable("firmalife.cellar.valid_block"));
                }
                else
                {
                    tooltip.accept(Helpers.translatable("firmalife.cellar.invalid_block"));
                }
                shelf.getCapability(Capabilities.ITEM).ifPresent(inv -> {
                    ItemStack stack = inv.getStackInSlot(0);
                    stack.getCapability(FoodCapability.CAPABILITY).ifPresent(food -> {
                        List<Component> foodTooltip = new ArrayList<>();
                        food.addTooltipInfo(stack, foodTooltip);
                        foodTooltip.forEach(tooltip);
                    });
                });
            }
        };

        public static final BlockEntityTooltip OVEN = (level, state, pos, entity, tooltip) -> {
            if (entity instanceof OvenLike oven)
            {
                BlockEntityTooltips.heat(tooltip, oven.getTemperature());
                if (state.getBlock() instanceof ICure cure && !cure.isCured())
                {
                    if (oven.getTemperature() < FLConfig.SERVER.ovenCureTemperature.get())
                    {
                        tooltip.accept(Helpers.translatable("firmalife.jade.cannot_cure"));
                    }
                    else
                    {
                        tooltip.accept(Helpers.translatable("firmalife.jade.cure_time_left", delta(level, FLConfig.SERVER.ovenCureTicks.get() - oven.getCureTicks())));
                    }
                }
                if (entity instanceof OvenTopBlockEntity top)
                {
                    entity.getCapability(Capabilities.ITEM).ifPresent(inv -> {
                        for (int i = 0; i < inv.getSlots(); i++)
                        {
                            final int ticksLeft = top.getTicksLeft(i);
                            if (ticksLeft > 0)
                            {
                                tooltip.accept(Helpers.translatable("firmalife.jade.cook_left", delta(level, ticksLeft)));
                            }
                        }
                    });
                }

            }
        };

        public static final BlockEntityTooltip FRUIT_TREE_SAPLING = (level, state, pos, entity, tooltip) -> {
            if (entity instanceof TickCounterBlockEntity counter && state.getBlock() instanceof FruitTreeSaplingBlock sapling)
            {
                timeLeft(level, tooltip, (long) (sapling.getTreeGrowthDays() * ICalendar.TICKS_IN_DAY * TFCConfig.SERVER.globalFruitSaplingGrowthModifier.get()) - counter.getTicksSinceUpdate(), Helpers.translatable("tfc.jade.ready_to_grow"));
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
