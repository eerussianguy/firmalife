package com.eerussianguy.firmalife.client.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
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
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.SeparateTransformsModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;
import net.neoforged.neoforge.client.model.pipeline.QuadBakingVertexConsumer;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.util.Helpers;

public class InventoryBlockModel implements IUnbakedGeometry<InventoryBlockModel>
{
    private final BlockModel baseModel;
    private final InventoryBlockModel.BakedModelFactory bakedModelFactory;

    public InventoryBlockModel(BlockModel blockModel, InventoryBlockModel.BakedModelFactory bakedModelFactory)
    {
        this.baseModel = blockModel;
        this.bakedModelFactory = bakedModelFactory;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides)
    {
        return bakedModelFactory.apply(
            context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), overrides,
            baseModel.bake(baker, baseModel, spriteGetter, modelState, context.useBlockLight())
        );
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context)
    {
        baseModel.resolveParents(modelGetter);
    }

    public static abstract class Baked extends SeparateTransformsModel.Baked
    {
        private final BakedModel baseModel;

        public Baked(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel)
        {
            //TODO using RenderHelpers#missingTexture() here tries to access the texture atlas before it is loaded, preventing the model from loading
            // is there a better fix than just passing null?
            super(isAmbientOcclusion, isGui3d, isSideLit, null, overrides, baseModel, ImmutableMap.of());
            this.baseModel = baseModel;
        }

        @Override
        public TextureAtlasSprite getParticleIcon(ModelData data)
        {
            return baseModel.getParticleIcon(data);
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand, ModelData data, @Nullable RenderType renderType)
        {
            final List<BakedQuad> quads = new ArrayList<>(super.getQuads(state, side, rand, data, renderType));
            final InventoryModelData inventory = data.get(InventoryModelData.PROPERTY);
            if (inventory != null && side == null)
            {
                quads.addAll(render(inventory));
            }
            return quads;
        }

        public List<BakedQuad> render(InventoryModelData data)
        {
            final int packedOverlay = OverlayTexture.NO_OVERLAY;
            final List<BakedQuad> quads = new ArrayList<>(24);

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

            // Inconveniently, this vertex consumer has to be manually baked after each quad. So, we listen to each
            // addVertex(x, y, z) to empty it, except the first call, and then remember to retrieve the final vertex
            final Baker baker = new Baker();
            render(data.inventory, data.state, data.pos, new PoseStack(), baker, data.light, packedOverlay);
            if (baker.count > 0) // We baked at least one quad, so get the last one
            {
                quads.add(baker.bakeQuad());
            }
            return quads;
        }

        protected abstract void render(List<ItemStack> inventory, BlockState state, BlockPos pos, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay);
    }

    public record InventoryModelData(List<ItemStack> inventory, int light, BlockState state, BlockPos pos)
    {
        public static final ModelProperty<InventoryBlockModel.InventoryModelData> PROPERTY = new ModelProperty<>();

        public static ModelData of(BlockAndTintGetter level, InventoryBlockEntity<?> blockEntity)
        {
            final ImmutableList.Builder<ItemStack> builder = new ImmutableList.Builder<>();
            Helpers.copyTo(builder, blockEntity.getInventory());
            BlockPos pos = blockEntity.getBlockPos();

            return ModelData.of(
                PROPERTY,
                new InventoryModelData(
                    builder.build(),
                    LightTexture.pack(level.getBrightness(LightLayer.BLOCK, pos), level.getBrightness(LightLayer.SKY, pos)),
                    blockEntity.getBlockState(),
                    pos
                )
            );
        }
    }

    public interface BakedModelFactory
    {
        InventoryBlockModel.Baked apply(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, ItemOverrides overrides, BakedModel baseModel);
    }

    public record Loader(InventoryBlockModel.BakedModelFactory factory) implements IGeometryLoader<InventoryBlockModel>
    {
        @Override
        public InventoryBlockModel read(JsonObject json, JsonDeserializationContext ctx) throws JsonParseException
        {
            final BlockModel base = ctx.deserialize(GsonHelper.getAsJsonObject(json, "base"), BlockModel.class);
            return new InventoryBlockModel(base, factory);
        }
    }
}
