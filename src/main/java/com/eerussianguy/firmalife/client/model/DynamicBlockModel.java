package com.eerussianguy.firmalife.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.SeparateTransformsModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;


public class DynamicBlockModel implements IUnbakedGeometry<DynamicBlockModel>
{
    private final BlockModel baseModel;
    private final BakedModelFactory bakedModelFactory;

    public DynamicBlockModel(BlockModel blockModel, BakedModelFactory bakedModelFactory)
    {
        this.baseModel = blockModel;
        this.bakedModelFactory = bakedModelFactory;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation)
    {
        return bakedModelFactory.apply(
            context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), overrides,
            baseModel.bake(baker, baseModel, spriteGetter, modelState, modelLocation, context.useBlockLight())
        );
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        baseModel.resolveParents(modelGetter);
    }

    public static class Baked extends SeparateTransformsModel.Baked
    {
        private final BakedModel baseModel;

        public Baked(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
        {
            super(isAmbientOcclusion, isGui3d, isSideLit, RenderHelpers.missingTexture(), overrides, baseModel, ImmutableMap.of());
            this.baseModel = baseModel;
        }

        @Override
        public TextureAtlasSprite getParticleIcon(@NotNull ModelData data)
        {
            return baseModel.getParticleIcon(data);
        }

        @Override
        public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType)
        {
            final List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand, data, renderType));
            final StaticModelData extraQuads = data.get(StaticModelData.PROPERTY);
            if (extraQuads != null && side == null)
            {
                quads.addAll(extraQuads.quads);
            }
            return quads;
        }
    }

    record StaticModelData(List<BakedQuad> quads)
    {
        public static final ModelProperty<StaticModelData> PROPERTY = new ModelProperty<>();
        public static final StaticModelData EMPTY = new StaticModelData(new ArrayList<>());
    }

    public interface BakedModelFactory
    {
        Baked apply(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel);
    }

    public record Loader(BakedModelFactory factory) implements IGeometryLoader<DynamicBlockModel>
    {
        @Override
        public DynamicBlockModel read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
        {
            final BlockModel base = ctx.deserialize(GsonHelper.getAsJsonObject(json, "base"), BlockModel.class);
            return new DynamicBlockModel(base, factory);
        }
    }
}
