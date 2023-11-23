package com.eerussianguy.firmalife.compat.tooltip;

import java.util.ArrayList;
import java.util.List;

import com.eerussianguy.firmalife.common.blocks.plant.FLFruitTreeSaplingBlock;
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
import net.dries007.tfc.compat.jade.common.RegisterCallback;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.compat.jade.common.BlockEntityTooltips.*;

public final class FLTooltips
{
    public static final class BlockEntities
    {
        public static void register(RegisterCallback<BlockEntityTooltip, Block> r)
        {
            register(r, "drying_mat", DRYING_MAT, DryingMatBlock.class);
            register(r, "solar_drier", DRYING_MAT, SolarDrierBlock.class);
            register(r, "string", STRING, StringBlock.class);
            register(r, "cheese", CHEESE, CheeseWheelBlock.class);
            register(r, "oven_bottom", OVEN, OvenBottomBlock.class);
            register(r, "oven_top", OVEN, OvenTopBlock.class);
            register(r, "shelf", SHELF_OR_HANGER, FoodShelfBlock.class);
            register(r, "hanger", SHELF_OR_HANGER, HangerBlock.class);
            register(r, "vat", VAT, VatBlock.class);
            register(r, "tumbler", TUMBLER, CompostTumblerBlock.class);
            register(r, "fruit_tree_sapling", FRUIT_TREE_SAPLING, FLFruitTreeSaplingBlock.class);
            register(r, "jarbnet", JARBNET, JarbnetBlock.class);
        }

        private static void register(RegisterCallback<BlockEntityTooltip, Block> r, String name, BlockEntityTooltip tooltip, Class<? extends Block> aClass)
        {
            r.register(FLHelpers.identifier(name), tooltip, aClass);
        }

        public static final BlockEntityTooltip VAT = (level, state, pos, entity, tooltip) -> {
            if (entity instanceof VatBlockEntity vat)
            {
                if (vat.isBoiling())
                {
                    tooltip.accept(Component.translatable("firmalife.jade.boiling"));
                }
                heat(tooltip, vat.getTemperature());
            }
        };

        public static final BlockEntityTooltip TUMBLER = (level, state, pos, entity, tooltip) -> {
            if (entity instanceof CompostTumblerBlockEntity tumbler)
            {
                if (tumbler.canWork())
                {
                    timeLeft(level, tooltip, tumbler.getReadyTicks() - tumbler.getTicksSinceUpdate(), Component.translatable("firmalife.tumbler.almost_ready"));
                }
                for (CompostTumblerBlockEntity.AdditionType type : CompostTumblerBlockEntity.AdditionType.VALUES)
                {
                    if (type != CompostTumblerBlockEntity.AdditionType.NONE && type != CompostTumblerBlockEntity.AdditionType.POISON)
                    {
                        final float pct = tumbler.getPercentage(type);
                        if (pct > 0f)
                        {
                            tooltip.accept(Component.translatable("firmalife.tumbler.component_pct", FLHelpers.translateEnum(type), String.format("%.2f", pct)));
                        }
                    }
                }
                if (tumbler.isReady())
                {
                    if (tumbler.isRotten())
                    {
                        tooltip.accept(Component.translatable("firmalife.tumbler.rotten"));
                    }
                    else
                    {
                        tooltip.accept(Component.translatable("firmalife.tumbler.ready"));
                    }
                }
                tooltip.accept(Component.translatable("firmalife.tumbler.total", tumbler.getTotal()));
            }
        };

        public static final BlockEntityTooltip DRYING_MAT = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof DryingMatBlock block && entity instanceof DryingMatBlockEntity mat && !mat.viewStack().isEmpty())
            {
                if (mat.getCachedRecipe() != null && mat.getTicksLeft() > 0)
                {
                    tooltip.accept(Component.translatable("tfc.jade.time_left", delta(level, mat.getTicksLeft())));
                }
            }
        };

        public static final BlockEntityTooltip STRING = (level, state, pos, entity, tooltip) -> {
            if (state.getBlock() instanceof StringBlock block && entity instanceof StringBlockEntity mat)
            {
                tooltip.accept(Component.translatable("firmalife.jade." + (StringBlock.findFirepit(level, pos) != null ? "has_firepit" : "no_firepit")));
                final ItemStack item = mat.viewStack();
                if (!item.isEmpty())
                {
                    final IFood food = item.getCapability(FoodCapability.CAPABILITY).resolve().orElse(null);
                    final List<FoodTrait> traits = food == null ? null : food.getTraits();
                    if (mat.getCachedRecipe() != null)
                    {
                        tooltip.accept(Component.translatable("tfc.jade.time_left", delta(level, mat.getTicksLeft())));
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
                    tooltip.accept(Component.translatable("firmalife.jade.aging"));
                }
                else
                {
                    tooltip.accept(Component.translatable("firmalife.jade.not_aging"));
                }
                tooltip.accept(Component.translatable("firmalife.jade.slices", state.getValue(CheeseWheelBlock.COUNT)));
                tooltip.accept(Component.translatable("firmalife.jade.food_age", FLHelpers.translateEnum(state.getValue(CheeseWheelBlock.AGE))));
            }
        };

        public static final BlockEntityTooltip SHELF_OR_HANGER = (level, state, pos, entity, tooltip) -> {
            if (level.getBlockEntity(pos) instanceof FoodShelfBlockEntity shelf)
            {
                if (shelf.isClimateValid())
                {
                    tooltip.accept(Component.translatable("firmalife.cellar.valid_block"));
                }
                else
                {
                    tooltip.accept(Component.translatable("firmalife.cellar.invalid_block"));
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
                if (oven.getTemperature() > 100 && oven instanceof OvenTopBlockEntity)
                {
                    tooltip.accept(Component.translatable("firmalife.jade.needs_peel"));
                }
                if (state.getBlock() instanceof ICure cure && !cure.isCured())
                {
                    if (oven.getTemperature() < FLConfig.SERVER.ovenCureTemperature.get())
                    {
                        tooltip.accept(Component.translatable("firmalife.jade.cannot_cure"));
                    }
                    else
                    {
                        tooltip.accept(Component.translatable("firmalife.jade.cure_time_left", delta(level, FLConfig.SERVER.ovenCureTicks.get() - oven.getCureTicks())));
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
                                tooltip.accept(Component.translatable("firmalife.jade.cook_left", delta(level, ticksLeft)));
                            }
                        }
                    });
                }

            }
            if (state.getBlock() instanceof AbstractOvenBlock block && !block.isInsulated(level, pos, state))
            {
                tooltip.accept(Component.translatable("firmalife.jade.not_insulated"));
            }
            if (!state.getValue(AbstractOvenBlock.HAS_CHIMNEY))
            {
                tooltip.accept(Component.translatable("firmalife.jade.no_chimney"));
            }
        };

        public static final BlockEntityTooltip FRUIT_TREE_SAPLING = (level, state, pos, entity, tooltip) -> {
            if (entity instanceof TickCounterBlockEntity counter && state.getBlock() instanceof FruitTreeSaplingBlock sapling)
            {
                timeLeft(level, tooltip, (long) (sapling.getTreeGrowthDays() * ICalendar.TICKS_IN_DAY * TFCConfig.SERVER.globalFruitSaplingGrowthModifier.get()) - counter.getTicksSinceUpdate(), Component.translatable("tfc.jade.ready_to_grow"));
            }
        };

        public static final BlockEntityTooltip JARBNET = (level, state, pos, entity, tooltip) -> {
            if (entity instanceof JarbnetBlockEntity counter && state.getValue(JarbnetBlock.LIT))
            {
                timeLeft(level, tooltip, TFCConfig.SERVER.candleTicks.get() - counter.getTicksSinceUpdate());
            }
        };

    }

    public static final class Entities
    {
        public static void register(RegisterCallback<EntityTooltip, Entity> r)
        {

        }
    }

    private static MutableComponent delta(Level level, long ticks)
    {
        return Calendars.get(level).getTimeDelta(ticks);
    }
}
