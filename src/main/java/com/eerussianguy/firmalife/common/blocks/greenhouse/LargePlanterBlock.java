package com.eerussianguy.firmalife.common.blocks.greenhouse;

import java.util.function.Consumer;
import com.eerussianguy.firmalife.client.FLClientHelpers;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blockentities.ClimateType;
import com.eerussianguy.firmalife.common.blockentities.LargePlanterBlockEntity;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.util.Mechanics;
import com.eerussianguy.firmalife.common.util.Plantable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.common.blocks.crop.CropHelpers;
import net.dries007.tfc.common.blocks.devices.DeviceBlock;
import net.dries007.tfc.common.blocks.soil.HoeOverlayBlock;
import net.dries007.tfc.common.capabilities.BlockCapabilities;
import net.dries007.tfc.util.Helpers;

public class LargePlanterBlock extends DeviceBlock implements HoeOverlayBlock
{
    public static final BooleanProperty WATERED = FLStateProperties.WATERED;

    private static final VoxelShape LARGE_SHAPE = box(0, 0, 0, 16, 8, 16);

    public LargePlanterBlock(ExtendedProperties properties)
    {
        super(properties, InventoryRemoveBehavior.DROP);
        registerDefaultState(getStateDefinition().any().setValue(WATERED, false));
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rand)
    {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof LargePlanterBlockEntity planter && state.getBlock() == this)
        {
            if (Mechanics.growthTick(level, pos, state, planter))
            {
                planter.updateBlockState(state);
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        final Plantable plant = Plantable.get(stack);
        final int slot = getUseSlot(hit, pos);
        if (level.getBlockEntity(pos) instanceof LargePlanterBlockEntity planter)
        {
            if (stack.getItem() == Items.BEDROCK && player.isCreative())
            {
                for (int i = 0; i < planter.slots(); i++)
                {
                    planter.setGrowth(i, 1f);
                }
                planter.markForSync();
                return ItemInteractionResult.SUCCESS;
            }
            if (CropHelpers.useFertilizer(level, player, hand, pos))
            {
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
            else if (plant != null)
            {
                if (plant.planter() != getPlanterType())
                {
                    player.displayClientMessage(Component.translatable("firmalife.greenhouse.wrong_type").append(FLHelpers.translateEnum(plant.planter())), true);
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                if (planter.getTier() < plant.tier())
                {
                    if (!planter.isClimateValid())
                    {
                        player.displayClientMessage(Component.translatable("firmalife.greenhouse.climate_invalid"), true);
                    }
                    else
                    {
                        player.displayClientMessage(Component.translatable("firmalife.greenhouse.wrong_tier"), true);
                    }
                    return ItemInteractionResult.sidedSuccess(level.isClientSide);
                }
                return insertSlot(level, planter, stack, player, slot);
            }
            else if (player.isShiftKeyDown() && stack.isEmpty())
            {
                return takeSlot(level, planter, slot, i -> ItemHandlerHelper.giveItemToPlayer(player, i));
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public PlanterType getPlanterType()
    {
        return PlanterType.LARGE;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack); // basically a convenience thing when you place next to another planter
        for (Direction d : Helpers.DIRECTIONS)
        {
            final BlockPos rel = pos.relative(d);
            if (level.getBlockEntity(rel) instanceof LargePlanterBlockEntity planter && planter.checkValid())
            {
                planter.setValid(level, rel, true, planter.getTier(), ClimateType.GREENHOUSE);
            }
        }
    }

    @Override
    public void addHoeOverlayInfo(Level level, BlockPos pos, BlockState state, Consumer<Component> text, boolean debug)
    {
        if (!level.isClientSide) return;
        if (level.getBlockEntity(pos) instanceof LargePlanterBlockEntity planter)
        {
            BlockHitResult target = FLClientHelpers.getTargetedLocation();
            if (target == null) return;

            final int slot = getUseSlot(target, pos);
            text.accept(Component.translatable("firmalife.planter.growth_water", String.format("%.2f", planter.getGrowth(slot)), String.format("%.2f", planter.getWater())));
            if (planter.getGrowth(slot) >= 1)
            {
                text.accept(Component.translatable("tfc.tooltip.farmland.mature"));
            }
            final Component invalidReason = planter.getInvalidReason();
            final boolean valid = invalidReason == null;
            text.accept(Component.translatable(valid ? "firmalife.greenhouse.valid_block" : "firmalife.greenhouse.invalid_block"));
            if (!valid)
            {
                text.accept(invalidReason);
            }

            text.accept(Component.translatable("tfc.tooltip.farmland.nutrients", format(planter, FarmlandBlockEntity.NutrientType.NITROGEN), format(planter, FarmlandBlockEntity.NutrientType.PHOSPHOROUS), format(planter, FarmlandBlockEntity.NutrientType.POTASSIUM)));
        }
    }


    private String format(LargePlanterBlockEntity planter, FarmlandBlockEntity.NutrientType value)
    {
        return String.format("%.2f", planter.getNutrient(value) * 100);
    }

    protected int getUseSlot(BlockHitResult hit, BlockPos pos)
    {
        return 0;
    }

    public ItemInteractionResult insertSlot(Level level, LargePlanterBlockEntity planter, ItemStack held, Player player, int slot)
    {
        IItemHandler inventory = Helpers.getCapability(BlockCapabilities.ITEM, planter);
        if (inventory != null) {
            var res = ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            if (inventory.getStackInSlot(slot).isEmpty())
            {
                res = FLHelpers.insertOne(level, held, slot, inventory, player);
                if (res.consumesAction())
                {
                    planter.setGrowth(slot, 0);
                    planter.updateCache();
                }
            }
            return res;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public static ItemInteractionResult takeSlot(Level level, LargePlanterBlockEntity planter, int slot, Consumer<ItemStack> onExtract)
    {
        IItemHandler inventory = Helpers.getCapability(BlockCapabilities.ITEM, planter);
        if(inventory != null) {
            Plantable plant = planter.getPlantable(slot);
            if (plant != null && planter.getGrowth(slot) >= 1)
            {
                if (planter.resetGrowthTo() == 0)
                {
                    inventory.extractItem(slot, 1, false); // discard the internal ingredient
                }
                final int seedAmount = level.random.nextFloat() < plant.extraSeedChance() ? 2 : 1;
                ItemStack seed = plant.getSeed();
                if (!seed.isEmpty())
                {
                    seed.setCount(seedAmount);
                    FLHelpers.roundCreationDate(seed);
                    onExtract.accept(seed);
                }
                ItemStack crop = plant.getCrop();
                FLHelpers.roundCreationDate(crop);
                onExtract.accept(crop);
                planter.setGrowth(slot, planter.resetGrowthTo());
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(WATERED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return LARGE_SHAPE;
    }
}
