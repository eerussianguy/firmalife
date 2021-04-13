package com.eerussianguy.firmalife.world;

import java.util.Random;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenerator;

import com.eerussianguy.firmalife.ConfigFL;
import com.eerussianguy.firmalife.registry.BlocksFL;
import net.dries007.tfc.world.classic.biomes.BiomeTFC;
import net.dries007.tfc.world.classic.biomes.BiomesTFC;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

@ParametersAreNonnullByDefault
public class WorldgenBees extends WorldGenerator
{
    @Override
    public boolean generate(World world, Random rand, BlockPos pos)
    {
        int beeRarity = ConfigFL.General.WORLDGEN.beeRarity;
        if (beeRarity == 0)
            return false;

        if (rand.nextInt(beeRarity) != 1)
            return false;

        ChunkDataTFC chunkData = ChunkDataTFC.get(world, pos);
        if (!chunkData.isInitialized()) return false;

        final Biome b = world.getBiome(pos);
        if (!(b instanceof BiomeTFC) || b == BiomesTFC.OCEAN || b == BiomesTFC.DEEP_OCEAN) return false;

        BlockPos genPos = world.getTopSolidOrLiquidBlock(pos);
        for (int i = 0; i < 12; i++)
        {
            BlockPos searchPos = genPos.add(rand.nextInt(5) - rand.nextInt(5), rand.nextInt(10), rand.nextInt(5) - rand.nextInt(5));
            BlockPos down = searchPos.down();
            if (world.getBlockState(searchPos).getMaterial() == Material.LEAVES && world.isAirBlock(down))
            {
                world.setBlockState(down, BlocksFL.BEE_NEST.getDefaultState(), 3);
                return true;
            }
        }
        return false;
    }
}
