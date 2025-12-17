package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.client.model.InventoryBlockModel;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.BaseBeehiveBlock;
import com.eerussianguy.firmalife.common.blocks.WildBeehiveBlock;
import com.eerussianguy.firmalife.common.blocks.WoodenBeehiveBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeAbility;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.entities.FLBee;
import com.eerussianguy.firmalife.common.entities.FLEntities;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.blocks.plant.PlantBlock;
import net.dries007.tfc.common.blocks.plant.ShortGrassBlock;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.DirtBlock;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.climate.Climate;

public class FLBeehiveBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements ICalendarTickable
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, FLBeehiveBlockEntity hive)
    {
        hive.checkForLastTickSync();
        hive.checkForCalendarUpdate();

        if (level.getGameTime() % 60 == 0)
        {
            hive.updateState();
        }
        //handle interval for spawning the entities
        if ((level.getGameTime() + pos.asLong()) % ENTITY_HANDLING_INTERVAL == 0)
        {
            hive.controlEntitiesTick();
        }
    }

    public static final int MIN_FLOWERS = 10;
    public static final int UPDATE_INTERVAL = ICalendar.CALENDAR_TICKS_IN_DAY;
    public static final int ENTITY_HANDLING_INTERVAL = 1000;
    public static final int FRAME_SLOTS = 4;

    private static final FarmlandBlockEntity.NutrientType N = FarmlandBlockEntity.NutrientType.NITROGEN;
    private static final FarmlandBlockEntity.NutrientType P = FarmlandBlockEntity.NutrientType.PHOSPHOROUS;
    private static final FarmlandBlockEntity.NutrientType K = FarmlandBlockEntity.NutrientType.POTASSIUM;

    private int beesInWorld;
    private long lastPlayerTick, lastAreaTick;
    private int honey;
    @Nullable private BlockPos linkedHive = null;
    private long linkedHiveTick = 0L;

    private BeeComponent beeData = BeeComponent.DEFAULT;

    public FLBeehiveBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.BEEHIVE.get(), pos, state, defaultInventory(FRAME_SLOTS), FirmaLife.MOD_ID);
        lastPlayerTick = Integer.MIN_VALUE;
        lastAreaTick = Calendars.SERVER.getTicks();
        honey = 0;
        beesInWorld = 0;

        sidedInventory
            .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3), Direction.Plane.HORIZONTAL)
            .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3), Direction.DOWN);
    }

    public @Nullable BlockPos getLinkedHive()
    {
        return linkedHive;
    }

    public void linkSwarm(BlockPos origin)
    {
        if (linkedHive != null)
            return;
        linkedHive = origin;
        linkedHiveTick = Calendars.SERVER.getTicks();
        markForSync();
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.saveAdditional(nbt, access);
        nbt.putLong("lastTick", lastPlayerTick);
        nbt.putLong("lastAreaTick", lastAreaTick);
        nbt.putInt("honey", honey);
        nbt.putInt("beesInWorld", beesInWorld);
        nbt.putLong("linkedHiveTick", linkedHiveTick);
        if (linkedHive != null)
            nbt.putLong("linkedHive", linkedHive.asLong());

        nbt.put("queen", BeeComponent.CODEC.encodeStart(NbtOps.INSTANCE, beeData).getOrThrow());
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider access)
    {
        super.loadAdditional(nbt, access);
        lastPlayerTick = nbt.getLong("lastTick");
        lastAreaTick = nbt.getLong("lastAreaTick");
        honey = Math.min(nbt.getInt("honey"), getMaxHoney());
        beesInWorld = nbt.getInt("beesInWorld");
        linkedHiveTick = nbt.getLong("linkedHiveTick");
        linkedHive = nbt.contains("linkedHive", CompoundTag.TAG_LONG) ? BlockPos.of(nbt.getLong("linkedHive")) : null;
        beeData = BeeComponent.CODEC.parse(NbtOps.INSTANCE, nbt.getCompound("queen")).getOrThrow();

        requestModelDataUpdate();
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        tryPeriodicUpdate();
    }

    public void tryPeriodicUpdate()
    {
        long now = Calendars.SERVER.getTicks();
        //handle update interval
        if (now > (lastAreaTick + UPDATE_INTERVAL))
        {
            while (lastAreaTick < now)
            {
                updateTick();
                lastAreaTick += UPDATE_INTERVAL;
            }
            markForSync();
        }

    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        requestModelDataUpdate();
        if (level != null)
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
    }

    public BeeComponent getBee()
    {
        return beeData;
    }

    /**
     * Main method called periodically to perform bee actions
     */
    private void updateTick()
    {
        assert level != null;

        Direction direction = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        BlockPos posInFront = worldPosition.relative(direction);
        // check if the bees have access out the front
        if (!level.getBlockState(posInFront).getCollisionShape(level, posInFront).isEmpty())
        {
            return;
        }

        // perform area of effect actions
        final int flowers = getFlowers(beeData, true);
        final int breedTickChanceInverted = getBreedTickChanceInverted(beeData, flowers);
        if (flowers > MIN_FLOWERS && (breedTickChanceInverted == 0 || level.random.nextInt(breedTickChanceInverted) == 0))
        {
            // todo
        }
        final int honeyChanceInverted = getHoneyTickChanceInverted(beeData, flowers);
        if (flowers > MIN_FLOWERS && (honeyChanceInverted == 0 || level.random.nextInt(honeyChanceInverted) == 0))
        {
            addHoney(1);
        }

    }

    private void controlEntitiesTick()
    {
        assert level != null;
        if (level.isNight() && beesInWorld > 0)
        {
            beesInWorld = 0;
        }
        else if (level.isDay() && beesInWorld <= 0)
        {
            final Direction direction = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            final BlockPos posInFront = worldPosition.relative(direction);

            if (isWarmEnough() && beeData.hasQueen() && beesInWorld == 0 && level.getBlockState(posInFront).getCollisionShape(level, posInFront).isEmpty())
            {
                FLBee beeEntity = FLEntities.FLBEE.get().create(level);
                assert beeEntity != null;

                beeEntity.moveTo(worldPosition.relative(direction).getCenter());
                beeEntity.setYRot(direction.toYRot());
                beeEntity.setSpawnPos(posInFront);
                level.addFreshEntity(beeEntity);

                level.playSound(null, worldPosition, SoundEvents.BEEHIVE_EXIT, SoundSource.BLOCKS, 1.0F, 1.0F);
                beesInWorld++;
            }
        }
    }

    public boolean isWarmEnough()
    {
        assert level != null;
        return Climate.getTemperature(level, worldPosition) > BeeAbility.getMinTemperature(beeData.getAbility(BeeAbility.HARDINESS));
    }

    @SuppressWarnings("deprecation")
    public int getFlowers(BeeComponent bee, boolean tick)
    {
        assert level != null;
        int flowers = 0;
        final BlockPos min = worldPosition.offset(-5, -5, -5);
        final BlockPos max = worldPosition.offset(5, 5, 5);
        if (level.hasChunksAt(min, max))
        {
            for (BlockPos pos : BlockPos.betweenClosed(min, max))
            {
                final BlockState state = level.getBlockState(pos);
                if (isFlower(state))
                {
                    flowers += 1;
                }
                if (tick && bee.hasQueen())
                {
                    tickPosition(pos, state, bee);
                }
            }
        }
        return flowers;
    }

    private boolean isFlower(BlockState state)
    {
        return Helpers.isBlock(state, BlockTags.FLOWERS) || (state.getBlock() instanceof PlantBlock && !(state.getBlock() instanceof ShortGrassBlock)) || (state.getBlock() instanceof LargePlanterBlock && state.getValue(LargePlanterBlock.WATERED));
    }

    public int getHoneyTickChanceInverted(BeeComponent bee, int flowers)
    {
        int chance = 30;
        if (bee.hasQueen())
        {
            chance += 10 - bee.getAbility(BeeAbility.PRODUCTION);
        }
        else
        {
            return 0;
        }
        return Math.max(0, chance - Mth.ceil((0.2 * Math.min(flowers, 60))));
    }

    public int getBreedTickChanceInverted(BeeComponent bee, int flowers)
    {
        int chance = 0;
        if (bee.hasQueen())
        {
            chance += 10 - bee.getAbility(BeeAbility.FERTILITY);
        }
        else
        {
            // no bees, have to give some chance
            chance = 80;
        }
        // flowers increase probability
        return Math.max(0, chance - Math.min(flowers, 60));
    }

    public void addHoney(int amount)
    {
        honey = Math.min(getMaxHoney(), amount + honey);
        markForSync();
    }

    public int takeHoney(int amount)
    {
        final int take = Math.min(amount, honey);
        honey -= take;
        updateState();
        markForSync();
        return take;
    }

    public int getMaxHoney()
    {
        return 12;
    }

    public int getHoney()
    {
        return honey;
    }

    private void tickPosition(BlockPos pos, BlockState state, BeeComponent bee)
    {
        assert level != null;
        final Block block = state.getBlock();

        if (level.getBlockEntity(pos) instanceof IFarmland farmland)
        {
            final float cropAffinity = (float) bee.getAbility(BeeAbility.CROP_AFFINITY); // 0 -> 10 scale
            if (cropAffinity >= 1 && level.random.nextInt(50) == 0)
            {
                final int which = level.random.nextInt(3); // 0, 1, 2
                final float nut = level.random.nextFloat() * cropAffinity * 0.01f;
                final float cap = (cropAffinity / 10) * 0.5f; // max that can possibly be set by bee fertilization, 0 -> 5 scale
                receiveNutrients(farmland, cap, which == 0 ? nut : 0, which == 1 ? nut : 0, which == 2 ? nut : 0);
            }
        }

        final int restore = bee.getAbility(BeeAbility.NATURE_RESTORATION);
        if (restore > 1)
        {
            if (level.random.nextInt(50 + 50 * (10 - restore)) == 0)
            {
                BlockPos above = pos.above();
                final boolean airAbove = level.getBlockState(above).isAir();
                if (airAbove && state.getBlock() == Blocks.WATER && state.getFluidState().isSource())
                {
                    FLHelpers.getRandomElement(BuiltInRegistries.BLOCK, FLTags.Blocks.BEE_RESTORATION_WATER_PLANTS, level.random).ifPresent(plant -> {
                        if (plant.defaultBlockState().canSurvive(level, pos))
                        {
                            level.setBlockAndUpdate(pos, plant.defaultBlockState());
                        }
                    });
                }
                else if (airAbove && block instanceof DirtBlock dirt)
                {
                    level.setBlockAndUpdate(pos, dirt.getGrass());
                }
                else if (state.isAir() && level.getBlockState(pos.below()).getBlock() instanceof ConnectedGrassBlock)
                {
                    FLHelpers.getRandomElement(BuiltInRegistries.BLOCK, FLTags.Blocks.BEE_RESTORATION_PLANTS, level.random).ifPresent(plant -> level.setBlockAndUpdate(pos, plant.defaultBlockState()));
                }
            }
        }
    }

    private void receiveNutrients(IFarmland farmland, float cap, float nitrogen, float phosphorous, float potassium)
    {
        float n = farmland.getNutrient(N);
        if (n < cap) farmland.setNutrient(N, Math.min(n + nitrogen, cap));
        float p = farmland.getNutrient(P);
        if (p < cap) farmland.setNutrient(P, Math.min(p + phosphorous, cap));
        float k = farmland.getNutrient(K);
        if (k < cap) farmland.setNutrient(K, Math.min(k + potassium, cap));
    }

    public void updateState()
    {
        assert level != null;
        final boolean bees = hasBees();
        final BlockState state = level.getBlockState(worldPosition);
        if (bees != state.getValue(WoodenBeehiveBlock.BEES))
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(WoodenBeehiveBlock.BEES, bees));
            markForSync();
        }
        boolean hasHoney = honey > 0;
        if (hasHoney != state.getValue(WoodenBeehiveBlock.HONEY))
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(WoodenBeehiveBlock.HONEY, hasHoney));
            markForSync();
        }
        if (linkedHive != null)
        {
            final BlockState linkState = level.getBlockState(linkedHive);
            final boolean isHive = linkState.getBlock() instanceof BaseBeehiveBlock;
            final boolean isWild = linkState.getBlock() instanceof WildBeehiveBlock && linkState.getValue(WildBeehiveBlock.BEES);
            if (!isWild && !isHive)
            {
                linkedHive = null;
                linkedHiveTick = 0L;
                markForSync();
            }
            else if (isWild)
            {
                if (linkedHiveTick > 0 && Calendars.SERVER.getTicks() - linkedHiveTick > ICalendar.CALENDAR_TICKS_IN_DAY)
                {
                    level.setBlockAndUpdate(linkedHive, linkState.setValue(WildBeehiveBlock.BEES, false));
                    linkedHive = null;
                    linkedHiveTick = 0L;

                    beeData = BeeComponent.initFreshAbilities(level.random);
                    markForSync();
                }
            }
        }
    }

    private boolean hasBees()
    {
        return beeData.hasQueen();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return stack.getItem() == FLItems.BEEHIVE_FRAME.get();
    }

    @Override
    public void onSlotTake(Player player, int slot, ItemStack stack)
    {
        assert level != null;
        if (BaseBeehiveBlock.shouldAnger(level, worldPosition))
        {
            BaseBeehiveBlock.attack(player);
        }
    }

    @Override
    public long getLastCalendarUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastCalendarUpdateTick(long tick)
    {
        lastPlayerTick = tick;
    }

    @Override
    public ModelData getModelData()
    {
        assert level != null;
        return InventoryBlockModel.InventoryModelData.of(level, this);
    }
}
