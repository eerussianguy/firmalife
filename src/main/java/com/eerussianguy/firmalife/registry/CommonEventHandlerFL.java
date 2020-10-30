package com.eerussianguy.firmalife.registry;


import net.dries007.tfc.objects.items.ItemMisc;
import net.dries007.tfc.util.Helpers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeLeaves;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeTrunk;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.objects.fluids.FluidsTFC;

import java.util.Objects;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CommonEventHandlerFL
{
    @SubscribeEvent
    public static void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event)
    {
        final EntityPlayer player = event.getHarvester();
        final ItemStack held = player == null ? ItemStack.EMPTY : player.getHeldItemMainhand();
        final IBlockState state = event.getState();
        final Block block = state.getBlock();

        if (block instanceof BlockFruitTreeLeaves)
        {
            event.getDrops().add(new ItemStack(ModRegistry.FRUIT_LEAF, 2 + Constants.RNG.nextInt(4)));
        }
        else if (block instanceof BlockFruitTreeTrunk)
        {
            IFruitTree tree = ((BlockFruitTreeTrunk) block).getTree();
            String poleName = "firmalife:" + tree.getName().toLowerCase() + "_pole";
            event.getDrops().add(new ItemStack(ItemMisc.getByNameOrId(poleName)));
        }
    }

    @SubscribeEvent
    public static void onFillBucketEvent(FluidEvent.FluidFillingEvent event)
    {
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        Entity entity = world.getEntitiesWithinAABBExcludingEntity(world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 3, false), new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D)).get(0);
        String name = entity.getName();
        Fluid fluid = FluidsTFC.MILK.get();
        boolean foundMilkable = false;
        switch (name)
        {
            case "yaktfc":
                foundMilkable = true;
                fluid = FluidsTFC.FRESH_WATER.get();
        }
        if (foundMilkable)
        {
            IFluidTank tank = event.getTank();
            event.setCanceled(true);
            tank.fill(new FluidStack(fluid, Fluid.BUCKET_VOLUME), true);
        }
    }
}
