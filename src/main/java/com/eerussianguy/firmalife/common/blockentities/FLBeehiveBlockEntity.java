package com.eerussianguy.firmalife.common.blockentities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import com.eerussianguy.firmalife.common.entities.FLBee;
import com.eerussianguy.firmalife.common.entities.FLEntities;
import com.eerussianguy.firmalife.common.items.FLItems;
import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBeehiveBlock;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeAbility;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeCapability;
import com.eerussianguy.firmalife.common.capabilities.bee.IBee;
import com.eerussianguy.firmalife.common.container.BeehiveContainer;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.blocks.plant.PlantBlock;
import net.dries007.tfc.common.blocks.plant.ShortGrassBlock;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.DirtBlock;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.climate.Climate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        if (hive.needsSlotUpdate)
        {
            if (hive.inventory.getStackInSlot(SLOT_JAR_OUT).isEmpty())
            {
                final ItemStack current = hive.inventory.getStackInSlot(SLOT_JAR_IN);
                if (Helpers.isItem(current, TFCItems.EMPTY_JAR.get()) && hive.takeHoney(1) > 0)
                {
                    hive.inventory.setStackInSlot(SLOT_JAR_IN, ItemStack.EMPTY);
                    hive.inventory.setStackInSlot(SLOT_JAR_OUT, FLItems.HONEY_JAR.get().getDefaultInstance());
                    Helpers.playSound(level, pos, SoundEvents.BOTTLE_FILL);
                }
            }
        }
    }

    public static final int MIN_FLOWERS = 10;
    public static final int UPDATE_INTERVAL = ICalendar.TICKS_IN_DAY;
    public static final int ENTITY_HANDLING_INTERVAL = 1000;
    public static final int FRAME_SLOTS = 4;
    public static final int TOTAL_SLOTS = 6;
    public static final int SLOT_JAR_IN = 4;
    public static final int SLOT_JAR_OUT = 5;

    private static final Component NAME = FLHelpers.blockEntityName("beehive");
    private static final FarmlandBlockEntity.NutrientType N = FarmlandBlockEntity.NutrientType.NITROGEN;
    private static final FarmlandBlockEntity.NutrientType P = FarmlandBlockEntity.NutrientType.PHOSPHOROUS;
    private static final FarmlandBlockEntity.NutrientType K = FarmlandBlockEntity.NutrientType.POTASSIUM;

    private final IBee[] cachedBees;

    private int beesInWorld;
    private long lastPlayerTick, lastAreaTick;
    private int honey;
    private boolean needsSlotUpdate = false;

    public FLBeehiveBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.BEEHIVE.get(), pos, state, be -> new FixedISH(be, TOTAL_SLOTS), NAME);
        lastPlayerTick = Integer.MIN_VALUE;
        lastAreaTick = Calendars.SERVER.getTicks();
        cachedBees = new IBee[] {null, null, null, null};
        honey = 0;
        beesInWorld = 0;

        sidedInventory
            .on(new PartialItemHandler(inventory).insert(4), Direction.Plane.HORIZONTAL)
            .on(new PartialItemHandler(inventory).extract(5), Direction.DOWN);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putLong("lastTick", lastPlayerTick);
        nbt.putLong("lastAreaTick", lastAreaTick);
        nbt.putInt("honey", honey);
        nbt.putInt("beesInWorld", beesInWorld);
        nbt.putBoolean("updatedSize", true);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        updateCache();
        lastPlayerTick = nbt.getLong("lastTick");
        lastAreaTick = nbt.getLong("lastAreaTick");
        honey = Math.min(nbt.getInt("honey"), getMaxHoney());
        beesInWorld = nbt.getInt("beesInWorld");
        needsSlotUpdate = true;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return BeehiveContainer.create(this, inv, windowID);
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
        updateCache();
        needsSlotUpdate = true;
    }

    private void updateCache()
    {
        for (int i = 0; i < FRAME_SLOTS; i++)
        {
            cachedBees[i] = getBee(i);
        }
    }

    public IBee[] getCachedBees()
    {
        if (level != null && level.isClientSide) updateCache();
        return cachedBees;
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

        final float temp = Climate.getTemperature(level, worldPosition);
        // collect bees that exist and have queens
        final List<IBee> usableBees = getUsableBees(temp);
        // perform area of effect actions
        final int flowers = getFlowers(usableBees, true);

        final int breedTickChanceInverted = getBreedTickChanceInverted(usableBees, flowers);
        if (flowers > MIN_FLOWERS && (breedTickChanceInverted == 0 || level.random.nextInt(breedTickChanceInverted) == 0))
        {
            IBee parent1 = null;
            IBee parent2 = null;
            IBee uninitializedBee = null;
            for (int i = 0; i < FRAME_SLOTS; i++)
            {
                final IBee bee = inventory.getStackInSlot(i).getCapability(BeeCapability.CAPABILITY).resolve().orElse(null);
                if (bee != null)
                {
                    if (bee.hasQueen())
                    {
                        if (parent1 == null) parent1 = bee;
                        else if (parent2 == null) parent2 = bee;
                    }
                    else if (uninitializedBee == null)
                    {
                        uninitializedBee = bee;
                    }
                }
            }
            if (uninitializedBee != null)
            {
                if (parent2 == null) // if we have one or no parents
                {
                    uninitializedBee.initFreshAbilities(level.random);
                }
                else if (parent1.hasQueen() && parent2.hasQueen())
                {
                    uninitializedBee.setAbilitiesFromParents(parent1, parent2, level.random);
                }
            }
        }
        final int honeyChanceInverted = getHoneyTickChanceInverted(usableBees, flowers);
        if (flowers > MIN_FLOWERS && (honeyChanceInverted == 0 || level.random.nextInt(honeyChanceInverted) == 0))
        {
            usableBees.removeIf(IBee::hasGeneticDisease);
            addHoney(usableBees.size());
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
            final float temp = Climate.getTemperature(level, worldPosition);

            // collect bees that exist and have queens
            final List<IBee> usableBees = getUsableBees(temp);

            final Direction direction = getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
            final BlockPos posInFront = worldPosition.relative(direction);

            if (level.getBlockState(posInFront).getCollisionShape(level, posInFront).isEmpty() && !usableBees.isEmpty() && beesInWorld == 0)
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

    @NotNull
    public List<IBee> getUsableBees(float temp)
    {
        return Arrays.stream(cachedBees).filter(bee -> bee != null && bee.hasQueen() && temp > BeeAbility.getMinTemperature(bee.getAbility(BeeAbility.HARDINESS))).collect(Collectors.toList());
    }

    @SuppressWarnings("deprecation")
    public int getFlowers(List<IBee> bees, boolean tick)
    {
        assert level != null;
        int flowers = 0;
        final BlockPos min = worldPosition.offset(-5, -5, -5);
        final BlockPos max = worldPosition.offset(5, 5, 5);
        final boolean empty = bees.isEmpty();
        if (level.hasChunksAt(min, max))
        {
            for (BlockPos pos : BlockPos.betweenClosed(min, max))
            {
                final BlockState state = level.getBlockState(pos);
                if (isFlower(state))
                {
                    flowers += 1;
                }
                if (tick)
                {
                    if (empty)
                    {
                        tickPosition(pos, state, null);
                    }
                    else
                    {
                        for (IBee bee : bees)
                        {
                            tickPosition(pos, state, bee);
                        }
                    }
                }
            }
        }
        return flowers;
    }

    private boolean isFlower(BlockState state)
    {
        return Helpers.isBlock(state, BlockTags.FLOWERS) || (state.getBlock() instanceof PlantBlock && !(state.getBlock() instanceof ShortGrassBlock)) || (state.getBlock() instanceof LargePlanterBlock && state.getValue(LargePlanterBlock.WATERED));
    }

    public int getHoneyTickChanceInverted(List<IBee> bees, int flowers)
    {
        int chance = 30;
        for (IBee bee : bees)
        {
            if (bee.hasQueen())
            {
                chance += 10 - bee.getAbility(BeeAbility.PRODUCTION);
            }
        }
        if (!bees.isEmpty())
        {
            chance /= bees.size();
        }
        return Math.max(0, chance - Mth.ceil((0.2 * Math.min(flowers, 60))));
    }

    public int getBreedTickChanceInverted(List<IBee> bees, int flowers)
    {
        int chance = 0;
        for (IBee bee : bees)
        {
            if (bee.hasQueen())
            {
                chance += 10 - bee.getAbility(BeeAbility.FERTILITY);
            }
        }
        // no bees, have to give some chance
        if (bees.isEmpty())
        {
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

    private void tickPosition(BlockPos pos, BlockState state, @Nullable IBee bee)
    {
        assert level != null;
        if (bee != null)
        {
            final Block block = state.getBlock();

            if (level.getBlockEntity(pos) instanceof IFarmland farmland)
            {
                final float cropAffinity = (float) bee.getAbility(BeeAbility.CROP_AFFINITY); // 0 -> 10 scale
                if (cropAffinity >= 1 && level.random.nextInt(FLConfig.SERVER.cropAffinityChance.get()) == 0)
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
                        Helpers.getRandomElement(ForgeRegistries.BLOCKS, FLTags.Blocks.BEE_RESTORATION_WATER_PLANTS, level.random).ifPresent(plant -> {
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
                        Helpers.getRandomElement(ForgeRegistries.BLOCKS, FLTags.Blocks.BEE_RESTORATION_PLANTS, level.random).ifPresent(plant -> level.setBlockAndUpdate(pos, plant.defaultBlockState()));
                    }
                }
            }

        }
    }

    private void receiveNutrients(IFarmland farmland, float cap, float nitrogen, float phosphorous, float potassium)
    {
        float n = farmland.getNutrient(N); if (n < cap) farmland.setNutrient(N, Math.min(n + nitrogen, cap));
        float p = farmland.getNutrient(P); if (p < cap) farmland.setNutrient(P, Math.min(p + phosphorous, cap));
        float k = farmland.getNutrient(K); if (k < cap) farmland.setNutrient(K, Math.min(k + potassium, cap));
    }

    public void updateState()
    {
        assert level != null;
        final boolean bees = hasBees();
        final BlockState state = level.getBlockState(worldPosition);
        if (bees != state.getValue(FLBeehiveBlock.BEES))
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(FLBeehiveBlock.BEES, bees));
            markForSync();
        }
        boolean hasHoney = honey > 0;
        if (hasHoney != state.getValue(FLBeehiveBlock.HONEY))
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(FLBeehiveBlock.HONEY, hasHoney));
            markForSync();
        }
    }

    private boolean hasBees()
    {
        for (int i = 0; i < FRAME_SLOTS; i++)
        {
            if (cachedBees[i] != null && cachedBees[i].hasQueen())
            {
                return true;
            }
        }
        return false;
    }

    @Nullable
    private IBee getBee(int slot)
    {
        final ItemStack stack = inventory.getStackInSlot(slot);
        if (!stack.isEmpty())
        {
            var opt = stack.getCapability(BeeCapability.CAPABILITY).resolve();
            if (opt.isPresent())
            {
                return opt.get();
            }
        }
        return null;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot < FRAME_SLOTS)
        {
            return stack.getCapability(BeeCapability.CAPABILITY).isPresent();
        }
        if (slot == SLOT_JAR_IN)
        {
            return Helpers.isItem(stack, TFCItems.EMPTY_JAR.get());
        }
        return false;
    }

    @Override
    public void onSlotTake(Player player, int slot, ItemStack stack)
    {
        assert level != null;
        if (FLBeehiveBlock.shouldAnger(level, worldPosition))
        {
            FLBeehiveBlock.attack(player);
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

    public static class FixedISH extends InventoryItemHandler
    {
        public FixedISH(InventoryBlockEntity<ItemStackHandler> be, int slots)
        {
            super(be, slots);
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            setSize(stacks.size());
            ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < tagList.size(); i++)
            {
                CompoundTag itemTags = tagList.getCompound(i);
                int slot = itemTags.getInt("Slot");

                if (slot >= 0 && slot < stacks.size())
                {
                    stacks.set(slot, ItemStack.of(itemTags));
                }
            }
            onLoad();
        }
    }
}
