package com.eerussianguy.firmalife.common.capabilities;

import java.util.function.Supplier;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.blockentities.BigBarrelBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blockentities.MixingBowlBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.VatBlockEntity;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.capabilities.wine.WineComponent;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.items.FilledWineBottleItem;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.common.capabilities.ItemCapabilities;
import net.dries007.tfc.common.component.TFCComponents.Id;
import net.dries007.tfc.common.component.fluid.FluidContainerHandler;

public final class FLComponents
{
    public static final DeferredRegister<DataComponentType<?>> COMPONENT = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, FirmaLife.MOD_ID);

    public static final Id<BeeComponent> BEE = register("bee", BeeComponent.CODEC, BeeComponent.STREAM_CODEC);
    public static final Id<WineComponent> WINE = register("wine", WineComponent.CODEC, WineComponent.STREAM_CODEC);

    public static void register(RegisterCapabilitiesEvent event)
    {
        event.registerItem(ItemCapabilities.FLUID, FLComponents::forWine,
            FLItems.HEMATITIC_WINE_BOTTLE,
            FLItems.OLIVINE_WINE_BOTTLE,
            FLItems.VOLCANIC_WINE_BOTTLE
        );

        event.registerItem(ItemCapabilities.FLUID, ItemCapabilities::forBucket, FLItems.HOLLOW_SHELL, FLItems.WINE_GLASS);

        registerInventory(event, FLBlockEntities.ASHTRAY);
        registerInventory(event, FLBlockEntities.BARREL_PRESS);
        registerInventory(event, FLBlockEntities.BEEHIVE);
        registerInventory(event, FLBlockEntities.BIG_BARREL);
        event.registerBlockEntity(BlockCapabilities.FLUID, FLBlockEntities.BIG_BARREL.get(), BigBarrelBlockEntity::getSidedFluidInventory);
        registerInventory(event, FLBlockEntities.BONSAI_PLANTER);
        registerInventory(event, FLBlockEntities.COMPOST_TUMBLER);
        registerInventory(event, FLBlockEntities.DRYING_MAT);
        registerInventory(event, FLBlockEntities.FOOD_SHELF);
        registerInventory(event, FLBlockEntities.HANGER);
        registerInventory(event, FLBlockEntities.HANGING_PLANTER);
        registerInventory(event, FLBlockEntities.HYDROPONIC_PLANTER);
        registerInventory(event, FLBlockEntities.JARBNET);
        registerInventory(event, FLBlockEntities.JARRING_STATION);
        registerInventory(event, FLBlockEntities.LARGE_PLANTER);
        registerInventory(event, FLBlockEntities.MIXING_BOWL);
        event.registerBlockEntity(BlockCapabilities.FLUID, FLBlockEntities.MIXING_BOWL.get(), MixingBowlBlockEntity::getSidedFluidInventory);
        registerInventory(event, FLBlockEntities.OVEN_BOTTOM);
        registerInventory(event, FLBlockEntities.OVEN_HOPPER);
        registerInventory(event, FLBlockEntities.OVEN_TOP);
        event.registerBlockEntity(BlockCapabilities.HEAT, FLBlockEntities.OVEN_TOP.get(), (be, ctx) -> be.getInventory());
        registerInventory(event, FLBlockEntities.PLATE);
        registerInventory(event, FLBlockEntities.QUAD_PLANTER);
        registerInventory(event, FLBlockEntities.SOLAR_DRIER);
        registerInventory(event, FLBlockEntities.STOMPING_BARREL);
        registerInventory(event, FLBlockEntities.STOVETOP_GRILL);
        event.registerBlockEntity(BlockCapabilities.HEAT, FLBlockEntities.STOVETOP_GRILL.get(), (be, ctx) -> be.getInventory());
        registerInventory(event, FLBlockEntities.STOVETOP_POT);
        event.registerBlockEntity(BlockCapabilities.FLUID, FLBlockEntities.STOVETOP_POT.get(), StovetopPotBlockEntity::getSidedFluidInventory);
        event.registerBlockEntity(BlockCapabilities.HEAT, FLBlockEntities.STOVETOP_POT.get(), (be, ctx) -> be.getInventory());
        registerInventory(event, FLBlockEntities.STRING);
        registerInventory(event, FLBlockEntities.TRELLIS_PLANTER);
        registerInventory(event, FLBlockEntities.VAT);
        event.registerBlockEntity(BlockCapabilities.FLUID, FLBlockEntities.VAT.get(), VatBlockEntity::getSidedFluidInventory);
        event.registerBlockEntity(BlockCapabilities.HEAT, FLBlockEntities.VAT.get(), (be, ctx) -> be.getInventory());
        registerInventory(event, FLBlockEntities.WINE_SHELF);
    }

    public static @Nullable FluidContainerHandler forWine(ItemStack stack, @Nullable Void context)
    {
        return stack.getItem() instanceof FilledWineBottleItem item ? new FluidContainerHandler(stack, item.getContainerInfo()) : null;
    }

    private static void registerInventory(RegisterCapabilitiesEvent event, Supplier<? extends BlockEntityType<? extends InventoryBlockEntity<?>>> type)
    {
        event.registerBlockEntity(BlockCapabilities.ITEM, type.get(), InventoryBlockEntity::getSidedInventory);
    }

    private static <T> Id<T> register(String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec)
    {
        return new Id<>(COMPONENT.register(name, () -> new DataComponentType.Builder<T>()
            .persistent(codec)
            .networkSynchronized(streamCodec)
            .build()));
    }
}
