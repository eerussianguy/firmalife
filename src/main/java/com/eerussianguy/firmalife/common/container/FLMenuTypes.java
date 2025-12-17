package com.eerussianguy.firmalife.common.container;

import java.util.function.Supplier;

import com.eerussianguy.firmalife.common.blockentities.BarrelPressBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.KegBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.StovetopGrillBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.StovetopPotBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.common.container.TFCContainerTypes.Id;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

public class FLMenuTypes
{
    public static final DeferredRegister<MenuType<?>> MENU = DeferredRegister.create(Registries.MENU, MOD_ID);

    public static final Id<StovetopGrillContainer> STOVETOP_GRILL = FLMenuTypes.<StovetopGrillBlockEntity, StovetopGrillContainer>registerBlock("stovetop_grill", FLBlockEntities.STOVETOP_GRILL, StovetopGrillContainer::create);
    public static final Id<StovetopPotContainer> STOVETOP_POT = FLMenuTypes.<StovetopPotBlockEntity, StovetopPotContainer>registerBlock("stovetop_pot", FLBlockEntities.STOVETOP_POT, StovetopPotContainer::create);
    public static final Id<KegContainer> KEG = FLMenuTypes.<KegBlockEntity, KegContainer>registerBlock("keg", FLBlockEntities.KEG, KegContainer::create);
    public static final Id<BarrelPressContainer> BARREL_PRESS = FLMenuTypes.<BarrelPressBlockEntity, BarrelPressContainer>registerBlock("barrel_press", FLBlockEntities.BARREL_PRESS, BarrelPressContainer::create);

    private static <T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> Id<C> registerBlock(String name, Supplier<BlockEntityType<T>> type, BlockEntityContainer.Factory<T, C> factory)
    {
        return new Id<>(RegistrationHelpers.registerBlockEntityContainer(MENU, name, type, factory));
    }

}
