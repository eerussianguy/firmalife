package com.eerussianguy.firmalife.entity;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBreakDoor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

import com.eerussianguy.firmalife.blocks.BlockGreenhouseDoor;
import com.eerussianguy.firmalife.blocks.BlockGreenhouseWall;
import com.eerussianguy.firmalife.blocks.BlockLargePlanter;

public class CombatGreenhouseTask extends EntityAIBreakDoor
{
    private int breakingTime;

    public CombatGreenhouseTask(EntityLiving entityIn)
    {
        super(entityIn);
    }

    @Override
    public void startExecuting()
    {
        super.startExecuting();
        breakingTime = 0;
    }

    @Override
    public void updateTask()
    {
        super.updateTask();
        breakingTime++;
    }

    @Override
    public boolean shouldExecute()
    {
        return shouldExecuteSuper()
            && net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(entity.world, entity)
            && entity.world.getBlockState(doorPosition).getBlock().canEntityDestroy(entity.world.getBlockState(doorPosition), entity.world, doorPosition, entity)
            && net.minecraftforge.event.ForgeEventFactory.onEntityDestroyBlock(entity, doorPosition, entity.world.getBlockState(doorPosition));
    }

    /**
     * I am growing more object oriented every second
     */
    private boolean shouldExecuteSuper()
    {
        PathNavigateGround pathnavigateground = (PathNavigateGround) entity.getNavigator();
        Path path = pathnavigateground.getPath();

        if (path != null && !path.isFinished() && pathnavigateground.getEnterDoors())
        {
            for (int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i)
            {
                PathPoint pathpoint = path.getPathPointFromIndex(i);
                doorPosition = new BlockPos(pathpoint.x, pathpoint.y + 1, pathpoint.z);

                if (entity.getDistanceSq(doorPosition.getX(), entity.posY, doorPosition.getZ()) <= 5.0D && entity.world.getBlockState(doorPosition).getBlock() instanceof BlockGreenhouseWall)
                {
                    return true;
                }
            }

            doorPosition = (new BlockPos(entity)).up();
            return entity.world.getBlockState(doorPosition).getBlock() instanceof BlockGreenhouseWall;
        }
        return false;
    }

    @Override
    public boolean shouldContinueExecuting()
    {
        return breakingTime <= 240;
    }
}
