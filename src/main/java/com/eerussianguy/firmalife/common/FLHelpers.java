package com.eerussianguy.firmalife.common;

import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.FirmaLife;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.TickCounterBlockEntity;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodTrait;
import net.dries007.tfc.common.capabilities.heat.HeatCapability;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.JsonHelpers;
import net.dries007.tfc.util.calendar.Calendars;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLHelpers
{
    public static final boolean ASSERTIONS_ENABLED = detectAssertionsEnabled();

    public static Direction[] NOT_DOWN = new Direction[] {Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH, Direction.UP};

    public static ResourceLocation identifier(String id)
    {
        return FLHelpers.res(MOD_ID, id);
    }

    public static Vec3 vec3(BlockPos pos)
    {
        return new Vec3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static void returnItem(Player player, ItemStack stack)
    {
        if (player.isAlive() && player instanceof ServerPlayer serverPlayer && !serverPlayer.hasDisconnected())
        {
            player.getInventory().placeItemBackInInventory(stack);
        }
        else
        {
            player.drop(stack, false);
        }
    }

    public static boolean stackableExceptHeatAndFood(ItemStack stack1, ItemStack stack2)
    {
        // This is a nice way of checking if two stacks are stackable, ignoring the creation date: copy both stacks, give them the same creation date, then check compatibility
        // This will also not stack items which have different traits, which is intended
        final ItemStack stack1Copy = stack1.copy(), stack2Copy = stack2.copy();
        final long date = Calendars.get().getTicks();

        FoodCapability.setCreationDate(stack1Copy, date);
        FoodCapability.setCreationDate(stack2Copy, date);
        HeatCapability.setTemperature(stack1Copy, 0);
        HeatCapability.setTemperature(stack2Copy, 0);

        return ItemStack.isSameItemSameTags(stack1Copy, stack2Copy) && stack1Copy.getMaxStackSize() >= stack2Copy.getCount() + stack1Copy.getCount();
    }

    public static void resetCounter(Level level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof TickCounterBlockEntity counter)
        {
            counter.resetCounter();
        }
    }

    public static void writeTraitList(List<FoodTrait> list, CompoundTag nbt, String key)
    {
        if (!list.isEmpty())
        {
            final ListTag listTag = new ListTag();
            for (FoodTrait trait : list)
            {
                final CompoundTag newTag = new CompoundTag();
                newTag.putString("trait", FoodTrait.getId(trait).toString());
                listTag.add(newTag);
            }
            nbt.put(key, listTag);
        }
    }

    public static void readTraitList(List<FoodTrait> list, CompoundTag nbt, String key)
    {
        list.clear();
        if (nbt.contains(key))
        {
            final ListTag excessNbt = nbt.getList(key, Tag.TAG_COMPOUND);
            for (int i = 0; i < excessNbt.size(); i++)
            {
                final FoodTrait trait = FoodTrait.getTrait(FLHelpers.res(excessNbt.getCompound(i).getString("trait")));
                if (trait != null)
                {
                    list.add(trait);
                }
            }
        }
    }

    public static void writeItemStackList(List<ItemStack> list, CompoundTag nbt, String key)
    {
        if (!list.isEmpty())
        {
            final ListTag listTag = new ListTag();
            for (ItemStack stack : list)
            {
                listTag.add(stack.save(new CompoundTag()));
            }
            nbt.put(key, listTag);
        }
    }

    public static void readItemStackList(List<ItemStack> list, CompoundTag nbt, String key)
    {
        list.clear();
        if (nbt.contains(key))
        {
            final ListTag excessNbt = nbt.getList(key, Tag.TAG_COMPOUND);
            for (int i = 0; i < excessNbt.size(); i++)
            {
                list.add(ItemStack.of(excessNbt.getCompound(i)));
            }
        }
    }

    public static <T> JsonElement codecToJson(Codec<T> codec, T instance)
    {
        return codec.encodeStart(JsonOps.INSTANCE, instance).getOrThrow(false, Util.prefix("Error encoding: ", FirmaLife.LOGGER::error));
    }

    public static Component blockEntityName(String name)
    {
        return Component.translatable(MOD_ID + ".block_entity." + name);
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

    public static ItemStack mergeInsertStack(IItemHandler inventory, int slot, ItemStack stack) {
        ItemStack existing = Helpers.removeStack(inventory, slot);
        ItemStack remainder = stack.copy();
        ItemStack merged = FoodCapability.mergeItemStacks(existing, remainder);
        remainder.grow(inventory.insertItem(slot, merged, false).getCount());
        return remainder;
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

    @Nullable
    public static <T> T ofJsonNullable(JsonObject json, Function<JsonObject, T> getter, String key)
    {
        return json.has(key) ? getter.apply(json) : null;
    }

    public static <T> T ofJsonDefaulting(JsonObject json, Function<JsonObject, T> getter, String key, T defaultValue)
    {
        return json.has(key) ? getter.apply(json) : defaultValue;
    }

    public static ResourceLocation res(String string)
    {
        return new ResourceLocation(string);
    }

    public static ResourceLocation res(String namespace, String path)
    {
        return new ResourceLocation(namespace, path);
    }

    public static Iterable<BlockPos> allPositionsCentered(BlockPos center, int radius, int height)
    {
        return BlockPos.betweenClosed(center.offset(-radius, -height, -radius), center.offset(radius, height, radius));
    }

    public static MutableComponent translateEnum(Enum<?> anEnum)
    {
        return Component.translatable(getEnumTranslationKey(anEnum));
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

    public static void roundCreationDate(ItemStack stack)
    {
        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(cap -> cap.setCreationDate(FoodCapability.getRoundedCreationDate(cap.getCreationDate())));
    }

    public static ResourceLocation[] arrayOfResourceLocationsFromJson(JsonObject json, String field)
    {
        final JsonArray array = JsonHelpers.getAsJsonArray(json, field);
        final ResourceLocation[] textures = new ResourceLocation[array.size()];
        int i = 0;
        for (JsonElement element : array)
        {
            textures[i] = FLHelpers.res(element.getAsString());
            i++;
        }
        return textures;
    }

    public static ResourceLocation[] arrayOfResourceLocationsFromNetwork(FriendlyByteBuf buffer)
    {
        final int length = buffer.readVarInt();
        if (length == 0) return new ResourceLocation[] {};
        final ResourceLocation[] textures = new ResourceLocation[length];
        for (int i = 0; i < length; i++)
        {
            textures[i] = FLHelpers.res(buffer.readUtf());
        }
        return textures;
    }

    public static void arrayOfResourceLocationsToNetwork(FriendlyByteBuf buffer, ResourceLocation[] textures)
    {
        buffer.writeVarInt(textures.length);
        for (ResourceLocation res : textures)
        {
            buffer.writeUtf(res.toString());
        }
    }


    @SuppressWarnings({"AssertWithSideEffects", "ConstantConditions"})
    private static boolean detectAssertionsEnabled()
    {
        boolean enabled = false;
        assert enabled = true;
        return enabled;
    }
}
