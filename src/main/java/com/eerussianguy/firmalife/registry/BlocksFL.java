package com.eerussianguy.firmalife.registry;

import com.google.common.collect.ImmutableList;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import com.eerussianguy.firmalife.blocks.*;
import com.eerussianguy.firmalife.init.FruitTreeFL;
import com.eerussianguy.firmalife.init.StemCrop;
import com.eerussianguy.firmalife.items.ItemBlockRot;
import com.eerussianguy.firmalife.te.*;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.objects.blocks.BlockFluidTFC;
import net.dries007.tfc.objects.blocks.agriculture.*;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.fluids.properties.FluidWrapper;
import net.dries007.tfc.objects.items.itemblock.ItemBlockTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.agriculture.FruitTree;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static net.dries007.tfc.objects.CreativeTabsTFC.*;

@Mod.EventBusSubscriber(modid = MOD_ID)
@GameRegistry.ObjectHolder(MOD_ID)
public class BlocksFL
{
    @GameRegistry.ObjectHolder("oven")
    public static final BlockOven OVEN = Helpers.getNull();
    @GameRegistry.ObjectHolder("oven_wall")
    public static final BlockOvenWall OVEN_WALL = Helpers.getNull();
    @GameRegistry.ObjectHolder("oven_chimney")
    public static final BlockOvenChimney OVEN_CHIMNEY = Helpers.getNull();
    @GameRegistry.ObjectHolder("pumpkin_fruit")
    public static final BlockStemFruit PUMPKIN_FRUIT = Helpers.getNull();
    @GameRegistry.ObjectHolder("melon_fruit")
    public static final BlockStemFruit MELON_FRUIT = Helpers.getNull();
    @GameRegistry.ObjectHolder("leaf_mat")
    public static final BlockLeafMat LEAF_MAT = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_log")
    public static final BlockCinnamonLog CINNAMON_LOG = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_leaves")
    public static final BlockCinnamonLeaves CINNAMON_LEAVES = Helpers.getNull();
    @GameRegistry.ObjectHolder("cinnamon_sapling")
    public static final BlockCinnamonSapling CINNAMON_SAPLING = Helpers.getNull();
    @GameRegistry.ObjectHolder("greenhouse_door")
    public static final BlockGreenhouseDoor GREENHOUSE_DOOR = Helpers.getNull();
    @GameRegistry.ObjectHolder("greenhouse_roof")
    public static final BlockGreenhouseRoof GREENHOUSE_ROOF = Helpers.getNull();
    @GameRegistry.ObjectHolder("greenhouse_wall")
    public static final BlockGreenhouseWall GREENHOUSE_WALL = Helpers.getNull();
    @GameRegistry.ObjectHolder("quad_planter")
    public static final BlockQuadPlanter QUAD_PLANTER = Helpers.getNull();

    private static ImmutableList<ItemBlock> allIBs;
    private static ImmutableList<Block> allNormalIBs = Helpers.getNull();
    private static ImmutableList<Block> allFoodIBs = Helpers.getNull();
    private static ImmutableList<BlockFruitTreeLeaves> allFruitLeaves = Helpers.getNull();
    private static ImmutableList<BlockFruitTreeSapling> allFruitSaps = Helpers.getNull();
    private static ImmutableList<BlockFruitFence> allFruitFences = Helpers.getNull();
    private static ImmutableList<BlockFruitFenceGate> allFruitFenceGates = Helpers.getNull();
    private static ImmutableList<BlockFruitDoor> allFruitDoors = Helpers.getNull();
    private static ImmutableList<BlockFruitTrapDoor> allFruitTrapDoors = Helpers.getNull();
    private static ImmutableList<BlockFluidBase> allFluidBlocks = Helpers.getNull();
    private static ImmutableList<BlockCropDead> allDeadCrops = Helpers.getNull();
    private static ImmutableList<BlockStemCrop> allCropBlocks = Helpers.getNull();
    private static ImmutableList<BlockJackOLantern> allJackOLanterns = Helpers.getNull();
    private static ImmutableList<ItemBlock> allInventoryIBs = Helpers.getNull();

    public static ImmutableList<ItemBlock> getAllIBs()
    {
        return allIBs;
    }

    public static ImmutableList<Block> getAllNormalIBs()
    {
        return allNormalIBs;
    }

    public static ImmutableList<Block> getAllFoodIBs()
    {
        return allFoodIBs;
    }

    public static ImmutableList<BlockFruitTreeLeaves> getAllFruitLeaves()
    {
        return allFruitLeaves;
    }

    public static ImmutableList<BlockFruitTreeSapling> getAllFruitSaps()
    {
        return allFruitSaps;
    }

    public static ImmutableList<BlockFruitFence> getAllFruitFences() { return allFruitFences; }

    public static ImmutableList<BlockFruitFenceGate> getAllFruitFenceGates() { return allFruitFenceGates; }

    public static ImmutableList<BlockFruitDoor> getAllFruitDoors() { return allFruitDoors; }

    public static ImmutableList<BlockFruitTrapDoor> getAllFruitTrapdoors() { return allFruitTrapDoors; }

    public static ImmutableList<BlockCropDead> getAllDeadCrops()
    {
        return allDeadCrops;
    }

    public static ImmutableList<BlockStemCrop> getAllCropBlocks()
    {
        return allCropBlocks;
    }

    public static ImmutableList<BlockFluidBase> getAllFluidBlocks()
    {
        return allFluidBlocks;
    }

    public static ImmutableList<BlockJackOLantern> getAllJackOLanterns()
    {
        return allJackOLanterns;
    }

    public static ImmutableList<ItemBlock> getAllInventoryIBs()
    {
        return allInventoryIBs;
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event)
    {
        FluidsFL.registerFluids();//this has to come first
        IForgeRegistry<Block> r = event.getRegistry();

        ImmutableList.Builder<ItemBlock> IBs = ImmutableList.builder();
        ImmutableList.Builder<Block> normalIBs = ImmutableList.builder();
        ImmutableList.Builder<Block> foodIBs = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitTreeLeaves> fruitLeaves = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitTreeSapling> fruitSaps = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitFence> fruitFences = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitFenceGate> fruitFenceGates = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitDoor> fruitDoors = ImmutableList.builder();
        ImmutableList.Builder<BlockFruitTrapDoor> fruitTrapdoors = ImmutableList.builder();
        ImmutableList.Builder<BlockCropDead> deadCrops = ImmutableList.builder();
        ImmutableList.Builder<BlockStemCrop> cropBlocks = ImmutableList.builder();
        ImmutableList.Builder<BlockJackOLantern> jackOLanterns = ImmutableList.builder();
        ImmutableList.Builder<ItemBlock> invIBs = ImmutableList.builder();

        for (FruitTreeFL fruitTree : FruitTreeFL.values())
        {
            String name = fruitTree.getName().toLowerCase();
            register(r, name + "_branch", new BlockFruitTreeBranch(fruitTree));
            fruitLeaves.add(register(r, name + "_leaves", new BlockFruitTreeLeaves(fruitTree), CT_WOOD));
            fruitSaps.add(register(r, name + "_sapling", new BlockFruitTreeSapling(fruitTree), CT_WOOD));
            register(r, name + "_trunk", new BlockFruitTreeTrunk(fruitTree));
            fruitFences.add(register(r, name + "_fence", new BlockFruitFence(), CT_DECORATIONS));
            fruitFenceGates.add(register(r, name + "_fence_gate", new BlockFruitFenceGate(), CT_DECORATIONS));
            fruitDoors.add(register(r, name + "_door", new BlockFruitDoor(), CT_DECORATIONS));
            fruitTrapdoors.add(register(r, name + "_trapdoor", new BlockFruitTrapDoor(), CT_DECORATIONS));
        }

        fruitFences.add(register(r, "cinnamon_fence", new BlockFruitFence(), CT_DECORATIONS));
        fruitFenceGates.add(register(r, "cinnamon_fence_gate", new BlockFruitFenceGate(), CT_DECORATIONS));
        fruitDoors.add(register(r, "cinnamon_door", new BlockFruitDoor(), CT_DECORATIONS));
        fruitTrapdoors.add(register(r, "cinnamon_trapdoor", new BlockFruitTrapDoor(), CT_DECORATIONS));

        for (IFruitTree fruitTree : FruitTree.values())
        {
            String name = fruitTree.getName().toLowerCase();
            fruitFences.add(register(r, name + "_fence", new BlockFruitFence(), CT_DECORATIONS));
            fruitFenceGates.add(register(r, name + "_fence_gate", new BlockFruitFenceGate(), CT_DECORATIONS));
            fruitDoors.add(register(r, name + "_door", new BlockFruitDoor(), CT_DECORATIONS));
            fruitTrapdoors.add(register(r, name + "_trapdoor", new BlockFruitTrapDoor(), CT_DECORATIONS));
        }

        normalIBs.add(register(r, "oven", new BlockOven(), CT_DECORATIONS));
        normalIBs.add(register(r, "oven_wall", new BlockOvenWall(), CT_DECORATIONS));
        normalIBs.add(register(r, "oven_chimney", new BlockOvenChimney(), CT_DECORATIONS));
        normalIBs.add(register(r, "leaf_mat", new BlockLeafMat(), CT_DECORATIONS));
        normalIBs.add(register(r, "cinnamon_log", new BlockCinnamonLog(), CT_WOOD));
        normalIBs.add(register(r, "cinnamon_leaves", new BlockCinnamonLeaves(), CT_WOOD));
        register(r, "cinnamon_sapling", new BlockCinnamonSapling(), CT_WOOD);
        normalIBs.add(register(r, "greenhouse_wall", new BlockGreenhouseWall(), CT_DECORATIONS));
        normalIBs.add(register(r, "greenhouse_roof", new BlockGreenhouseRoof(), CT_DECORATIONS));
        normalIBs.add(register(r, "climate_station", new BlockClimateStation(), CT_DECORATIONS));
        register(r, "quad_planter", new BlockQuadPlanter(), CT_DECORATIONS);
        register(r, "greenhouse_door", new BlockGreenhouseDoor(), CT_DECORATIONS);

        for (BlockJackOLantern.Carving carving : BlockJackOLantern.Carving.values())
        {
            jackOLanterns.add(register(r, "lit_pumpkin_" + carving.getName(), new BlockJackOLantern(carving), CT_DECORATIONS));
        }

        foodIBs.add(register(r, "pumpkin_fruit", new BlockStemFruit(), CT_FLORA));
        foodIBs.add(register(r, "melon_fruit", new BlockStemFruit(), CT_FLORA));

        for (StemCrop crop : StemCrop.values())
        {
            deadCrops.add(register(r, "dead_crop/" + crop.name().toLowerCase(), new BlockCropDead(crop)));
            cropBlocks.add(register(r, "crop/" + crop.name().toLowerCase(), BlockStemCrop.create(crop)));
        }

        ImmutableList.Builder<BlockFluidBase> fluids = ImmutableList.builder();
        for (FluidWrapper wrapper : FluidsFL.getAllFiniteFluids())
        {
            fluids.add(register(r, wrapper.get().getName(), new BlockFluidTFC(wrapper.get(), Material.WATER)));
        }
        allFluidBlocks = fluids.build();
        allNormalIBs = normalIBs.build();
        allNormalIBs.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allInventoryIBs = invIBs.build();
        allFoodIBs = foodIBs.build();
        allFoodIBs.forEach((x) -> {
            IBs.add(new ItemBlockRot(x));
        });
        allJackOLanterns = jackOLanterns.build();
        allJackOLanterns.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allFruitLeaves = fruitLeaves.build();
        allFruitLeaves.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allFruitSaps = fruitSaps.build();
        allFruitSaps.forEach((x) -> {
            ItemBlock ib = new ItemBlockTFC(x);
            IBs.add(ib);
            invIBs.add(ib);
        });
        allFruitFences = fruitFences.build();
        allFruitFences.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allFruitFenceGates = fruitFenceGates.build();
        allFruitFenceGates.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allFruitDoors = fruitDoors.build();

        allFruitTrapDoors = fruitTrapdoors.build();
        allFruitTrapDoors.forEach((x) -> {
            IBs.add(new ItemBlockTFC(x));
        });
        allIBs = IBs.build();
        allDeadCrops = deadCrops.build();
        allCropBlocks = cropBlocks.build();

        register(TEOven.class, "oven");
        register(TEQuadPlanter.class, "quad_planter");
        register(TELeafMat.class, "leaf_mat");
        register(TEStemCrop.class, "stem_crop");
        register(TEClimateStation.class, "climate_station");
        //needs fix
        FluidsTFC.getWrapper(FluidsFL.COCONUT_MILK.get());
        FluidsTFC.getWrapper(FluidsFL.YAK_MILK.get());
        FluidsTFC.getWrapper(FluidsFL.GOAT_MILK.get());
        FluidsTFC.getWrapper(FluidsFL.ZEBU_MILK.get());
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T block, CreativeTabs ct)
    {
        block.setCreativeTab(ct);
        return register(r, name, block);
    }

    private static <T extends Block> T register(IForgeRegistry<Block> r, String name, T block)
    {
        block.setRegistryName(MOD_ID, name);
        block.setTranslationKey(MOD_ID + "." + name.replace('/', '.'));
        r.register(block);
        return block;
    }

    private static <T extends TileEntity> void register(Class<T> te, String name)
    {
        TileEntity.register(MOD_ID + ":" + name, te);
    }
}
