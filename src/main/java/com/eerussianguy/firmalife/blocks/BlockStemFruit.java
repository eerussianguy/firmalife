package com.eerussianguy.firmalife.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.te.TEStemCrop;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.food.CapabilityFood;
import net.dries007.tfc.api.capability.food.FoodHandler;
import net.dries007.tfc.api.capability.player.CapabilityPlayerData;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.items.ItemSeedsTFC;
import net.dries007.tfc.objects.te.TETickCounter;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.agriculture.Crop;
import net.dries007.tfc.util.calendar.CalendarTFC;
import net.dries007.tfc.util.skills.SimpleSkill;
import net.dries007.tfc.util.skills.SkillType;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockStemFruit extends BlockDirectional implements IItemSize
{
    public BlockStemFruit()
    {
        super(Material.GOURD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.setHardness(1.0f);
    }

    /**
     * Checks if this block can be placed exactly at the given position.
     */
    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos)
    {
        return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && worldIn.isSideSolid(pos.down(), EnumFacing.UP);
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot)
    {
        return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState withMirror(IBlockState state, Mirror mirrorIn)
    {
        return state.withRotation(mirrorIn.toRotation(state.getValue(FACING)));
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer));
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
    }

    @Override
    public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player)
    {
        super.onBlockHarvested(worldIn, pos, state, player);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        TETickCounter te = new TETickCounter();
        te.resetCounter();
        return te;
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        super.getDrops(drops, world, pos, state, fortune);
        if (world instanceof World && !((World) world).isRemote)
        {
            TETickCounter te = Helpers.getTE(world, pos, TETickCounter.class);
            if (te != null)
            {
                long currentTime = CalendarTFC.PLAYER_TIME.getTicks();
                long foodCreationDate = currentTime - te.getTicksSinceUpdate();
                drops.forEach(stack -> {
                    FoodHandler handler = (FoodHandler) stack.getCapability(CapabilityFood.CAPABILITY, null);
                    if (handler != null)
                        handler.setCreationDate(foodCreationDate);
                });
            }
        }

    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false); //delay deletion of the block until after getDrops
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity tee, ItemStack tool)
    {
        super.harvestBlock(world, player, pos, state, tee, tool);

        if (!world.isRemote)
        {
            for (EnumFacing neighbor : EnumFacing.Plane.HORIZONTAL)
            {
                BlockPos cropPos = pos.offset(neighbor);
                Block block = world.getBlockState(cropPos).getBlock();
                if (block instanceof BlockStemCrop)
                {
                    BlockStemCrop crop = (BlockStemCrop) block;
                    //check the crop is pointing towards us
                    TEStemCrop te = Helpers.getTE(world, cropPos, TEStemCrop.class);
                    if (te != null && te.getFruitDirection() == neighbor.getOpposite())
                    {
                        IBlockState cropState = world.getBlockState(cropPos);
                        int cropStage = cropState.getValue(crop.getStageProperty());
                        if (cropStage == crop.getCrop().getMaxStage())
                        {
                            world.setBlockState(cropPos, cropState.withProperty(crop.getStageProperty(), cropStage - 3));
                            SimpleSkill skill = CapabilityPlayerData.getSkill(player, SkillType.AGRICULTURE);
                            ItemStack seedDrop = new ItemStack(ItemSeedsTFC.get(crop.getCrop()), 0);
                            if (skill != null)
                            {
                                seedDrop.setCount(Crop.getSkillSeedBonus(skill, RANDOM));
                            }
                            if (!seedDrop.isEmpty())
                                ItemHandlerHelper.giveItemToPlayer(player, seedDrop);
                        }

                    }
                }
            }
        }
        world.setBlockToAir(pos);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING);
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.LARGE;
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
    }
}
