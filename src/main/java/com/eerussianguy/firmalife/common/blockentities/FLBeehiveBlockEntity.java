package com.eerussianguy.firmalife.common.blockentities;

import java.util.HashSet;
import java.util.Set;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.client.model.InventoryBlockModel;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.bee.BaseBeehiveBlock;
import com.eerussianguy.firmalife.common.blocks.bee.WildBeehiveBlock;
import com.eerussianguy.firmalife.common.blocks.bee.WoodenBeehiveBlock;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeAbility;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeComponent;
import com.eerussianguy.firmalife.common.capabilities.bee.ParasiticInfection;
import com.eerussianguy.firmalife.common.entities.FLBee;
import com.eerussianguy.firmalife.common.entities.FLEntities;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.common.misc.FLPOIs;
import com.eerussianguy.firmalife.config.FLConfig;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.blocks.TFCBlockStateProperties;
import net.dries007.tfc.common.blocks.plant.ITallPlant;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.DirtBlock;
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
    @Nullable protected BlockPos linkedHive = null;
    protected long linkedHiveTick = 0L;
    protected int lastWarmEnough = 0;

    protected BeeComponent beeData = BeeComponent.DEFAULT;

    public FLBeehiveBlockEntity(BlockPos pos, BlockState state, BlockEntityType<?> type, int slots)
    {
        super(type, pos, state, defaultInventory(slots), FirmaLife.MOD_ID);
        lastPlayerTick = Integer.MIN_VALUE;
        lastAreaTick = Calendars.SERVER.getTicks();
        beesInWorld = 0;
    }

    public @Nullable BlockPos getLinkedHive()
    {
        return linkedHive;
    }

    public void linkSwarmFrom(BlockPos origin)
    {
        if (linkedHive != null)
            return;
        if (origin == worldPosition)
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
        nbt.putInt("beesInWorld", beesInWorld);
        nbt.putInt("lastWarmEnough", lastWarmEnough);
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
        beesInWorld = nbt.getInt("beesInWorld");
        lastWarmEnough = nbt.getInt("lastWarmEnough");
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
        assert level != null;
        final ICalendar calendar = Calendars.SERVER;
        final boolean occluded = isOccluded();
        final Set<Flower> flowers = countFlowers();
        final long now = calendar.getTicks();
        //handle update interval
        if (now > (lastAreaTick + UPDATE_INTERVAL))
        {
            while (lastAreaTick < now)
            {
                float temperature = Climate.getInstantTemperature(level, worldPosition, calendar, calendar.getCalendarTickFromOffset(lastAreaTick - calendar.getTicks()));
                updateTick(temperature, occluded, flowers);
                lastAreaTick += UPDATE_INTERVAL;
            }
            trySwarm(occluded); // happens at the end of an update, only once.
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

    public void setBeeData(BeeComponent beeData)
    {
        this.beeData = beeData;
    }

    public BeeComponent getBee()
    {
        return beeData;
    }

    /**
     * Main method called periodically to perform bee actions
     */
    public void updateTick(float temperature, boolean occluded, Set<Flower> flowers)
    {
        assert level != null;
        beeData = beeData.getOlder();

        if (beeData.hasQueen())
        {
            flowers.forEach(f -> tickPosition(f.pos, f.state));
            final boolean warmNow = isWarmEnough(temperature) && getAvailableFrames() > 0;
            final int honeyChanceInverted = getHoneyTickChanceInverted(beeData, flowers.size());
            if (!occluded && warmNow && (honeyChanceInverted == 0 || level.random.nextInt(honeyChanceInverted) == 0))
            {
                addHoney(1);
            }
            if (warmNow || lastWarmEnough == 0)
            {
                lastWarmEnough = beeData.age(); // if too cold or not initialized
            }
            else if (beeData.age() - lastWarmEnough > FLConfig.SERVER.beeUseHoneyDays.get())
            {
                final int honey = getHoney();
                if (honey == 0)
                {
                    if (level.random.nextInt(8) == 0)
                        beeData = BeeComponent.DEFAULT; // rip
                }
                else
                {
                    // if too cold, and it's been long enough, eat a honey.
                    takeHoney(1);
                    lastWarmEnough = beeData.age();
                }

            }
            checkInfections(temperature, occluded);
        }
        else
        {
            lastWarmEnough = 0;
        }

        markForSync();
    }

    public boolean isOccluded()
    {
        assert level != null;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (Direction d : Helpers.DIRECTIONS)
        {
            if (d != Direction.DOWN && level.getBlockState(cursor.setWithOffset(worldPosition, d)).isCollisionShapeFullBlock(level, worldPosition))
            {
                return true;
            }
        }
        return false;
    }

    public boolean canSwarm(boolean occluded)
    {
        if (linkedHive == null && getBee().hasQueen() && !getBee().hasGeneticDisease())
        {
            if (occluded) // if the hive is uncomfortable
                return true;
            final int honey = getHoney();
            final boolean warm = isWarmEnough();
            if (honey == 4 && beeData.age() > 24 - beeData.getAbility(BeeAbility.FERTILITY) && warm) // the case in which the bees are full and healthy and old enough
            {
                return true;
            }
            if (honey == 0 && beeData.age() > (beeData.getAbility(BeeAbility.FERTILITY) > 8 ? 1 : 4) && !warm) // the hive has no honey, and is unable to produce more
            {
                return true;
            }
        }
        return false;
    }

    public void trySwarm(boolean occluded)
    {
        assert level != null;
        if (canSwarm(occluded) && !level.isClientSide)
        {
            final boolean varroa = beeData.parasiticInfection() == ParasiticInfection.VARROA;
            final BlockPos hivePos = FLHelpers.getPoint(level, worldPosition, varroa ? 10 : 5, FLPOIs.BEEHIVES, (level, pos) -> {
                return level.getBlockEntity(pos) instanceof FLBeehiveBlockEntity hive && (!hive.getBee().hasQueen() || varroa) && !hive.isSkep();
            });
            if (hivePos != null && level.getBlockEntity(hivePos) instanceof FLBeehiveBlockEntity hive)
            {
                hive.linkSwarmFrom(worldPosition);
            }
        }
    }

    public void checkInfections(float temp, boolean isOccluded)
    {
        if (beeData.hasParasiticInfection())
            return;
        assert level != null;
        final RandomSource random = level.random;

        final int resistance = beeData.getAbility(BeeAbility.INFECTION_RESISTANCE);
        if (random.nextFloat() * 10f < resistance)
            return;
        final float rain = Climate.get(level).getInstantRainfall(level, worldPosition);
        if (rain > 470 && random.nextInt(80) == 0)
        {
            beeData = BeeComponent.withDiseases(beeData, beeData.geneticDisease(), temp > 16 ? ParasiticInfection.STONEBROOD : ParasiticInfection.CHALKBROOD);
        }
        else if ((rain < 50 || temp < -18) && random.nextInt(400) == 0)
        {
            beeData = BeeComponent.withDiseases(beeData, beeData.geneticDisease(), temp > 16 ? ParasiticInfection.FOULBROOD : ParasiticInfection.WAX_MOTHS);
        }
        else if (temp > 27 && random.nextInt(80) == 0)
        {
            beeData = BeeComponent.withDiseases(beeData, beeData.geneticDisease(), ParasiticInfection.HIVE_BEETLES);
        }
        else if (isOccluded && random.nextInt(10) == 0)
        {
            beeData = BeeComponent.withDiseases(beeData, beeData.geneticDisease(), ParasiticInfection.VARROA);
        }
    }

    public void takeHoney(int honey)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (honey > 0 && Helpers.isItem(inventory.getStackInSlot(i).getItem(), FLTags.Items.FILLED_BEEHIVE_FRAMES))
            {
                inventory.setStackInSlot(i, FLItems.BEEHIVE_FRAME.get().getDefaultInstance());
                honey--;
            }
        }
        requestModelDataUpdate();
    }

    public void addHoney(int honey)
    {
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (inventory.getStackInSlot(i).getItem() == FLItems.BEEHIVE_FRAME.get())
            {
                inventory.setStackInSlot(i, FLItems.FILLED_BEEHIVE_FRAME.get().getDefaultInstance());
                honey--;
                if (honey == 0)
                {
                    break;
                }
            }
        }
        requestModelDataUpdate();
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
        return isWarmEnough(Climate.getInstantTemperature(level, worldPosition));
    }

    public boolean isWarmEnough(float temperature)
    {
        assert level != null;
        return temperature > getMinTemperature();
    }

    public float getMinTemperature()
    {
        boolean insulation = false;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            if (inventory.getStackInSlot(i).getItem() == FLItems.INSULATING_BEEHIVE_FRAME.get())
            {
                insulation = true;
                break;
            }
        }
        return BeeAbility.getMinTemperature(beeData.getAbility(BeeAbility.HARDINESS)) - (insulation ? 2 : 0);
    }

    public record Flower(BlockPos pos, BlockState state) {}

    @SuppressWarnings("deprecation")
    public Set<Flower> countFlowers()
    {
        final Set<Flower> set = new HashSet<>();
        assert level != null;
        final BlockPos min = worldPosition.offset(-5, -5, -5);
        final BlockPos max = worldPosition.offset(5, 5, 5);
        if (level.hasChunksAt(min, max))
        {
            for (BlockPos pos : BlockPos.betweenClosed(min, max))
            {
                final BlockState state = level.getBlockState(pos);
                if (isFlower(state))
                {
                    set.add(new Flower(pos.immutable(), state));
                }
            }
        }
        return set;
    }


    private boolean isFlower(BlockState state)
    {
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF) && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER)
            return false;
        if (state.hasProperty(TFCBlockStateProperties.TALL_PLANT_PART) && state.getValue(TFCBlockStateProperties.TALL_PLANT_PART) == ITallPlant.Part.UPPER)
            return false;
        return Helpers.isBlock(state, BlockTags.FLOWERS);
    }

    public int getHoneyTickChanceInverted(BeeComponent bee, int flowers)
    {
        if (flowers < MIN_FLOWERS)
            return Integer.MAX_VALUE;
        if (bee.hasParasiticInfection())
            return Integer.MAX_VALUE;
        final float geneticChance = (10 - bee.getAbility(BeeAbility.PRODUCTION)) / 10f * 0.7f;
        final float flowersChance = Mth.clamp(Mth.sqrt(flowers / 60f), 0f, 0.3f);
        return Mth.ceil(4f / (geneticChance + flowersChance));
    }

    public int getHoney()
    {
        int honey = 0;
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            if (Helpers.isItem(stack, FLTags.Items.FILLED_BEEHIVE_FRAMES))
                honey += 1;
        }
        return honey;
    }

    public int getAvailableFrames()
    {
        int frames = 0;
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            // this is subtly a different check than just the 'frames' tag.
            if (Helpers.isItem(stack, FLTags.Items.FILLED_BEEHIVE_FRAMES) || stack.getItem() == FLItems.BEEHIVE_FRAME.get())
                frames += 1;
        }
        return frames;
    }

    private void tickPosition(BlockPos pos, BlockState state)
    {
        assert level != null;
        final Block block = state.getBlock();

        if (level.getBlockEntity(pos) instanceof IFarmland farmland)
        {
            final float cropAffinity = (float) beeData.getAbility(BeeAbility.CROP_AFFINITY); // 0 -> 10 scale
            if (cropAffinity >= 1 && level.random.nextInt(50) == 0)
            {
                final int which = level.random.nextInt(3); // 0, 1, 2
                final float nut = level.random.nextFloat() * cropAffinity * 0.01f;
                final float cap = (cropAffinity / 10) * 0.5f; // max that can possibly be set by bee fertilization, 0 -> 5 scale
                receiveNutrients(farmland, cap, which == 0 ? nut : 0, which == 1 ? nut : 0, which == 2 ? nut : 0);
            }
        }

        final int restore = beeData.getAbility(BeeAbility.NATURE_RESTORATION);
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
        final boolean hasHoney = getHoney() > 0;
        if (hasHoney != state.getValue(WoodenBeehiveBlock.HONEY))
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(WoodenBeehiveBlock.HONEY, hasHoney));
            markForSync();
        }
        if (linkedHive != null && level.isAreaLoaded(linkedHive, 1))
        {
            final BlockState linkState = level.getBlockState(linkedHive);
            final boolean isHive = linkState.getBlock() instanceof BaseBeehiveBlock;
            final boolean isWild = linkState.getBlock() instanceof WildBeehiveBlock && linkState.getValue(WildBeehiveBlock.BEES);
            // the linked hive is neither wild or a beehive block (eg. it is probably air/destroyed)
            if (!isWild && !isHive)
            {
                linkedHive = null;
                linkedHiveTick = 0L;
                markForSync();
            }
            else if (isWild) // if it is a wild beehive, create a new bee here
            {
                if (linkedHiveTick > 0 && Calendars.SERVER.getTicks() - linkedHiveTick > ICalendar.CALENDAR_TICKS_IN_DAY)
                {
                    level.setBlockAndUpdate(linkedHive, linkState.setValue(WildBeehiveBlock.BEES, false));
                    linkedHive = null;
                    linkedHiveTick = 0L;

                    Helpers.playSound(level, worldPosition, SoundEvents.BEEHIVE_EXIT);
                    beeData = BeeComponent.getWildBee(level, worldPosition);
                    markForSync();
                }
            }
            else // if it is a man-made hive, transfer a bee over.
            {
                if (linkedHiveTick > 0 && Calendars.SERVER.getTicks() - linkedHiveTick > ICalendar.CALENDAR_TICKS_IN_DAY)
                {
                    if (level.getBlockEntity(linkedHive) instanceof FLBeehiveBlockEntity hive &&
                        (!beeData.hasQueen() || hive.beeData.parasiticInfection() == ParasiticInfection.VARROA)
                    )
                    {
                        Helpers.playSound(level, worldPosition, SoundEvents.BEEHIVE_EXIT);
                        if (hive.isSplitting())
                        {
                            beeData = hive.isSkep() ? hive.beeData : hive.beeData.mutate(level.random);
                            hive.beeData = hive.beeData.withAge(0); // reset age
                            if (level.random.nextFloat() < 0.2f)
                                hive.beeData = hive.beeData.withTrait(BeeAbility.CALMNESS, 1);
                            hive.markForSync();
                        }
                        else // if we are not splitting, wipe the bee data.
                        {
                            beeData = hive.beeData;
                            hive.beeData = BeeComponent.DEFAULT;
                            hive.markForSync();
                        }
                    }
                    linkedHive = null;
                    linkedHiveTick = 0L;
                    markForSync();
                }
            }
        }
    }

    public boolean isSplitting()
    {
        return isWarmEnough();
    }

    public boolean isSkep()
    {
        return false;
    }

    private boolean hasBees()
    {
        return beeData.hasQueen();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, FLTags.Items.BEEHIVE_FRAMES);
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
