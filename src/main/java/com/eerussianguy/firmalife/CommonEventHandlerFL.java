package com.eerussianguy.firmalife;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import com.eerussianguy.firmalife.gui.FLGuiHandler;
import com.eerussianguy.firmalife.player.CapPlayerDataFL;
import com.eerussianguy.firmalife.player.PlayerDataFL;
import com.eerussianguy.firmalife.registry.BlocksFL;
import com.eerussianguy.firmalife.registry.FluidsFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.IFruitTree;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeLeaves;
import net.dries007.tfc.objects.blocks.agriculture.BlockFruitTreeTrunk;
import net.dries007.tfc.objects.entity.animal.EntityCowTFC;
import net.dries007.tfc.objects.entity.animal.EntityGoatTFC;
import net.dries007.tfc.objects.entity.animal.EntityYakTFC;
import net.dries007.tfc.objects.entity.animal.EntityZebuTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;
import net.dries007.tfc.objects.items.ItemMisc;
import net.dries007.tfc.util.Helpers;

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
            event.getDrops().add(new ItemStack(ItemsFL.FRUIT_LEAF, 2 + Constants.RNG.nextInt(4)));
        }
        else if (block instanceof BlockFruitTreeTrunk) //todo: implement this without strings
        {
            IFruitTree tree = ((BlockFruitTreeTrunk) block).getTree();
            String poleName = MOD_ID + tree.getName().toLowerCase() + "_pole";
            Item pole = ItemMisc.getByNameOrId(poleName);
            if (pole != null)
                event.getDrops().add(new ItemStack(pole));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event)
    {
        if (!ConfigFL.General.COMPAT.customMilk)
            return;
        if (event.getWorld().isRemote)
            return;
        Entity entity = event.getTarget();
        ItemStack item = event.getItemStack();
        EntityPlayer player = event.getEntityPlayer();
        if (!item.isEmpty())
        {
            IFluidHandlerItem bucket = item.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null);
            if (bucket != null)
            {
                FluidActionResult fillResult = FluidUtil.tryFillContainer(item, FluidUtil.getFluidHandler(new ItemStack(Items.MILK_BUCKET)),
                    Fluid.BUCKET_VOLUME, player, false);//checking if it can be filled
                if (fillResult.isSuccess() && entity instanceof EntityCowTFC)
                {
                    EntityCowTFC cow = (EntityCowTFC) entity;//we can just cast the entity to a cow to test familiarity etc
                    Fluid fluid = FluidsTFC.MILK.get();
                    boolean foundMilkable = false;
                    if (entity instanceof EntityYakTFC)//have to check the original entity to get the proper instanceof however
                    {
                        foundMilkable = true;
                        fluid = FluidsFL.YAK_MILK.get();
                    }
                    else if (entity instanceof EntityGoatTFC)
                    {
                        foundMilkable = true;
                        fluid = FluidsFL.GOAT_MILK.get();
                    }
                    else if (entity instanceof EntityZebuTFC)
                    {
                        foundMilkable = true;
                        fluid = FluidsFL.ZEBU_MILK.get();
                    }
                    if (foundMilkable)
                    {
                        if (cow.getFamiliarity() > 0.15f && cow.isReadyForAnimalProduct())
                        {
                            bucket.drain(1000, true);
                            bucket.fill(new FluidStack(fluid, 1000), true);
                        }
                    }
                }

            }
        }
    }

    @SubscribeEvent
    public static void onAttachEntityCapabilities(AttachCapabilitiesEvent<Entity> event)
    {
        if (event.getObject() instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) event.getObject();
            if (!player.hasCapability(CapPlayerDataFL.CAPABILITY, null))
            {
                event.addCapability(CapPlayerDataFL.NAMESPACE, new PlayerDataFL());
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event)
    {
        if (event.getItemStack().getItem() == Item.getItemFromBlock(BlocksFL.PUMPKIN_FRUIT) &&
            Helpers.playerHasItemMatchingOre(event.getEntityPlayer().inventory, "knife") &&
            !event.getWorld().isRemote)
        {
            FLGuiHandler.openGui(event.getWorld(), event.getPos(), event.getEntityPlayer(), FLGuiHandler.Type.KNAPPING_PUMPKIN);
        }
    }
}
