package com.eerussianguy.firmalife;

import javax.annotation.Nonnull;

import net.minecraft.block.*;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.eerussianguy.firmalife.blocks.BlockBonsai;
import com.eerussianguy.firmalife.blocks.BlockFruitDoor;
import com.eerussianguy.firmalife.blocks.BlockFruitFenceGate;
import com.eerussianguy.firmalife.blocks.BlockStemCrop;
import com.eerussianguy.firmalife.init.RegistriesFL;
import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.items.ItemFruitDoor;
import com.eerussianguy.firmalife.items.ItemMetalMalletMold;
import com.eerussianguy.firmalife.recipe.PlanterRecipe;
import com.eerussianguy.firmalife.registry.BlocksFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import com.eerussianguy.firmalife.render.*;
import com.eerussianguy.firmalife.te.TELeafMat;
import com.eerussianguy.firmalife.te.TEOven;
import com.eerussianguy.firmalife.te.TEString;
import com.eerussianguy.firmalife.te.TETurntable;
import net.dries007.tfc.api.capability.IMoldHandler;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.GrassColorHandler;
import net.dries007.tfc.objects.blocks.agriculture.BlockCropDead;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeLeaves;
import net.dries007.tfc.objects.blocks.wood.BlockSaplingTFC;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(value = {Side.CLIENT}, modid = FirmaLife.MOD_ID)
public class ClientRegisterEventsFL
{
    public ClientRegisterEventsFL() {}

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event)
    {
        //Setting the model resource location for items
        for (Item i : ItemsFL.getAllEasyItems())
            ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName().toString()));
        for (ItemFruitDoor i : ItemsFL.getAllFruitDoors())
            ModelLoader.setCustomModelResourceLocation(i, 0, new ModelResourceLocation(i.getRegistryName().toString()));
        for (ItemBlock ib : BlocksFL.getAllIBs())
            ModelLoader.setCustomModelResourceLocation(ib, 0, new ModelResourceLocation(ib.getRegistryName().toString(), "normal"));
        for (BlockFruitTreeLeaves leaves : BlocksFL.getAllFruitLeaves())
            ModelLoader.setCustomStateMapper(leaves, new StateMap.Builder().ignore(BlockFruitTreeLeaves.DECAYABLE).ignore(BlockFruitTreeLeaves.HARVESTABLE).build());
        ModelLoader.setCustomStateMapper(BlocksFL.SPOUT, new StateMap.Builder().ignore(StatePropertiesFL.WATERED).ignore(StatePropertiesFL.NEEDS_SOURCE).build());
        ModelLoader.setCustomStateMapper(BlocksFL.SPRINKLER, new StateMap.Builder().ignore(StatePropertiesFL.WATERED).ignore(StatePropertiesFL.NEEDS_SOURCE).build());
        ModelLoader.setCustomStateMapper(BlocksFL.TURNTABLE, new StateMap.Builder().ignore(StatePropertiesFL.CLAY).build());

        ModelLoader.setCustomModelResourceLocation(ItemsFL.CHEESECLOTH, 0, new ModelResourceLocation(ItemsFL.CHEESECLOTH.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemsFL.CRACKED_COCONUT, 0, new ModelResourceLocation(ItemsFL.CRACKED_COCONUT.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemsFL.ITEM_CINNAMON_SAPLING, 0, new ModelResourceLocation(ItemsFL.ITEM_CINNAMON_SAPLING.getRegistryName().toString()));
        ModelLoader.setCustomModelResourceLocation(ItemsFL.ITEM_QUAD_PLANTER, 0, new ModelResourceLocation(ItemsFL.ITEM_QUAD_PLANTER.getRegistryName().toString(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(ItemsFL.ITEM_LARGE_PLANTER, 0, new ModelResourceLocation(ItemsFL.ITEM_LARGE_PLANTER.getRegistryName().toString(), "inventory"));


        //Mallet mold
        ItemMetalMalletMold item = ItemsFL.malletMold;
        ModelBakery.registerItemVariants(item, new ModelResourceLocation(item.getRegistryName().toString()));
        ModelBakery.registerItemVariants(item, TFCRegistries.METALS.getValuesCollection()
            .stream()
            .filter(Metal.ItemType.PROPICK_HEAD::hasMold)
            .map(x -> new ModelResourceLocation(FirmaLife.MOD_ID + ":" + x.getRegistryName().getPath() + "_" + item.getRegistryName().getPath()))
            .toArray(ModelResourceLocation[]::new));
        ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition()
        {
            private final ModelResourceLocation FALLBACK = new ModelResourceLocation(item.getRegistryName().toString());

            @Override
            @Nonnull
            public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack)
            {
                IFluidHandler cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                if (cap instanceof IMoldHandler)
                {
                    Metal metal = ((IMoldHandler) cap).getMetal();
                    if (metal != null)
                    {
                        return new ModelResourceLocation(FirmaLife.MOD_ID + ":" + metal.getRegistryName().getPath() + "_" + stack.getItem().getRegistryName().getPath());
                    }
                }
                return FALLBACK;
            }
        });

        //Configuring block states to ignore certain properties / use others
        //use vanilla stem rendering for StemCrops
        for (BlockStemCrop block : BlocksFL.getAllCropBlocks())
            ModelLoader.setCustomStateMapper(block, new VanillaStemStateMapper());
        for (BlockFruitDoor door : BlocksFL.getAllFruitDoors())
            ModelLoader.setCustomStateMapper(door, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
        for (BlockFruitFenceGate gate : BlocksFL.getAllFruitFenceGates())
            ModelLoader.setCustomStateMapper(gate, new StateMap.Builder().ignore(BlockFenceGate.POWERED).build());

        ModelLoader.setCustomStateMapper(BlocksFL.CINNAMON_LOG, new StateMap.Builder().ignore(StatePropertiesFL.CAN_GROW).build());
        ModelLoader.setCustomStateMapper(BlocksFL.CINNAMON_LEAVES, new StateMap.Builder().ignore(BlockLeaves.DECAYABLE).build());
        ModelLoader.setCustomStateMapper(BlocksFL.CINNAMON_SAPLING, new StateMap.Builder().ignore(BlockSaplingTFC.STAGE).build());

        for (Block block : BlocksFL.getAllFluidBlocks())
            ModelLoader.setCustomStateMapper(block, new StateMap.Builder().ignore(BlockFluidBase.LEVEL).build());
        ModelLoader.setCustomStateMapper(BlocksFL.GREENHOUSE_DOOR, new StateMap.Builder().ignore(BlockDoor.POWERED).build());
        ModelLoader.setCustomStateMapper(BlocksFL.CINNAMON_LOG, new StateMap.Builder().ignore(StatePropertiesFL.CAN_GROW).build());
        ModelLoader.setCustomStateMapper(BlocksFL.CINNAMON_LEAVES, new StateMap.Builder().ignore(BlockLeaves.DECAYABLE).build());
        ModelLoader.setCustomStateMapper(BlocksFL.CINNAMON_SAPLING, new StateMap.Builder().ignore(BlockSaplingTFC.STAGE).build());

        ModelLoader.setCustomStateMapper(BlocksFL.QUAD_PLANTER, new QuadPlanterStateMapper());
        ModelLoader.setCustomStateMapper(BlocksFL.LARGE_PLANTER, new LargePlanterStateMapper());

        ClientRegistry.bindTileEntitySpecialRenderer(TEOven.class, new TESROven());
        ClientRegistry.bindTileEntitySpecialRenderer(TEString.class, new TESRString());
        ClientRegistry.bindTileEntitySpecialRenderer(TELeafMat.class, new TESRLeafMat());
        ClientRegistry.bindTileEntitySpecialRenderer(TETurntable.class, new TESRTurntable());
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerColorHandlerItems(ColorHandlerEvent.Item event)
    {
        ItemColors itemColors = event.getItemColors();

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
            BlocksFL.getAllFruitLeaves().toArray(new BlockFruitTreeLeaves[0])
        );
        itemColors.registerItemColorHandler((stack, tintIndex) ->
                event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
            BlocksFL.getAllBonsai().toArray(new BlockBonsai[0])
        );

        itemColors.registerItemColorHandler((stack, tintIndex) ->
                event.getBlockColors().colorMultiplier(((ItemBlock) stack.getItem()).getBlock().getStateFromMeta(stack.getMetadata()), null, null, tintIndex),
            BlocksFL.CINNAMON_LEAVES);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerColorHandlerBlocks(ColorHandlerEvent.Block event)
    {
        BlockColors blockColors = event.getBlockColors();
        IBlockColor foliageColor = GrassColorHandler::computeGrassColor;

        blockColors.registerBlockColorHandler(foliageColor, BlocksFL.getAllFruitLeaves().toArray(new Block[0]));
        blockColors.registerBlockColorHandler(foliageColor, BlocksFL.getAllBonsai().toArray(new Block[0]));

        //use vanilla stem coloring for stemcrops
        for (BlockStemCrop block : BlocksFL.getAllCropBlocks())
        {
            blockColors.registerBlockColorHandler((state, world, pos, tintIndex) ->
            {
                int vanillaAge = VanillaStemStateMapper.getVanillaAge(state);
                if (vanillaAge == -1)
                    vanillaAge = 7; //for fully grown, we color it like stage 7
                return blockColors.colorMultiplier(Blocks.MELON_STEM.getDefaultState().withProperty(BlockStem.AGE, vanillaAge), world, pos, tintIndex);
            }, block);
        }

        for (BlockCropDead block : BlocksFL.getAllDeadCrops())
        {
            blockColors.registerBlockColorHandler((state, world, os, tintIndex) -> 0xCC7400, block);
        }
        blockColors.registerBlockColorHandler(foliageColor, BlocksFL.CINNAMON_LEAVES);
    }

    @SubscribeEvent
    public static void onModelBake(ModelBakeEvent event)
    {
        event.getModelRegistry().putObject(new ModelResourceLocation(MOD_ID + ":quad_planter"), new QuadPlanterBakedModel());
        event.getModelRegistry().putObject(new ModelResourceLocation(MOD_ID + ":large_planter"), new LargePlanterBakedModel());
    }

    @SubscribeEvent
    public static void onTextureStitchEvent(TextureStitchEvent.Pre event)
    {
        for (PlanterRecipe crop : RegistriesFL.PLANTER_QUAD.getValuesCollection())
        {
            if (crop.getRegistryName() != null)
            {
                for (int stage = 0; stage <= PlanterRecipe.getMaxStage(crop); stage++)
                {
                    event.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/crop/" + crop.getRegistryName().getPath() + "_" + stage));
                }
            }
        }
        event.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/potting_soil_wet"));
        event.getMap().registerSprite(new ResourceLocation(MOD_ID, "blocks/potting_soil_dry"));
    }
}
