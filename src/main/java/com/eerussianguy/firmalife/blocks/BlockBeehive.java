package com.eerussianguy.firmalife.blocks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemHandlerHelper;

import com.eerussianguy.firmalife.init.FoodFL;
import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.registry.EffectsFL;
import com.eerussianguy.firmalife.registry.ItemsFL;
import com.eerussianguy.firmalife.te.TEHangingPlanter;
import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.api.types.Plant;
import net.dries007.tfc.objects.blocks.BlockFlowerPotTFC;
import net.dries007.tfc.objects.blocks.devices.BlockFirePit;
import net.dries007.tfc.objects.blocks.plants.BlockPlantTFC;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.OreDictionaryHelper;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.climate.ClimateTFC;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockBeehive extends Block implements IItemSize
{
    public static final PropertyInteger STAGE = StatePropertiesFL.STAGE;
    private static final Vec3i[] VECTORS = {
        new Vec3i(0, -1, 0),
        new Vec3i(-1, -1, 0),
        new Vec3i(0, -1, -1),
        new Vec3i(1, -1, 0),
        new Vec3i(0, -1, 1)
    };

    public BlockBeehive()
    {
        super(Material.WOOD);
        setHardness(2.0f);
        setResistance(2.0f);
        setTickRandomly(true);
        setDefaultState(getBlockState().getBaseState().withProperty(STAGE, 0));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand)
    {
        if (state.getValue(STAGE) > 0 && world.isDaytime())
        {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;
            for (int i = 0; i < 3 + rand.nextInt(4); i++)
            {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x + rand.nextFloat() - rand.nextFloat(), y + rand.nextFloat(), z + rand.nextFloat() - rand.nextFloat(),
                    0.5 * (rand.nextFloat() - rand.nextFloat()), 0.5 * (rand.nextFloat() - rand.nextFloat()), 0.5 * (rand.nextFloat() - rand.nextFloat()));
            }
        }
    }

    @Override
    public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
    {
        if (world.isRemote) return;
        TEHangingPlanter te = Helpers.getTE(world, pos, TEHangingPlanter.class);
        if (te == null) return;
        if (!isValid(world, pos, te))
        {
            te.resetCounter();
            return;
        }
        int stage = state.getValue(STAGE);
        if (stage < 2 && te.getTicksSinceUpdate() >= ICalendar.TICKS_IN_DAY * 3)
        {
            int flowers = countFlowers(world, pos);
            float chance = flowers / 10f;
            if (random.nextFloat() < chance)
            {
                world.setBlockState(pos, state.withProperty(STAGE, stage + 1));
            }
            else if (flowers == 0)
            {
                world.setBlockState(pos, state.withProperty(STAGE, 0));
            }
            te.resetCounter();
        }
    }

    private boolean isValid(World world, BlockPos pos, TEHangingPlanter te)
    {
        return te.isClimateValid() || ClimateTFC.getDailyTemp(world, pos) > 10;
    }

    private static int countFlowers(World world, BlockPos pos)
    {
        int flowers = 0;
        BlockPos searchPos;
        for (int x = -4; x <= 4; x++)
        {
            for (int y = -1; y <= 1; y++)
            {
                for (int z = -4; z <= 4; z++)
                {
                    if (flowers == 10) return flowers;
                    searchPos = pos.add(x, y, z);
                    Block block = world.getBlockState(searchPos).getBlock();
                    if (block instanceof BlockPlantTFC)
                    {
                        if (((BlockPlantTFC) block).getPlant().getPlantType() == Plant.PlantType.STANDARD)
                            flowers++;
                    }
                    else if (block instanceof BlockFlowerPotTFC || block instanceof BlockBushTrellis || block instanceof BlockLargePlanter || block instanceof BlockHangingPlanter)
                    {
                        flowers++;
                    }
                }
            }
        }
        return flowers;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (world.isRemote || hand == EnumHand.OFF_HAND) return false;
        if (state.getValue(STAGE) == 2)
        {
            world.setBlockState(pos, state.withProperty(STAGE, 1));
            Item giveItem = !OreDictionaryHelper.doesStackMatchOre(player.getHeldItem(hand), "knife") ? ItemsFL.getFood(FoodFL.RAW_HONEY) : ItemsFL.HONEYCOMB;
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(giveItem));
            if (isNotCalm(world, pos, state))
            {
                player.addPotionEffect(new PotionEffect(EffectsFL.SWARM, 30 * 20));
            }
            TEHangingPlanter te = Helpers.getTE(world, pos, TEHangingPlanter.class);
            if (te != null)
                te.resetCounter();
            return true;
        }
        return false;
    }

    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        if (isNotCalm(world, pos, state))
        {
            player.addPotionEffect(new PotionEffect(EffectsFL.SWARM, 30 * 20));
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    private static boolean isNotCalm(World world, BlockPos pos, IBlockState state)
    {
        if (state.getValue(STAGE) == 0 || !world.isDaytime()) return false;
        for (Vec3i v : VECTORS)
        {
            BlockPos searchPos = pos.add(v);
            IBlockState searchState = world.getBlockState(searchPos);
            if (searchState.getBlock() instanceof BlockFirePit && searchState.getValue(BlockFirePit.LIT))
            {
                return false;
            }
        }
        return true;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, STAGE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(STAGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(STAGE);
    }

    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.NORMAL;
    }

    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.HEAVY;
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
        return new TEHangingPlanter();
    }
}
