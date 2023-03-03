package com.eerussianguy.firmalife.common.blocks.plant;

import java.util.function.Supplier;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;

import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.util.FLClimateRanges;
import com.eerussianguy.firmalife.common.util.FLFruit;
import net.dries007.tfc.common.blockentities.BerryBushBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.plant.fruit.*;

import static net.dries007.tfc.common.blocks.plant.fruit.Lifecycle.*;

public final class FLFruitBlocks
{
    public enum Tree
    {
        COCOA(FLItems.FOODS.get(FLFood.COCOA_BEANS), new Lifecycle[] {HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY}),
        FIG(FLItems.FRUITS.get(FLFruit.FIG), new Lifecycle[] {HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT, DORMANT, HEALTHY, HEALTHY});

        private final Supplier<Item> product;
        private final Lifecycle[] stages;

        Tree(Supplier<Item> product, Lifecycle[] stages)
        {
            this.product = product;
            this.stages = stages;
        }

        public Block createSapling()
        {
            return new FLFruitTreeSaplingBlock(ExtendedProperties.of(Material.PLANT).noCollission().randomTicks().strength(0).sound(SoundType.GRASS).blockEntity(FLBlockEntities.TICK_COUNTER).flammableLikeLeaves(), FLBlocks.FRUIT_TREE_GROWING_BRANCHES.get(this), 8, FLClimateRanges.FRUIT_TREES.get(this), stages);
        }

        public Block createPottedSapling()
        {
            return new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, FLBlocks.FRUIT_TREE_SAPLINGS.get(this), BlockBehaviour.Properties.of(Material.DECORATION).instabreak().noOcclusion());
        }

        public Block createLeaves()
        {
            return new FLFruitTreeLeavesBlock(ExtendedProperties.of(Material.LEAVES).strength(0.5F).sound(SoundType.GRASS).randomTicks().noOcclusion().blockEntity(FLBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammableLikeLeaves(), product, stages, FLClimateRanges.FRUIT_TREES.get(this));
        }

        public Block createBranch()
        {
            return new FruitTreeBranchBlock(ExtendedProperties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f).flammableLikeLogs(), FLClimateRanges.FRUIT_TREES.get(this));
        }

        public Block createGrowingBranch()
        {
            return new FLGrowingFruitTreeBranchBlock(ExtendedProperties.of(Material.WOOD).sound(SoundType.SCAFFOLDING).randomTicks().strength(1.0f).blockEntity(FLBlockEntities.TICK_COUNTER).flammableLikeLogs(), FLBlocks.FRUIT_TREE_BRANCHES.get(this), FLBlocks.FRUIT_TREE_LEAVES.get(this), FLClimateRanges.FRUIT_TREES.get(this));
        }
    }

    public enum StationaryBush
    {
        PINEAPPLE(FLItems.FRUITS.get(FLFruit.PINEAPPLE), DORMANT, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT),
        NIGHTSHADE(FLItems.NIGHTSHADE_BERRY, DORMANT, HEALTHY, HEALTHY, HEALTHY, HEALTHY, HEALTHY, FLOWERING, FRUITING, DORMANT, DORMANT, DORMANT, DORMANT);

        private final Supplier<Item> product;
        private final Lifecycle[] stages;

        StationaryBush(Supplier<Item> product, Lifecycle... stages)
        {
            this.product = product;
            this.stages = stages;
        }

        public Block create()
        {
            return new StationaryBerryBushBlock(ExtendedProperties.of(Material.LEAVES).strength(0.6f).noOcclusion().randomTicks().sound(SoundType.SWEET_BERRY_BUSH).blockEntity(FLBlockEntities.BERRY_BUSH).serverTicks(BerryBushBlockEntity::serverTick).flammableLikeLeaves(), product, stages, FLClimateRanges.STATIONARY_BUSHES.get(this));
        }
    }
}
