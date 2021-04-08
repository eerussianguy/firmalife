package com.eerussianguy.firmalife.world;

import java.util.Random;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

public class WorldGeneratorFL implements IWorldGenerator
{
    private final WorldgenCinnamon cinnamon = new WorldgenCinnamon();
    private final WorldgenBees bees = new WorldgenBees();

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider)
    {
        BlockPos center = new BlockPos(chunkX * 16 + 8, world.getHeight(chunkX * 16 + 8, chunkZ * 16 + 8), chunkZ * 16 + 8);

        cinnamon.generate(world, random, center);
        bees.generate(world, random, center);
    }
}
