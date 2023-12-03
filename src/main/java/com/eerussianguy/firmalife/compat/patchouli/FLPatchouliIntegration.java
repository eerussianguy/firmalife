package com.eerussianguy.firmalife.compat.patchouli;

import java.util.function.Function;

import com.eerussianguy.firmalife.common.util.Mechanics;
import net.minecraft.core.Direction;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.SlabType;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBlocks;
import com.eerussianguy.firmalife.common.blocks.greenhouse.*;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.soil.SoilBlockType;
import net.dries007.tfc.util.Helpers;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.IStateMatcher;
import vazkii.patchouli.api.PatchouliAPI;

public class FLPatchouliIntegration
{
    public static void registerMultiBlocks()
    {
        registerMultiblock("treated_wood_greenhouse", api -> greenhouse(api, Greenhouse.TREATED_WOOD, FLTags.Blocks.ALL_TREATED_WOOD_GREENHOUSE));
        registerMultiblock("copper_greenhouse", api -> greenhouse(api, Greenhouse.COPPER, FLTags.Blocks.ALL_COPPER_GREENHOUSE));
        registerMultiblock("iron_greenhouse", api -> greenhouse(api, Greenhouse.IRON, FLTags.Blocks.ALL_IRON_GREENHOUSE));
        registerMultiblock("stainless_steel_greenhouse", api -> greenhouse(api, Greenhouse.IRON, FLTags.Blocks.STAINLESS_STEEL_GREENHOUSE));
        registerMultiblock("cellar", FLPatchouliIntegration::cellar);
    }

    private static IMultiblock cellar(PatchouliAPI.IPatchouliAPI api)
    {
        final Block door = FLBlocks.SEALED_DOOR.get();
        final IStateMatcher doorBottom = api.predicateMatcher(door.defaultBlockState().setValue(GreenhouseDoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(DoorBlock.FACING, Direction.EAST), s -> Helpers.isBlock(s, door));
        final IStateMatcher doorTop = api.predicateMatcher(door.defaultBlockState().setValue(GreenhouseDoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(DoorBlock.FACING, Direction.EAST), s -> Helpers.isBlock(s, door));

        return api.makeMultiblock(new String[][] {
            {"XXXXX", "XXXXX", "XXXXX", "XXXXX", "XXXXX"},
            {"XXXXX", "X   X", "X   X", "X   X", "XXXXX"},
            {"XXXXX", "X   X", "X   X", "X   X", "XXXXX"},
            {"XXEXX", "X   X", "X 0 X", "X   X", "XXXXX"},
            {"XXDXX", "X   X", "X   X", "X   X", "XXXXX"},
            {"XXXXX", "XXXXX", "XXXXX", "XXXXX", "XXXXX"}
        },
            '0', api.airMatcher(),
            'X', api.predicateMatcher(FLBlocks.SEALED_BRICKS.get().defaultBlockState(), Mechanics.CELLAR),
            'D', doorBottom,
            'E', doorTop
        );
    }

    private static IMultiblock greenhouse(PatchouliAPI.IPatchouliAPI api, Greenhouse greenhouse, TagKey<Block> tag)
    {
        final IStateMatcher wall = api.predicateMatcher(greenBlock(greenhouse, Greenhouse.BlockType.WALL), s -> Helpers.isBlock(s, tag) && s.getBlock() instanceof GreenhouseWallBlock);
        final IStateMatcher roof = api.predicateMatcher(greenBlock(greenhouse, Greenhouse.BlockType.ROOF).setValue(StairBlock.FACING, Direction.EAST),s -> Helpers.isBlock(s, tag) && s.getBlock() instanceof GreenhouseStairBlock && s.getValue(StairBlock.HALF) == Half.BOTTOM);
        final IStateMatcher roof2 = api.predicateMatcher(greenBlock(greenhouse, Greenhouse.BlockType.ROOF).setValue(StairBlock.FACING, Direction.WEST), s -> Helpers.isBlock(s, tag) && s.getBlock() instanceof GreenhouseStairBlock && s.getValue(StairBlock.HALF) == Half.BOTTOM);
        final IStateMatcher top = api.predicateMatcher(greenBlock(greenhouse, Greenhouse.BlockType.ROOF_TOP), s -> Helpers.isBlock(s, tag) && s.getBlock() instanceof GreenhouseSlabBlock && s.getValue(SlabBlock.TYPE) == SlabType.BOTTOM);
        final IStateMatcher doorT = api.predicateMatcher(greenBlock(greenhouse, Greenhouse.BlockType.DOOR).setValue(GreenhouseDoorBlock.HALF, DoubleBlockHalf.UPPER).setValue(GreenhouseDoorBlock.FACING, Direction.EAST), s -> Helpers.isBlock(s, tag) && s.getBlock() instanceof GreenhouseDoorBlock);
        final IStateMatcher doorB = api.predicateMatcher(greenBlock(greenhouse, Greenhouse.BlockType.DOOR).setValue(GreenhouseDoorBlock.HALF, DoubleBlockHalf.LOWER).setValue(GreenhouseDoorBlock.FACING, Direction.EAST), s -> Helpers.isBlock(s, tag) && s.getBlock() instanceof GreenhouseDoorBlock);
        final IStateMatcher solid = api.predicateMatcher(TFCBlocks.SOIL.get(SoilBlockType.DIRT).get(SoilBlockType.Variant.SILTY_LOAM).get(), s -> true);
        final IStateMatcher station = api.predicateMatcher(FLBlocks.CLIMATE_STATION.get().defaultBlockState().setValue(ClimateStationBlock.STASIS, true), s -> Helpers.isBlock(s, FLBlocks.CLIMATE_STATION.get()));

        return api.makeMultiblock(new String[][] {
            {"     ", "     ", "TTTTT", "     ", "     "},
            {"     ", "RRRRR", "W   W", "SSSSS", "     "},
            {"RRRRR", "W   W", "W   W", "W   W", "SSSSS"},
            {"WWWWW", "W   W", "W 0 W", "W   W", "WWWWW"},
            {"WWEWW", "W   W", "W   W", "W   W", "WWWWW"},
            {"WWDWW", "W  CW", "W   W", "W   W", "WWWWW"},
            {"GGGGG", "GGGGG", "GGGGG", "GGGGG", "GGGGG"},
        },
            'C', station,
            '0', api.airMatcher(),
            'W', wall,
            'G', solid,
            'T', top,
            'R', roof,
            'S', roof2,
            'D', doorB,
            'E', doorT
        );
    }

    private static BlockState greenBlock(Greenhouse greenhouse, Greenhouse.BlockType block)
    {
        return FLBlocks.GREENHOUSE_BLOCKS.get(greenhouse).get(block).get().defaultBlockState();
    }

    private static void registerMultiblock(String name, Function<PatchouliAPI.IPatchouliAPI, IMultiblock> factory)
    {
        final PatchouliAPI.IPatchouliAPI api = PatchouliAPI.get();
        api.registerMultiblock(FLHelpers.identifier(name), factory.apply(api));
    }

}
