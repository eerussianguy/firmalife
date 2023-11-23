package com.eerussianguy.firmalife.common.blocks;

import java.util.Map;
import java.util.stream.Collectors;
import com.eerussianguy.firmalife.common.blockentities.StovetopGrillBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.client.IHighlightHandler;
import net.dries007.tfc.common.TFCDamageSources;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.BottomSupportedDeviceBlock;
import net.dries007.tfc.common.capabilities.Capabilities;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blockentities.GrillBlockEntity.*;


public class StovetopGrillBlock extends BottomSupportedDeviceBlock implements IHighlightHandler
{
    public static int getSlotForSelection(BlockHitResult result)
    {
        final Vec3 location = result.getLocation();
        final BlockPos pos = result.getBlockPos();
        for (Map.Entry<Integer, AABB> entry : SLOT_BOUNDS.entrySet())
        {
            if (entry.getValue().move(pos).contains(location))
            {
                return entry.getKey();
            }
        }
        return -1;
    }

    private static final Map<Integer, VoxelShape> SLOT_RENDER_SHAPES = Map.of(
        4, Shapes.box(0.4, 0, 0.4, 0.6, 0.22, 0.6),
        3, Shapes.box(0.6, 0, 0.6, 0.8, 0.22, 0.8),
        2, Shapes.box(0.6, 0, 0.2, 0.8, 0.22, 0.4),
        1, Shapes.box(0.2, 0, 0.6, 0.4, 0.22, 0.8),
        0, Shapes.box(0.2, 0, 0.2, 0.4, 0.22, 0.4)
    );

    private static final Map<Integer, AABB> SLOT_BOUNDS = SLOT_RENDER_SHAPES.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().bounds().inflate(0.01f)));
    public static final Map<Integer, Vec3> SLOT_CENTERS = SLOT_BOUNDS.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getCenter().multiply(1, 0, 1).add(0, 0.125, 0)));

    public static final VoxelShape GRILL_SHAPE = box(1, 0, 1, 15, 1, 15);

    public StovetopGrillBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP, GRILL_SHAPE);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource rand)
    {
        super.animateTick(state, level, pos, rand);
        if (level.getBlockEntity(pos) instanceof StovetopGrillBlockEntity grill && grill.getTemperature() > 0f)
        {
            final var inv = Helpers.getCapability(grill, Capabilities.ITEM);
            if (inv != null)
            {
                SLOT_CENTERS.forEach((slot, vec) -> {
                    if (!inv.getStackInSlot(slot).isEmpty() && rand.nextFloat() < 0.4f)
                    {
                        final double x = vec.x + pos.getX();
                        final double y = vec.y + pos.getY();
                        final double z = vec.z + pos.getZ();
                        level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 0.25F, rand.nextFloat() * 0.7F + 0.4F, false);
                        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0, 0);
                    }
                });
            }
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (level.getBlockEntity(pos) instanceof StovetopGrillBlockEntity grill)
        {
            final ItemStack stack = player.getItemInHand(hand);
            final var inv = Helpers.getCapability(grill, Capabilities.ITEM);
            final int slot = getSlotForSelection(result);
            final ItemStack current = slot == -1 || inv == null ? ItemStack.EMPTY : inv.getStackInSlot(slot);
            if (!stack.isEmpty() && inv != null && slot != -1 && current.isEmpty() && inv.isItemValid(slot, stack))
            {
                ItemHandlerHelper.giveItemToPlayer(player, inv.insertItem(slot, stack.split(1), false));
                grill.markForSync();
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
            if (stack.isEmpty() && slot != -1 && inv != null && !current.isEmpty())
            {
                // if we are shifting or if there's no possible recipe (eg, this heating has already been completed)
                if (!inv.isItemValid(slot, current) || player.isShiftKeyDown())
                {
                    ItemHandlerHelper.giveItemToPlayer(player, inv.extractItem(slot, 64, false));
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
            else
            {
                if (player instanceof ServerPlayer serverPlayer)
                {
                    Helpers.openScreen(serverPlayer, grill, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity)
    {
        if (!entity.fireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entity) && level.getBlockEntity(pos) instanceof StovetopGrillBlockEntity grill && grill.getTemperature() > 0)
        {
            TFCDamageSources.grill(entity, 1.0F);
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public boolean drawHighlight(Level level, BlockPos pos, Player player, BlockHitResult rayTrace, PoseStack stack, MultiBufferSource buffers, Vec3 rendererPosition)
    {
        final int slot = getSlotForSelection(rayTrace);
        if (slot != -1)
        {
            IHighlightHandler.drawBox(stack, SLOT_RENDER_SHAPES.get(slot), buffers, pos, rendererPosition, 1f, 0f, 0f, 1f);
            return true;
        }
        return false;
    }
}
