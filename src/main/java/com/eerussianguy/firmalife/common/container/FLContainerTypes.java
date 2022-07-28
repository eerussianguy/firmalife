package com.eerussianguy.firmalife.common.container;

import java.util.function.Supplier;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.container.BlockEntityContainer;
import net.dries007.tfc.common.container.ItemStackContainer;
import net.dries007.tfc.common.container.KnappingContainer;
import net.dries007.tfc.util.registry.RegistrationHelpers;

import static com.eerussianguy.firmalife.Firmalife.MOD_ID;

public class FLContainerTypes
{
    public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MOD_ID);

    public static final RegistryObject<MenuType<BeehiveContainer>> BEEHIVE = FLContainerTypes.<FLBeehiveBlockEntity, BeehiveContainer>registerBlock("beehive", FLBlockEntities.BEEHIVE, BeehiveContainer::create);
    public static final RegistryObject<MenuType<KnappingContainer>> PUMPKIN = FLContainerTypes.registerItem("pumpkin", FLContainerTypes::createPumpkin);

    public static KnappingContainer createPumpkin(ItemStack stack, InteractionHand hand, Inventory playerInventory, int windowId)
    {
        return new KnappingContainer(PUMPKIN.get(), FLRecipeTypes.PUMPKIN_KNAPPING.get(), windowId, playerInventory, stack, hand, 1, false, false, TFCSounds.KNAP_LEATHER.get()).init(playerInventory, 20);
    }

    private static <T extends InventoryBlockEntity<?>, C extends BlockEntityContainer<T>> RegistryObject<MenuType<C>> registerBlock(String name, Supplier<BlockEntityType<T>> type, BlockEntityContainer.Factory<T, C> factory)
    {
        return RegistrationHelpers.registerBlockEntityContainer(CONTAINERS, name, type, factory);
    }

    private static <C extends ItemStackContainer> RegistryObject<MenuType<C>> registerItem(String name, ItemStackContainer.Factory<C> factory)
    {
        return RegistrationHelpers.registerItemStackContainer(CONTAINERS, name, factory);
    }
}
