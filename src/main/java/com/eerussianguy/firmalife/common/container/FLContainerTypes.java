package com.eerussianguy.firmalife.common.container;

import java.util.function.Supplier;

import com.eerussianguy.firmalife.common.blockentities.StovetopGrillBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.ItemStackContainer;
import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);

    public static final RegistryObject<MenuType<BeehiveContainer>> BEEHIVE = FLContainerTypes.<FLBeehiveBlockEntity, BeehiveContainer>registerBlock("beehive", FLBlockEntities.BEEHIVE, BeehiveContainer::create);
    public static final RegistryObject<MenuType<StovetopGrillContainer>> STOVETOP_GRILL = FLContainerTypes.<StovetopGrillBlockEntity, StovetopGrillContainer>registerBlock("stovetop_grill", FLBlockEntities.STOVETOP_GRILL, StovetopGrillContainer::create);
    public static final RegistryObject<MenuType<StovetopPotContainer>> STOVETOP_POT = FLContainerTypes.<StovetopPotBlockEntity, StovetopPotContainer>registerBlock("stovetop_pot", FLBlockEntities.STOVETOP_POT, StovetopPotContainer::create);

    private static <T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> RegistryObject<MenuType<C>> registerBlock(String name, Supplier<BlockEntityType<T>> type, BlockEntityContainer.Factory<T, C> factory)
    {
        return RegistrationHelpers.registerBlockEntityContainer(CONTAINERS, name, type, factory);
    }

    private static <C extends ItemStackContainer> RegistryObject<MenuType<C>> registerItem(String name, ItemStackContainer.Factory<C> factory)
    {
        return RegistrationHelpers.registerItemStackContainer(CONTAINERS, name, factory);
    }
}
