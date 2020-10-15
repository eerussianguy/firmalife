package com.eerussianguy.firmalife.registry;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.Constants;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeLeaves;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

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
    }
}
