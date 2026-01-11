package com.eerussianguy.firmalife.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.RenderHelpers;

// TODO replacing this with a blockstate model loader if in a version that supports custom loaders is probably the best option
// https://docs.neoforged.net/docs/1.21.5/resources/client/models/modelloaders/#block-state-definition-loaders
// TODO rename if used for something other than greenhouse blocks
// TODO this does not support side culling
public class GreenhouseBlockModel implements IUnbakedGeometry<GreenhouseBlockModel>
{

    private final BakedModelFactory bakedModelFactory;

    public GreenhouseBlockModel(BakedModelFactory bakedModelFactory)
    {
        this.bakedModelFactory = bakedModelFactory;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        return bakedModelFactory.apply(context, context.useAmbientOcclusion(), context.isGui3d(), false, overrides);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {

    }

    public abstract static class Baked implements IDynamicBakedModel
    {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;

        public Baked(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides)
        {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource, ModelData modelData, @Nullable RenderType renderType)
        {
            StaticModelData data = modelData.get(StaticModelData.PROPERTY);
            if (data != null)
            {
                return data.quads;
            }
            return List.of();
        }

        protected abstract void render(BlockState state, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay);

        @Override
        public boolean useAmbientOcclusion()
        {
            return isAmbientOcclusion;
        }

        @Override
        public boolean isGui3d()
        {
            return isGui3d;
        }

        @Override
        public boolean usesBlockLight()
        {
            return true;
        }

        @Override
        public boolean isCustomRenderer()
        {
            return false;
        }

        @Override
        public TextureAtlasSprite getParticleIcon()
        {
            return RenderHelpers.missingTexture();
        }

        @Override
        public ItemOverrides getOverrides()
        {
            return ItemOverrides.EMPTY;
        }

        @Override
        public ModelData getModelData(BlockAndTintGetter level, BlockPos pos, BlockState state, ModelData modelData)
        {
            final int packedLight = LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos));
            final int packedOverlay = OverlayTexture.NO_OVERLAY;
            final List<BakedQuad> quads = new ArrayList<>(24);

            // Copied from TFC, needs to bake every quad manually or rendering will break.
            // See TFC's SimpleStaticBlockEntityModel#render for a better explanation
            class Baker extends QuadBakingVertexConsumer
            {
                int count = 0;

                @Override
                public VertexConsumer addVertex(float x, float y, float z)
                {
                    if (count == 4)
                    {
                        count = 0;
                        quads.add(bakeQuad());
                    }
                    count++;
                    return super.addVertex(x, y, z);
                }
            }
            final Baker baker = new Baker();
            final PoseStack poseStack = new PoseStack();

            render(state, poseStack, baker, packedLight, packedOverlay);
            if (baker.count > 0) // We baked at least one quad, so get the last one
            {
                quads.add(baker.bakeQuad());
            }
            return ModelData.of(StaticModelData.PROPERTY, quads.isEmpty() ? StaticModelData.EMPTY : new StaticModelData(quads));
        }
    }

    record StaticModelData(List<BakedQuad> quads)
    {
        public static final ModelProperty<StaticModelData> PROPERTY = new ModelProperty<>();
        public static final StaticModelData EMPTY = new StaticModelData(new ArrayList<>());
    }

    public interface BakedModelFactory
    {
        GreenhouseBlockModel.Baked apply(IGeometryBakingContext context, boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides);
    }

    public record Loader(BakedModelFactory factory) implements IGeometryLoader<GreenhouseBlockModel>
    {
        @Override
        public GreenhouseBlockModel read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
        {
            return new GreenhouseBlockModel(factory);
        }
    }
}
