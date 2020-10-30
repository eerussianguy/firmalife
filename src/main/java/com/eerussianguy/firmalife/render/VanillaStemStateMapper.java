package com.eerussianguy.firmalife.render;

import java.util.Map;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;

import com.eerussianguy.firmalife.blocks.BlockStemCrop;

import static net.dries007.tfc.objects.blocks.agriculture.BlockCropTFC.WILD;

public class VanillaStemStateMapper extends StateMapperBase
{
    //tfc data says facing=[dir] and stage=[0..maxStage]
    //vanilla can render a vertical stalk wih facing=up and age=[0..7]
    //or a bent stalk with facing=[dir] and no other parameters.
    //visually, we want stage=maxStage to render a bent stalk
    //and we want stage=0 to render age=0, and we want stage=maxStage-1 to render age=7
    //for all other stages we can interpolate
    //this returns the vanilla age from 0 to 7, or -1 if fully grown
    public static int getVanillaAge(IBlockState state)
    {
        BlockStemCrop block = (BlockStemCrop) state.getBlock();
        PropertyInteger stage = block.getStageProperty();
        int growthStage = state.getValue(stage);
        int maxStage = block.getCrop().getMaxStage();
        if (growthStage == maxStage)
            return -1;
        float fractionGrown = ((float) growthStage) / ((float) (maxStage - 1));
        return (int) (fractionGrown * 7.0f);
    }

    @Override
    public ModelResourceLocation getModelResourceLocation(IBlockState state)
    {

        BlockStemCrop block = (BlockStemCrop) state.getBlock();

        //use the vanilla melon stem blockstates json
        String s = Block.REGISTRY.getNameForObject(Blocks.MELON_STEM).toString();
        Map<IProperty<?>, Comparable<?>> map = Maps.newLinkedHashMap(state.getProperties());
        //always skip the wild parameter, since it doesn't affect rendering
        map.remove(WILD);

        int vanillaAge = getVanillaAge(state);
        map.remove(block.getStageProperty());

        //spoof a vanilla melon's blockstate
        if (vanillaAge >= 0)
        {
            map.put(BlockStem.AGE, vanillaAge);
            map.remove(BlockStemCrop.FACING); //needed to get correct insertion order
            map.put(BlockStemCrop.FACING, EnumFacing.UP);
        }
        else if (map.get(BlockStemCrop.FACING) == EnumFacing.UP)
        {
            //this should never happen in reality but it is a possible blockstate
            //so we have to include something here so that we don't get
            //missing variant errors
            map.put(BlockStemCrop.FACING, EnumFacing.NORTH);
        }
        //if vanillaAge is -1, then the crop is fully grown, so it keeps only the FACING property

        return new ModelResourceLocation(s, getPropertyString(map));
    }
}
