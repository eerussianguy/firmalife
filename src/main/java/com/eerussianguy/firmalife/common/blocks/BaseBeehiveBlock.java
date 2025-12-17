package com.eerussianguy.firmalife.common.blocks;

import java.util.function.Consumer;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.FLBeehiveBlockEntity;
import com.eerussianguy.firmalife.common.blockentities.FLBlockEntities;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeAbility;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.misc.FLEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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
        if (level.getGameTime() % 24000 > 12000)
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
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack held, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
    {
        if (Helpers.isItem(held, FLItems.BEEHIVE_FRAME.get()))
        {
            final var res = FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.BEEHIVE, (hive, inv) ->
                FLHelpers.insertOneAny(level, held, 0, FLBeehiveBlockEntity.FRAME_SLOTS - 1, inv, player)
            );
            if (res.consumesAction())
                return res;
        }
        else if (held.isEmpty() && player.isShiftKeyDown())
        {
            final var res = FLHelpers.consumeItemInventory(level, pos, FLBlockEntities.BEEHIVE, (hive, inv) ->
                FLHelpers.takeOneAny(level, 0, FLBeehiveBlockEntity.FRAME_SLOTS - 1, inv, player)
            );
            if (res.consumesAction())
            {
                if (BaseBeehiveBlock.shouldAnger(level, pos))
                    attack(player);
                return res;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random)
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
            if (hive.getHoney() > 0)
            {
                tooltip.accept(Component.translatable("firmalife.beehive.honey", String.valueOf(hive.getHoney())).withStyle(ChatFormatting.GOLD));
            }
            final float temp = Climate.getTemperature(level, pos);
            final BeeComponent bee = hive.getBee();
            if (bee.hasQueen())
            {
                tooltip.accept(Component.translatable("firmalife.beehive.has_queen"));
                bee.addTooltipInfo(tooltip);
                final float minTemp = BeeAbility.getMinTemperature(bee.getAbility(BeeAbility.HARDINESS));
                if (temp <= minTemp)
                {
                    tooltip.accept(Component.translatable("firmalife.beehive.bee_cold", minTemp, String.format("%.2f", temp)).withStyle(ChatFormatting.AQUA));
                }
            }
            final int flowers = hive.getFlowers(bee, false);
            tooltip.accept(Component.translatable("firmalife.beehive.flowers", flowers));
            if (flowers < FLBeehiveBlockEntity.MIN_FLOWERS)
            {
                tooltip.accept(Component.translatable("firmalife.beehive.min_flowers"));
            }
            else
            {
                if (!bee.hasQueen())
                {
                    int breed = hive.getBreedTickChanceInverted(bee, flowers);
                    if (breed == 0) tooltip.accept(Component.translatable("firmalife.beehive.breed_chance_100"));
                    else tooltip.accept(Component.translatable("firmalife.beehive.breed_chance", breed));
                }
                else
                {
                    int honey = hive.getHoneyTickChanceInverted(bee, flowers);
                    if (honey == 0) tooltip.accept(Component.translatable("firmalife.beehive.honey_chance_100"));
                    else tooltip.accept(Component.translatable("firmalife.beehive.honey_chance", honey));
                }
            }
        }

    }
}
