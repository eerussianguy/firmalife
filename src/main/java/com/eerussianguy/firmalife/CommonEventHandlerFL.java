package com.eerussianguy.firmalife;


import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;

import com.eerussianguy.firmalife.entity.CombatGreenhouseTask;
import com.eerussianguy.firmalife.gui.FLGuiHandler;
import com.eerussianguy.firmalife.items.ItemFruitPole;
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
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.util.Helpers;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class CommonEventHandlerFL
{
    @SubscribeEvent
    public static void onBlockHarvestDrops(BlockEvent.HarvestDropsEvent event)
    {
        final IBlockState state = event.getState();
        final Block block = state.getBlock();

        if (block instanceof BlockFruitTreeLeaves)
        {
            event.getDrops().add(new ItemStack(ItemsFL.FRUIT_LEAF, 2 + Constants.RNG.nextInt(4)));
        }
        else if (block instanceof BlockFruitTreeTrunk)
        {
            if (event.isCanceled()) event.setCanceled(false);
            IFruitTree tree = ((BlockFruitTreeTrunk) block).getTree();
            ItemFruitPole pole = ItemFruitPole.get(tree);
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
            if (bucket != null) //checking if it can be filled
            {
                FluidActionResult fillResult = FluidUtil.tryFillContainer(item, FluidUtil.getFluidHandler(new ItemStack(Items.MILK_BUCKET)), Fluid.BUCKET_VOLUME, player, false);
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
                            FluidTank fluidHandler = new FluidTank(fluid, 1000, 1000);
                            player.setHeldItem(player.getActiveHand(), FluidUtil.tryFillContainerAndStow(item, fluidHandler, new PlayerInvWrapper(player.inventory), Fluid.BUCKET_VOLUME, player, true).getResult());
                            cow.setProductsCooldown();
                            event.setCanceled(true);
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
        World world = event.getWorld();
        if (world.isRemote) return;
        Item item = event.getItemStack().getItem();

        EntityPlayer player = event.getEntityPlayer();
        BlockPos pos = event.getPos();
        if (item == Item.getItemFromBlock(BlocksFL.PUMPKIN_FRUIT) && Helpers.playerHasItemMatchingOre(player.inventory, "knife"))
        {
            FLGuiHandler.openGui(world, pos, player, FLGuiHandler.Type.KNAPPING_PUMPKIN);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
    {
        World world = event.getWorld();
        if (world.isRemote) return;
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        EntityPlayer player = event.getEntityPlayer();
        BlockPos pos = event.getPos();
        EnumFacing facing = event.getFace();
        if (item == ItemsTFC.WOOL_YARN && player.isSneaking() && facing != null)
        {
            BlockPos offsetPos = pos.offset(facing);
            if (world.isAirBlock(offsetPos))
            {
                IBlockState string = BlocksFL.WOOL_STRING.getStateForPlacement(world, player, facing, offsetPos);
                world.setBlockState(offsetPos, string);
                stack.shrink(1);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onEntitySpawn(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityZombie)
        {
            EntityZombie zombie = (EntityZombie) entity;
            zombie.tasks.addTask(4, new CombatGreenhouseTask(zombie));
        }
    }
}
