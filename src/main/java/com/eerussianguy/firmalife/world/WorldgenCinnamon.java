package com.eerussianguy.firmalife.world;

import java.util.Random;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.eerussianguy.firmalife.registry.BlocksFL;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.world.classic.biomes.BiomeTFC;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class WorldgenCinnamon extends WorldGenerator
{

    @Override
    public boolean generate(World world, Random rand, BlockPos pos)
    {
        if (rand.nextInt(130) != 4)
            return false;

        ChunkDataTFC chunkData = ChunkDataTFC.get(world, pos);
        if (!chunkData.isInitialized()) return false;

        final Biome b = world.getBiome(pos);
        if (!(b instanceof BiomeTFC) || b == BiomesTFC.OCEAN || b == BiomesTFC.DEEP_OCEAN)
            return false;

        final float diversity = chunkData.getFloraDiversity();
        final float density = chunkData.getFloraDensity();
        final float temp = chunkData.getAverageTemp();
        final float rain = chunkData.getRainfall();

        int x = pos.getX() - 7 + rand.nextInt(14);
        int z = pos.getZ() - 7 + rand.nextInt(14);
        BlockPos genPos = world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z));
        if (rain > 300 && temp > 29)
        {
            return generateCinnamon(world, rand, genPos);
        }


        return false;
    }

    private boolean generateCinnamon(World world, Random rand, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos.down());
        if (world.isAirBlock(pos) && state.isSideSolid(world, pos.down(), EnumFacing.UP) && BlocksTFC.isGrowableSoil(state))
        {
            for (int air = 1; air < 15; air++)
            {
                if (!world.isAirBlock(pos.offset(EnumFacing.UP, air)))
                    return false;
            }
            int height = 7 + rand.nextInt(5);
            IBlockState leaves = BlocksFL.CINNAMON_LEAVES.getDefaultState();
            for (int trunk = 0; trunk < height; trunk++)
            {
                BlockPos trunkPos = pos.offset(EnumFacing.UP, trunk);
                world.setBlockState(trunkPos, BlocksFL.CINNAMON_LOG.getDefaultState());
                if (trunk < 3)
                    continue;
                for (EnumFacing d : EnumFacing.HORIZONTALS)
                {
                    world.setBlockState(trunkPos.offset(d, 1), leaves);
                    if (rand.nextFloat() < 1 - (float) trunk / height)
                    {
                        world.setBlockState(trunkPos.offset(d, 2), leaves);
                    }
                    else { continue; }
                    if (trunk < 0.3 * height && rand.nextFloat() < (1 - (float) trunk / (height)) / 3)
                        world.setBlockState(trunkPos.offset(d, 3), leaves);
                }
            }
            world.setBlockState(pos.offset(EnumFacing.UP, height), leaves);

            return true;
        }
        return false;
    }

}
