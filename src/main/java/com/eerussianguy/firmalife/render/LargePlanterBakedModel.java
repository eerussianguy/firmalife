package com.eerussianguy.firmalife.render;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.property.IExtendedBlockState;

import com.eerussianguy.firmalife.blocks.BlockLargePlanter;
import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.recipe.PlanterRecipe;
import mcp.MethodsReturnNonnullByDefault;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;
import static com.eerussianguy.firmalife.util.ClientHelpers.bake;

@MethodsReturnNonnullByDefault
public class LargePlanterBakedModel implements IBakedModel
{
    private static final IModel dummy = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(MOD_ID, "block/large_planter"));

    public LargePlanterBakedModel() { }

    /**
     * Are you not entertained?
     */
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        if (state == null || !(state.getBlock() instanceof BlockLargePlanter)) return bake(dummy).getQuads(state, side, rand);
        Map<String, String> sprites = new HashMap<>();
        sprites.put("soil", MOD_ID + (state.getValue(StatePropertiesFL.WET) ? ":blocks/potting_soil_wet" : ":blocks/potting_soil_dry"));
        if (state instanceof IExtendedBlockState)
        {
            sprites.put("crop1", resolveTexture((IExtendedBlockState) state, BlockLargePlanter.CROP));
        }
        IModel newModel = dummy.retexture(ImmutableMap.copyOf(sprites));
        return bake(newModel).getQuads(state, side, rand);
    }

    @Override
    public boolean isAmbientOcclusion()
    {
        return true;
    }

    @Override
    public boolean isGui3d()
    {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer()
    {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture()
    {
        return Objects.requireNonNull(Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry("minecraft:blocks/hardened_clay"));
    }

    @Override
    public ItemOverrideList getOverrides()
    {
        return ItemOverrideList.NONE;
    }

    protected String resolveTexture(IExtendedBlockState state, UnlistedCropProperty property)
    {
        PlanterRecipe.PlantInfo info = state.getValue(property);
        if (info == null || info.getRecipe() == null) return "tfc:blocks/empty";
        ResourceLocation crop = info.getRecipe().getRegistryName();
        if (crop != null && !property.valueToString(info).equals("null")) // epic non-null null
        {
            return crop.getNamespace() + ":blocks/crop/" + crop.getPath() + "_" + info.getStage();
        }
        return "tfc:blocks/empty";
    }
}
