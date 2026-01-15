package com.eerussianguy.firmalife.common.blocks.bee;

import java.util.function.Consumer;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.blocks.FourWayDeviceBlock;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeAbility;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.misc.FLEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.devices.FirepitBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.Climate;

public class BaseBeehiveBlock extends FourWayDeviceBlock implements HoeOverlayBlock
{
    public static boolean shouldAnger(Level level, BlockPos pos)
    {
        if (level.getBrightness(LightLayer.SKY, pos) < 2)
        {
            return false;
        }
        if (hasFirepit(level, pos)) return false;
        return level.getBlockEntity(pos, FLBlockEntities.BEEHIVE.get()).map(hive -> {
            return level.random.nextFloat() > hive.getBee().getAbility(BeeAbility.CALMNESS) / 10f;
        }).orElse(false);
    }

    public static boolean hasFirepit(Level level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        mutable.set(pos);
        for (int i = 0; i < 5; i++)
        {
            mutable.move(0, -1, 0);
            BlockState state = level.getBlockState(mutable);
            if (!state.canBeReplaced())
            {
                if (state.getBlock() instanceof FirepitBlock)
                {
                    return state.getValue(FirepitBlock.LIT);
                }
                return false; // we hit a solid block
            }
        }
        return false;
    }

    public static void attack(Player player)
    {
        player.addEffect(new MobEffectInstance(FLEffects.SWARM.holder(), 100));
    }

    public static final BooleanProperty HONEY = FLStateProperties.HONEY;
    public static final BooleanProperty BEES = FLStateProperties.BEES;

    public BaseBeehiveBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(BEES, false).setValue(HONEY, false));
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (Helpers.isItem(held, FLTags.Items.BEEHIVE_FRAMES))
        {
            final var res = FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.BEEHIVE, (hive, inv) ->
                FLHelpers.insertOneAny(level, held, 0, inv.getSlots() - 1, inv, player)
            );
            if (res.consumesAction())
            {
                Helpers.playSound(level, pos, SoundEvents.BAMBOO_WOOD_PLACE);
                return res;
            }
        }
        else if (held.isEmpty())
        {
            final var res = FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.BEEHIVE, (hive, inv) ->
                FLHelpers.takeOneAny(level, 0, inv.getSlots() - 1, inv, player)
            );
            if (res.consumesAction())
            {
                if (BaseBeehiveBlock.shouldAnger(level, pos) && !player.isCreative())
                    attack(player);
                Helpers.playSound(level, pos, SoundEvents.BAMBOO_WOOD_BREAK);
                return res;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
    {
        if (level.getBlockEntity(pos) instanceof FLBeehiveBlockEntity hive)
        {
            hive.tryPeriodicUpdate();
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder.add(HONEY, BEES));
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity entity, ItemStack tool)
    {
        if (BaseBeehiveBlock.shouldAnger(level, pos))
        {
            attack(player);
        }
        super.playerDestroy(level, player, pos, state, entity, tool);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random)
    {
        if (state.getValue(HONEY))
        {
            for(int i = 0; i < random.nextInt(1) + 1; ++i)
            {
                WildBeehiveBlock.honeyDripParticle(level, pos, state);
            }
        }
        if (level.getBlockEntity(pos) instanceof FLBeehiveBlockEntity hive)
        {
            final BlockPos linked = hive.getLinkedHive();
            if (linked == null)
                return;

            final double t = random.nextDouble();
            double x = pos.getX() + 0.5 + t * (linked.getX() - pos.getX());
            double y = pos.getY() + 0.5 + t * (linked.getY() - pos.getY());
            double z = pos.getZ() + 0.5 + t * (linked.getZ() - pos.getZ());
            x += (random.nextDouble() - 0.5) * 0.2;
            y += (random.nextDouble() - 0.5) * 0.2;
            z += (random.nextDouble() - 0.5) * 0.2;

            level.addParticle(ParticleTypes.SMOKE, x, y, z, 0, -0.01, 0);
        }
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState blockState, Consumer<Component> tooltip, boolean debug)
    {
        if (level.getBlockEntity(pos) instanceof FLBeehiveBlockEntity hive)
        {
            if (hive.getLinkedHive() != null)
            {
                tooltip.accept(Component.translatable("firmalife.beehive.swarm"));
            }
            final int honey = hive.getHoney();
            if (honey > 0)
            {
                tooltip.accept(Component.translatable("firmalife.beehive.honey" + (honey == 1 ? "_1" : ""), String.valueOf(honey)).withStyle(ChatFormatting.GOLD));
            }
            final float temp = Climate.getInstantTemperature(level, pos);
            final BeeComponent bee = hive.getBee();
            if (bee.hasQueen())
            {
                tooltip.accept(Component.translatable("firmalife.beehive.has_queen"));
                bee.addTooltipInfo(tooltip);
                final float minTemp = hive.getMinTemperature();
                if (temp <= minTemp || hive.getAvailableFrames() == 0)
                {
                    if (temp <= minTemp)
                        tooltip.accept(Component.translatable("firmalife.beehive.bee_cold", String.format("%.2f", temp), minTemp).withStyle(ChatFormatting.AQUA));
                    if (honey == 0)
                    {
                        tooltip.accept(Component.translatable("firmalife.beehive.starving"));
                    }
                }
                else
                {
                    tooltip.accept(Component.translatable("firmalife.beehive.bee_warm", String.format("%.2f", temp), minTemp).withStyle(ChatFormatting.DARK_GREEN));
                }
            }
            final int flowers = hive.countFlowers().size();
            tooltip.accept(Component.translatable("firmalife.beehive.flowers", flowers));
            if (flowers < FLBeehiveBlockEntity.MIN_FLOWERS)
            {
                tooltip.accept(Component.translatable("firmalife.beehive.min_flowers"));
            }
            else if (hive.isOccluded())
            {
                tooltip.accept(Component.translatable("firmalife.beehive.occluded"));
            }
            else if (bee.hasQueen())
            {
                int chance = hive.getHoneyTickChanceInverted(bee, flowers);
                if (chance == 0) tooltip.accept(Component.translatable("firmalife.beehive.honey_chance_100"));
                if (chance == Integer.MAX_VALUE) tooltip.accept(Component.translatable("firmalife.beehive.honey_chance_0"));
                else tooltip.accept(Component.translatable("firmalife.beehive.honey_chance", chance));
            }
        }

    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state)
    {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
    {
        return level.getBlockEntity(pos) instanceof FLBeehiveBlockEntity hive ? Mth.lerpDiscrete((float) hive.getHoney() / hive.getInventory().getSlots(), 0, 15) : 0;
    }
}
