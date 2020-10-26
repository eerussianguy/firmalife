package com.eerussianguy.firmalife.registry;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.blocks.BlockGreenhouseDoor;
import com.eerussianguy.firmalife.blocks.BlockPlanter;
import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.te.*;
import net.dries007.tfc.client.GrassColorHandler;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeLeaves;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = {Side.CLIENT}, modid = FirmaLife.MOD_ID)
public class ClientRegisterEventsFL
{
    public ClientRegisterEventsFL() {}

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        for (Item i : ModRegistry.getAllEasyItems())
            ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName().toString()));
        for (ItemBlock ib : ModRegistry.getAllIBs())
            ModelLoader.setCustomModelResourceLocation(ib, 0, new ModelResourceLocation(ib.getRegistryName().toString()));
        for (BlockFruitTreeLeaves leaves : ModRegistry.getAllFruitLeaves())
            ModelLoader.setCustomStateMapper(leaves, new StateMap.Builder().ignore(BlockFruitTreeLeaves.DECAYABLE).ignore(BlockFruitTreeLeaves.HARVESTABLE).build());
        for (BlockPlanter planter : ModRegistry.getAllPlanters())
            ModelLoader.setCustomStateMapper(planter, new StateMap.Builder().ignore(StatePropertiesFL.CAN_GROW).build());
        for (BlockGreenhouseDoor door : ModRegistry.getAllGreenhouseDoors())
            ModelLoader.setCustomStateMapper(door, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
        ModelLoader.setCustomStateMapper(ModRegistry.CINNAMON_LOG, new StateMap.Builder().ignore(StatePropertiesFL.CAN_GROW).build());
        ModelLoader.setCustomStateMapper(ModRegistry.CINNAMON_LEAVES, new StateMap.Builder().ignore(BlockLeaves.DECAYABLE).build());
        ModelLoader.setCustomStateMapper(ModRegistry.CINNAMON_SAPLING, new StateMap.Builder().ignore(BlockSaplingTFC.STAGE).build());

        ClientRegistry.bindTileEntitySpecialRenderer(TEOven.class, new TESROven());
        ClientRegistry.bindTileEntitySpecialRenderer(TELeafMat.class, new TESRLeafMat());
        ClientRegistry.bindTileEntitySpecialRenderer(TEQuadPlanter.class, new TESRQuadPlanter());
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerColorHandlerItems(ColorHandlerEvent.Item event)
    {
        ItemColors itemColors = event.getItemColors();

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
            ModRegistry.getAllFruitLeaves().toArray(new BlockFruitTreeLeaves[0])
        );

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
            ModRegistry.CINNAMON_LEAVES);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        BlockColors blockColors = event.getBlockColors();
        IBlockColor foliageColor = GrassColorHandler::computeGrassColor;

        blockColors.registerBlockColorHandler(foliageColor, ModRegistry.getAllFruitLeaves().toArray(new Block[0]));
        blockColors.registerBlockColorHandler(foliageColor, ModRegistry.CINNAMON_LEAVES);
    }
}
