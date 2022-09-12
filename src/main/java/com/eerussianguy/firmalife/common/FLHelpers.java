package com.eerussianguy.firmalife.common;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import com.google.gson.JsonElement;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.FirmaLife;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLHelpers
{
    public static final boolean ASSERTIONS_ENABLED = detectAssertionsEnabled();

    public static Direction[] NOT_DOWN = new Direction[] {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.UP};

    public static ResourceLocation identifier(String id)
    {
        return new ResourceLocation(MOD_ID, id);
    }

    public static void resetCounter(Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity counter)
        {
            counter.resetCounter();
        }
    }

    public static <T> JsonElement codecToJson(Codec<T> codec, T instance)
    {
        return codec.encodeStart(JsonOps.INSTANCE, instance).getOrThrow(false, Util.prefix("Error encoding: ", FirmaLife.LOGGER::error));
    }

    public static Component blockEntityName(String name)
    {
        return new TranslatableComponent(MOD_ID + ".block_entity." + name);
    }

    public static <T extends BlockEntity> void readInventory(Level level, BlockPos pos, Supplier<BlockEntityType<T>> type, BiConsumer<T, IItemHandler> consumer)
    {
        level.getBlockEntity(pos, type.get()).ifPresent(be -> be.getCapability(Capabilities.ITEM).ifPresent(inv -> consumer.accept(be, inv)));
    }

    public static <T extends BlockEntity> InteractionResult consumeInventory(Level level, BlockPos pos, Supplier<BlockEntityType<T>> type, BiFunction<T, IItemHandler, InteractionResult> consumer)
    {
        return level.getBlockEntity(pos, type.get()).map(be ->
            be.getCapability(Capabilities.ITEM).map(inv -> consumer.apply(be, inv)).orElse(InteractionResult.PASS)
        ).orElse(InteractionResult.PASS);
    }

    public static InteractionResult insertOne(Level level, ItemStack item, int slot, IItemHandler inv, Player player)
    {
        if (!inv.isItemValid(slot, item)) return InteractionResult.PASS;
        return completeInsertion(level, item, inv, player, slot);
    }

    public static InteractionResult takeOne(Level level, int slot, IItemHandler inv, Player player)
    {
        ItemStack stack = inv.extractItem(slot, 1, false);
        if (stack.isEmpty()) return InteractionResult.PASS;
        ItemHandlerHelper.giveItemToPlayer(player, stack);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static InteractionResult insertOneAny(Level level, ItemStack item, int start, int end, ICapabilityProvider provider, Player player)
    {
        return provider.getCapability(Capabilities.ITEM).map(inv -> insertOneAny(level, item, start, end, inv, player)).orElse(InteractionResult.PASS);
    }

    public static InteractionResult insertOneAny(Level level, ItemStack item, int start, int end, IItemHandler inv, Player player)
    {
        for (int i = start; i <= end; i++)
        {
            if (inv.getStackInSlot(i).isEmpty())
            {
                return completeInsertion(level, item, inv, player, i);
            }
        }
        return InteractionResult.PASS;
    }

    private static InteractionResult completeInsertion(Level level, ItemStack item, IItemHandler inv, Player player, int slot)
    {
        ItemStack stack = inv.insertItem(slot, item.split(1), false);
        if (stack.isEmpty()) return InteractionResult.sidedSuccess(level.isClientSide);
        ItemHandlerHelper.giveItemToPlayer(player, stack);
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    public static InteractionResult takeOneAny(Level level, int start, int end, ICapabilityProvider provider, Player player)
    {
        return provider.getCapability(Capabilities.ITEM).map(inv -> takeOneAny(level, start, end, inv, player)).orElse(InteractionResult.PASS);
    }

    public static InteractionResult takeOneAny(Level level, int start, int end, IItemHandler inv, Player player)
    {
        for (int i = start; i <= end; i++)
        {
            ItemStack stack = inv.extractItem(i, 1, false);
            if (stack.isEmpty()) continue;
            ItemHandlerHelper.giveItemToPlayer(player, stack);
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    public static Iterable<BlockPos> allPositionsCentered(BlockPos center, int radius, int height)
    {
        return BlockPos.betweenClosed(center.offset(-radius, -height, -radius), center.offset(radius, height, radius));
    }

    public static TranslatableComponent translateEnum(Enum<?> anEnum)
    {
        return Helpers.translatable(getEnumTranslationKey(anEnum));
    }

    /**
     * Gets the translation key name for an enum. For instance, Metal.UNKNOWN would map to "firmalife.enum.metal.unknown"
     */
    public static String getEnumTranslationKey(Enum<?> anEnum)
    {
        return getEnumTranslationKey(anEnum, anEnum.getDeclaringClass().getSimpleName());
    }

    /**
     * Gets the translation key name for an enum, using a custom name instead of the enum class name
     */
    public static String getEnumTranslationKey(Enum<?> anEnum, String enumName)
    {
        return String.join(".", MOD_ID, "enum", enumName, anEnum.name()).toLowerCase(Locale.ROOT);
    }

    @SuppressWarnings({"AssertWithSideEffects", "ConstantConditions"})
    private static boolean detectAssertionsEnabled()
    {
        boolean enabled = false;
        assert enabled = true;
        return enabled;
    }
}
