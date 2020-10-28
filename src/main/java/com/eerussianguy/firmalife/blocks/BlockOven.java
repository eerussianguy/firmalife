package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.registry.ModRegistry;
import com.eerussianguy.firmalife.te.TEOven;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.objects.advancements.TFCTriggers;
import net.dries007.tfc.objects.blocks.property.ILightableBlock;
import net.dries007.tfc.objects.items.ItemFireStarter;
import net.dries007.tfc.util.DamageSourcesTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.fuel.FuelManager;

import static com.eerussianguy.firmalife.init.StatePropertiesFL.CURED;
import static net.dries007.tfc.Constants.RNG;
import static net.minecraft.block.BlockHorizontal.FACING;

public class BlockOven extends Block implements ILightableBlock, IItemSize
{

    public BlockOven()
    {
        super(Material.ROCK, MapColor.RED_STAINED_HARDENED_CLAY);
        setHardness(2.0f);
        setResistance(3.0f);
        setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(CURED, false).withProperty(FACING, EnumFacing.NORTH).withProperty(LIT, false));
    }

    /**
     * This is a local way for an oven to check if it's valid. Does not care about chimneys.
     * The ifs are nested like that for readability, I know it's not something a real dev would write.
     * @param world The world! What more did you want
     * @param ovenPos The oven
     * @param needsCure Does it need to be cured to return true?
     * @return If there's correctly placed ovens or walls on either side
     */
    public boolean isValidHorizontal(World world, BlockPos ovenPos, boolean needsCure)
    {
        IBlockState ovenState = world.getBlockState(ovenPos);
        EnumFacing facing = ovenState.getValue(FACING);
        EnumFacing left = facing.rotateYCCW();
        EnumFacing right = facing.rotateY();
        IBlockState leftState = world.getBlockState(ovenPos.offset(left));
        IBlockState rightState = world.getBlockState(ovenPos.offset(right));
        IBlockState[] checkStates = {leftState, rightState};

        for (IBlockState state : checkStates)
        {
            if (needsCure && !isCuredBlock(state))
            {
                return false;
            }
            Block b = state.getBlock();
            if(!(b instanceof BlockOven || b instanceof BlockOvenWall))
            {
                return false; // return false if it's not an oven or oven wall
            }
            if (b instanceof BlockOven)
            {
                if (state.getValue(FACING) != ovenState.getValue(FACING))
                {
                    return false; // if it's an oven, it should face the same way
                }
            }
            if (b instanceof BlockOvenWall)
            {
                if (state == leftState)
                {
                    if (leftState.getValue(FACING) != facing.getOpposite())
                    {
                        return false; // if it's a wall, it should be rotated to touch the oven properly
                    }
                }
                if (state == rightState)
                {
                    if (rightState.getValue(FACING) != facing)
                    {
                        return false; // see above
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checks if there's a chimney. Two blocks to each side. Needs three blocks of chimney
     * @param world The world
     * @param ovenPos The oven
     * @return A boolean saying if it's good or not
     */
    public boolean hasChimney(World world, BlockPos ovenPos)
    {
        IBlockState ovenState = world.getBlockState(ovenPos);
        EnumFacing facing = ovenState.getValue(FACING);
        EnumFacing left = facing.rotateYCCW();
        EnumFacing right = facing.rotateY();

        BlockPos[] checkPositions = {ovenPos.up(), ovenPos.offset(left).up(), ovenPos.offset(left, 2).up(), ovenPos.offset(right).up(), ovenPos.offset(right, 2).up()};
        boolean noChimneys = true;
        for (BlockPos pos : checkPositions)
        {
            if (world.getBlockState(pos).getBlock() instanceof BlockOvenChimney)
            {
                noChimneys = false;
                for (int i = 0; i < 3; i++)
                {
                    BlockPos chimPos = pos.offset(EnumFacing.UP, i);
                    if (!(world.getBlockState(chimPos).getBlock() instanceof BlockOvenChimney))
                        return false;
                }
            }
        }
        return !noChimneys;
    }

    /**
     * Tests if it's a cured block
     * @param state the block you want to test
     * @return false if it's not cured, or if it's not an oven block
     */
    private boolean isCuredBlock(IBlockState state)
    {
        if ((state.getBlock() instanceof BlockOven || state.getBlock() instanceof BlockOvenChimney) || state.getBlock() instanceof BlockOvenWall)
        {
            return state.getValue(CURED);
        }
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            if (!state.getValue(LIT))
            {
                ItemStack held = player.getHeldItem(hand); // in order to light, it doesn't need to be cured, but the structure should exist
                if (isValidHorizontal(world, pos, false) && hasChimney(world, pos) && ItemFireStarter.onIgnition(held))
                {
                    if (ItemFireStarter.onIgnition(held))
                    {
                        TFCTriggers.LIT_TRIGGER.trigger((EntityPlayerMP) player, state.getBlock()); // Trigger lit block
                        TEOven te = Helpers.getTE(world, pos, TEOven.class);
                        if (te != null)
                        {
                            world.setBlockState(pos, state.withProperty(LIT, true));
                            te.light();
                        }
                    }
                    return true;
                }
                TEOven te = Helpers.getTE(world, pos, TEOven.class);
                IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (!player.isSneaking() && inventory != null && !held.isItemEqual(new ItemStack(ModRegistry.PEEL)) && !held.isEmpty())
                {
                    for (int i = 0; i < 3; i++) // put stuff in the oven, will try every slot
                    {
                        ItemStack slotStack = inventory.getStackInSlot(i);
                        if (i < 2 && FuelManager.isItemFuel(held) && slotStack.isEmpty()) // fuel slots
                        {
                            inventory.insertItem(i, held, false);
                            if (!player.isCreative())
                                held.shrink(1);
                            te.markForSync();
                            return true;
                        }
                        else if (i == 2 && slotStack.isEmpty()) // this is the recipe slot
                        {
                            inventory.insertItem(i, held, false);
                            if (!player.isCreative())
                                held.shrink(1);
                            te.markForSync();
                            return true;
                        }
                    }
                    return true;
                }
                else if (inventory != null && (held.isEmpty() || held.isItemEqual(new ItemStack(ModRegistry.PEEL))))
                {
                    for (int i = 2; i >= 0; i--) // take stuff out. starts with the main slot and cycles backwards
                    {
                        ItemStack slotStack = inventory.getStackInSlot(i);
                        if (!slotStack.isEmpty())
                        {
                            ItemStack takeStack = inventory.extractItem(i, 1, false);
                            ItemHandlerHelper.giveItemToPlayer(player, takeStack);
                            te.markForSync();
                            if (!held.isItemEqual(new ItemStack(ModRegistry.PEEL)) && state.getValue(CURED))
                                player.attackEntityFrom(DamageSourcesTFC.GRILL, 2.0F); // damage player if they don't use peel
                            return true;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        if (!worldIn.isRemote)
        {
            if (state.getValue(LIT) && !isValidHorizontal(worldIn, pos, false))
            {
                TEOven te = Helpers.getTE(worldIn, pos, TEOven.class);
                if (te != null)
                {
                    te.turnOff();
                }
            }
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        if (state.getValue(LIT))
        {
            if (!isValidHorizontal(worldIn, pos, false))
            {
                TEOven te = Helpers.getTE(worldIn, pos, TEOven.class);
                if (te != null)
                {
                    te.turnOff();
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (stateIn.getValue(LIT))
        {
            if (worldIn.getBlockState(pos.up()).getBlock() instanceof BlockOvenChimney)
            {
                TFCParticles particle = TFCParticles.FIRE_PIT_SMOKE1;
                //chimney particles
                switch (rand.nextInt(3))
                {
                    case 0:
                        particle = TFCParticles.FIRE_PIT_SMOKE2;
                    case 1:
                        particle = TFCParticles.FIRE_PIT_SMOKE3;
                }
                particle.spawn(worldIn, pos.getX() + (rand.nextFloat() / 2) + 0.25, pos.getY() + 3, pos.getZ() +  (rand.nextFloat() / 2) + 0.25,
                    0f, 0.2F + rand.nextFloat() / 2, 0f, 110);
            }
            // inside the oven
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + rand.nextFloat(), pos.getY() + 0.11, pos.getZ() +  rand.nextFloat() / 2,
                0.02f, 0.05f * rand.nextFloat(), 0.02f);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, pos.getX() + rand.nextFloat(), pos.getY() + 0.11, pos.getZ() +  rand.nextFloat() / 2,
                0.02f, 0.05f * rand.nextFloat(), 0.02f);
            if (worldIn.getTotalWorldTime() % 80 == 0)
            {
                worldIn.playSound((double) pos.getX() + 0.5D, pos.getY(), (double) pos.getZ() + 0.5D, SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.BLOCKS, 0.5F, 0.6F, false);
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        if (facing.getAxis() == EnumFacing.Axis.Y)
        {
            facing = placer.getHorizontalFacing().getOpposite();
        }
        return getDefaultState().withProperty(FACING, facing);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState getStateFromMeta(int meta)
    {
        boolean cured = meta > 7;
        boolean lit = meta > 11 || (meta > 3 && meta < 8);
        int facing = meta;
        if (lit)
            facing -= 4;
        if (cured)
            facing -= 8;
        return this.getDefaultState().withProperty(CURED, cured).withProperty(LIT, lit).withProperty(FACING, EnumFacing.byHorizontalIndex(facing));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        int facing = state.getValue(FACING).getHorizontalIndex(); //0, 1, 2, 3
        int cured = state.getValue(CURED) ? 8 : 0; // true = 8, false = 0
        int lit = state.getValue(LIT) ? 4 : 0; // true = 0, false = 4

        return facing + cured + lit;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TEOven te = Helpers.getTE(world, pos, TEOven.class);
        if (te != null)
        {
            te.onBreakBlock(world, pos, state);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        if (state.getValue(CURED))
        {
            drops.add(new ItemStack(Items.BRICK, 3 + RNG.nextInt(3)));
        }
        else
        {
            drops.add(new ItemStack(Items.CLAY_BALL, 3 + RNG.nextInt(3)));
        }
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(LIT) ? 15 : 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.LARGE; // Can only store in chests
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.VERY_HEAVY; // Stacksize = 1
    }

    @Override
    @Nonnull
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LIT, CURED);
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEOven();
    }
}
