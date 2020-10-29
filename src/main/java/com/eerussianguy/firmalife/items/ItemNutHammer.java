package com.eerussianguy.firmalife.items;

import javax.annotation.Nonnull;

import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.registry.ModRegistry;
import com.eerussianguy.firmalife.util.CapPlayerDataFL;
import com.eerussianguy.firmalife.util.IPlayerDataFL;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.objects.potioneffects.PotionEffectsTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;

import static net.dries007.tfc.Constants.RNG;

public class ItemNutHammer extends Item implements IItemSize
{
    public ItemNutHammer()
    {
        super();
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND)
        {
            if (Constants.RNG.nextInt(30) == 2) // breaking the hammer randomly
            {
                player.setHeldItem(hand, ItemStack.EMPTY);
                worldIn.playSound(null, player.getPosition(), SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.5f, 1.0f);
                return EnumActionResult.SUCCESS;
            }
            IBlockState logState = worldIn.getBlockState(pos);
            // check if the log is in the registry, grab the leaves, nut
            int leafCount = 0;
            for (int i = 1; i < 10; i++)
            {
                BlockPos logPos = pos.up(i);
                logState = worldIn.getBlockState(logPos);
                if (!(logState.getBlock() instanceof BlockLog)) // this should be checking that same log
                    break;
                for (EnumFacing d : EnumFacing.HORIZONTALS)//this is a crappy leaf counting algorithm
                {
                    IBlockState leafState = null;
                    for (int j = 1; j < 5; j++)
                    {
                        BlockPos offsetPos = logPos.offset(d, j);
                        leafState = worldIn.getBlockState(offsetPos);
                        if (worldIn.isAirBlock(offsetPos))
                            continue;
                        if (leafState.getBlock() instanceof BlockLeaves)// this should be checking specific leaves
                            leafCount++;
                    }
                }
            }
            if (leafCount > 0)
            {
                IPlayerDataFL playerData = player.getCapability(CapPlayerDataFL.CAPABILITY, null);
                if (playerData != null)
                {
                    long nuttedTime = playerData.getNuttedTime();// check if it's been 24 hours
                    if ((int) CalendarTFC.CALENDAR_TIME.getTicks() - nuttedTime > ICalendar.TICKS_IN_DAY)
                    {
                        playerData.setNuttedTime();
                        while (leafCount > 0)//this is overly complicated for no reason, but essentially it can't drop more than how many leaves we found
                        {
                            int dropCount = Math.min(Constants.RNG.nextInt(4) + 1, leafCount);
                            BlockPos dropPos = pos.up(4 + Constants.RNG.nextInt(3)).offset(EnumFacing.random(Constants.RNG), Constants.RNG.nextInt(3) + 1);
                            Helpers.spawnItemStack(worldIn, dropPos, new ItemStack(ModRegistry.CHESTNUTS, Constants.RNG.nextInt(dropCount)));//should be querying nut
                            TFCParticles.LEAF1.sendToAllNear(worldIn, dropPos.getX() + RNG.nextFloat() / 10, dropPos.getY() - RNG.nextFloat() / 10, dropPos.getZ() + RNG.nextFloat() / 10, (RNG.nextFloat() - 0.5) / 10, -0.15D + RNG.nextFloat() / 10, (RNG.nextFloat() - 0.5) / 10, 90);
                            leafCount -= dropCount;
                        }
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
                    }
                    else// if you did it again too early
                    {
                        player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectsTFC.THIRST, 200, 0));
                        player.sendStatusMessage(new TextComponentTranslation("tooltip.firmalife.refractory"), true);
                    }
                    return EnumActionResult.SUCCESS;
                }
            }
        }

        return EnumActionResult.FAIL;
    }

    @Nonnull
    public Size getSize(ItemStack stack) {
        return Size.VERY_LARGE;
    }

    @Nonnull
    public Weight getWeight(ItemStack stack) {
        return Weight.HEAVY;
    }
}
