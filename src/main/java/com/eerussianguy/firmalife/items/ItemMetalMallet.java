package com.eerussianguy.firmalife.items;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import com.eerussianguy.firmalife.ConfigFL;
import com.eerussianguy.firmalife.blocks.BlockStemFruit;
import com.eerussianguy.firmalife.init.FoodFL;
import com.eerussianguy.firmalife.player.CapPlayerDataFL;
import com.eerussianguy.firmalife.player.IPlayerDataFL;
import com.eerussianguy.firmalife.recipe.CrackingRecipe;
import com.eerussianguy.firmalife.recipe.NutRecipe;
import com.eerussianguy.firmalife.registry.BlocksFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.IFood;
import net.dries007.tfc.api.capability.forge.ForgeableHeatableHandler;
import net.dries007.tfc.api.capability.metal.IMetalItem;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.objects.blocks.BlockPlacedItemFlat;
import net.dries007.tfc.objects.items.ItemTFC;
import net.dries007.tfc.objects.potioneffects.PotionEffectsTFC;
import net.dries007.tfc.objects.te.TEPlacedItemFlat;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.Month;

import static net.dries007.tfc.Constants.RNG;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class ItemMetalMallet extends ItemTFC implements IMetalItem
{
    private final Metal metal;
    public final ToolMaterial material;
    private final double attackDamage;
    private final float attackSpeed;

    public ItemMetalMallet(Metal metal)
    {
        this.metal = metal;
        this.material = metal.getToolMetal();
        this.setMaxDamage((int) ((double) material.getMaxUses() / 4));
        this.attackDamage = (0.5d * this.material.getAttackDamage());
        this.attackSpeed = -3.0F;
        this.setMaxStackSize(1);
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote && hand == EnumHand.MAIN_HAND)
        {
            Block block = worldIn.getBlockState(pos).getBlock();
            if (block instanceof BlockPlacedItemFlat)
            {
                TEPlacedItemFlat tile = (TEPlacedItemFlat) worldIn.getTileEntity(pos);
                if (tile == null) return EnumActionResult.FAIL;
                CrackingRecipe entry = CrackingRecipe.get(tile.getStack());
                if (entry == null) return EnumActionResult.FAIL;

                if (Constants.RNG.nextInt(100) < entry.getChance())
                {
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), entry.getOutputItem(tile.getStack()));
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 2.0F, 1.0F);
                }
                else
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_FALL, SoundCategory.BLOCKS, 2.0F, 1.0F);

                tile.setStack(ItemStack.EMPTY);
                worldIn.setBlockToAir(pos);
                player.getHeldItem(hand).damageItem(1, player);
                return EnumActionResult.SUCCESS;
            }
            else if (block == BlocksFL.MELON_FRUIT)
            {
                List<ItemStack> drops = block.getDrops(worldIn, pos, worldIn.getBlockState(pos), 0);
                ItemStack stack = drops.get(0);
                if (stack.getItem() == Item.getItemFromBlock(block))
                {
                    IFood cap = stack.getCapability(CapabilityFood.CAPABILITY, null);
                    if (cap != null)
                    {
                        if (!cap.isRotten())
                        {
                            for (int i = 0; i < 2 + RNG.nextInt(4); i++)
                                Helpers.spawnItemStack(worldIn, pos, new ItemStack(ItemsFL.getFood(FoodFL.MELON)));
                        }
                    }
                }
                worldIn.destroyBlock(pos, false);

                worldIn.playSound(null, pos, SoundEvents.BLOCK_GRASS_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
                return EnumActionResult.SUCCESS;
            }

            BlockPos offsetPos;
            BlockPos logPos = pos;
            IBlockState logState = worldIn.getBlockState(pos);
            Block logBlock = logState.getBlock();
            NutRecipe entry = NutRecipe.get(logBlock);// grabbing the registry to verify you're clicking a nut tree
            if (entry == null)
                return EnumActionResult.FAIL;

            int leafCount = 0;
            for (int i = 1; i < 14; i++)
            {
                logPos = pos.up(i);
                logState = worldIn.getBlockState(logPos);
                if (logState.getBlock() != logBlock)// we already verified that logBlock is correct
                    break;
                for (EnumFacing d : EnumFacing.HORIZONTALS)//this is a crappy leaf counting algorithm
                {
                    IBlockState leafState;
                    for (int j = 1; j < 5; j++)
                    {
                        offsetPos = logPos.offset(d, j);
                        leafState = worldIn.getBlockState(offsetPos);
                        if (j == 1 && leafState.getBlock() == logBlock)// offset the thing if the trunk seems to curve
                            pos = pos.offset(d, j);
                        if (worldIn.isAirBlock(offsetPos))
                            continue;
                        if (leafState.getBlock() == entry.getLeaves())
                            leafCount++;
                    }
                }
            }
            if (leafCount > 0)
            {
                Month month = CalendarTFC.CALENDAR_TIME.getMonthOfYear();
                if (!(month == Month.OCTOBER || month == Month.NOVEMBER))
                {
                    player.sendStatusMessage(new TextComponentTranslation("tooltip.firmalife.not_fall"), true);
                    return EnumActionResult.PASS;
                }

                IPlayerDataFL playerData = player.getCapability(CapPlayerDataFL.CAPABILITY, null);
                if (playerData != null)
                {
                    boolean timePassed = (int) CalendarTFC.CALENDAR_TIME.getTicks() - playerData.getNuttedTime() > ConfigFL.General.BALANCE.nutTime;
                    boolean distanced = playerData.getNutDistance(pos) > ConfigFL.General.BALANCE.nutDistance;
                    if (distanced && timePassed)
                    {
                        playerData.setNuttedTime();
                        playerData.setNutPosition(pos);
                        leafCount = (int) Math.ceil(leafCount * 0.66);
                        while (leafCount > 0)// batches drops a few times
                        {
                            int dropCount = Math.min(Constants.RNG.nextInt(4) + 1, leafCount);
                            BlockPos dropPos = logPos.offset(EnumFacing.random(Constants.RNG), Constants.RNG.nextInt(3) + 1);
                            Helpers.spawnItemStack(worldIn, dropPos, new ItemStack(entry.getNut().getItem(), Constants.RNG.nextInt(dropCount)));//should be querying nut
                            TFCParticles.LEAF1.sendToAllNear(worldIn, dropPos.getX() + RNG.nextFloat() / 10, dropPos.getY() - RNG.nextFloat() / 10, dropPos.getZ() + RNG.nextFloat() / 10, (RNG.nextFloat() - 0.5) / 10, -0.15D + RNG.nextFloat() / 10, (RNG.nextFloat() - 0.5) / 10, 90);
                            leafCount -= dropCount;
                        }
                        worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 3.0F, 1.0F);
                        player.getHeldItem(hand).damageItem(1, player);
                        player.addPotionEffect(new PotionEffect(MobEffects.HUNGER, 200, 1));
                    }
                    else
                    {
                        if (!timePassed)
                        {
                            player.sendStatusMessage(new TextComponentTranslation("tooltip.firmalife.refractory"), true);
                        }
                        else
                        {
                            player.sendStatusMessage(new TextComponentTranslation("tooltip.firmalife.distance"), true);
                        }
                        player.addPotionEffect(new PotionEffect(MobEffects.MINING_FATIGUE, 200, 1));
                        player.addPotionEffect(new PotionEffect(PotionEffectsTFC.THIRST, 200, 0));
                    }
                    return EnumActionResult.SUCCESS;
                }
            }
        }

        return EnumActionResult.FAIL;
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack)
    {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if (slot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Tool modifier", this.attackDamage, 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", this.attackSpeed, 0));
        }

        return multimap;
    }

    @Nonnull
    public Size getSize(ItemStack stack)
    {
        return Size.VERY_LARGE;
    }

    @Nonnull
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
    }

    public boolean canStack(ItemStack itemStack)
    {
        return false;
    }

    @Nullable
    @Override
    public Metal getMetal(ItemStack itemStack)
    {
        return metal;
    }

    @Override
    public int getSmeltAmount(ItemStack itemStack)
    {
        if (this.isDamageable() && itemStack.isItemDamaged())
        {
            double d = (double) (itemStack.getMaxDamage() - itemStack.getItemDamage()) / (double) itemStack.getMaxDamage() - 0.1D;
            return d < 0.0D ? 0 : MathHelper.floor((double) 100 * d);
        }
        else
            return 100;
    }

    @Override
    public boolean canMelt(ItemStack itemStack)
    {
        return true;
    }

    @Nullable
    public ICapabilityProvider initCapabilities(ItemStack itemStack, @Nullable NBTTagCompound nbt)
    {
        return new ForgeableHeatableHandler(nbt, metal.getSpecificHeat(), metal.getMeltTemp());
    }
}
