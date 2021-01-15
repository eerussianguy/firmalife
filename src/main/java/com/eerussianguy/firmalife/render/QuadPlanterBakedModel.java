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
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.property.IExtendedBlockState;

import com.eerussianguy.firmalife.blocks.BlockQuadPlanter;
import com.eerussianguy.firmalife.init.StatePropertiesFL;
import com.eerussianguy.firmalife.recipe.PlanterRecipe;
import mcp.MethodsReturnNonnullByDefault;

import static com.eerussianguy.firmalife.FirmaLife.MOD_ID;

@MethodsReturnNonnullByDefault
public class QuadPlanterBakedModel implements IBakedModel
{
    private static final IModel dummy = ModelLoaderRegistry.getModelOrMissing(new ResourceLocation(MOD_ID, "block/quad_planter"));

    public QuadPlanterBakedModel() { }

    /**
     * Are you not entertained?
     */
    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand)
    {
        if (state == null || !(state.getBlock() instanceof BlockQuadPlanter)) return dummy.bake(dummy.getDefaultState(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter()).getQuads(state, side, rand);
        Map<String, String> sprites = new HashMap<>();
        sprites.put("soil", MOD_ID + (state.getValue(StatePropertiesFL.WET) ? ":blocks/potting_soil_wet" : ":blocks/potting_soil_dry"));
        if (state instanceof IExtendedBlockState)
        {
            IExtendedBlockState extendedState = (IExtendedBlockState) state;
            sprites.put("crop1", resolveTexture(extendedState, BlockQuadPlanter.CROP_1));
            sprites.put("crop2", resolveTexture(extendedState, BlockQuadPlanter.CROP_2));
            sprites.put("crop3", resolveTexture(extendedState, BlockQuadPlanter.CROP_3));
            sprites.put("crop4", resolveTexture(extendedState, BlockQuadPlanter.CROP_4));
        }
        IModel newModel = dummy.retexture(ImmutableMap.copyOf(sprites));
        IBakedModel bakedModel = newModel.bake(newModel.getDefaultState(), DefaultVertexFormats.BLOCK, ModelLoader.defaultTextureGetter());
        return bakedModel.getQuads(state, side, rand);
    }

    private String resolveTexture(IExtendedBlockState state, UnlistedCropProperty property)
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
}
