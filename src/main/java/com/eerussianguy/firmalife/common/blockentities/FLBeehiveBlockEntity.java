package com.eerussianguy.firmalife.common.blockentities;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBeehiveBlock;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeAbility;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeCapability;
import com.eerussianguy.firmalife.common.capabilities.bee.IBee;
import com.eerussianguy.firmalife.common.container.BeehiveContainer;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.IFarmland;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.DirtBlock;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendar;
import net.dries007.tfc.util.calendar.ICalendarTickable;
import net.dries007.tfc.util.climate.Climate;
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
    }

    public static final int MIN_FLOWERS = 10;
    public static final int UPDATE_INTERVAL = ICalendar.TICKS_IN_DAY;
    public static final int SLOTS = 4;
    private static final Component NAME = FLHelpers.blockEntityName("beehive");
    private static final FarmlandBlockEntity.NutrientType N = FarmlandBlockEntity.NutrientType.NITROGEN;
    private static final FarmlandBlockEntity.NutrientType P = FarmlandBlockEntity.NutrientType.PHOSPHOROUS;
    private static final FarmlandBlockEntity.NutrientType K = FarmlandBlockEntity.NutrientType.POTASSIUM;

    private final IBee[] cachedBees;
    private long lastPlayerTick, lastAreaTick;
    private int honey;

    public FLBeehiveBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.BEEHIVE.get(), pos, state, defaultInventory(SLOTS), NAME);
        lastPlayerTick = Integer.MIN_VALUE;
        lastAreaTick = Calendars.SERVER.getTicks();
        cachedBees = new IBee[] {null, null, null, null};
        honey = 0;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putLong("lastTick", lastPlayerTick);
        nbt.putLong("lastAreaTick", lastAreaTick);
        nbt.putInt("honey", honey);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        updateCache();
        lastPlayerTick = nbt.getLong("lastTick");
        lastAreaTick = nbt.getLong("lastAreaTick");
        honey = Math.min(nbt.getInt("honey"), getMaxHoney());
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
    }

    private void updateCache()
    {
        for (int i = 0; i < SLOTS; i++)
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
        final float temp = Climate.getTemperature(level, worldPosition);
        // collect bees that exist and have queens
        final List<IBee> usableBees = Arrays.stream(cachedBees).filter(bee -> bee != null && bee.hasQueen() && temp > BeeAbility.getMinTemperature(bee.getAbility(BeeAbility.HARDINESS))).collect(Collectors.toList());
        // perform area of effect actions
        final int flowers = getFlowers(usableBees, true);

        final int breedTickChanceInverted = getBreedTickChanceInverted(usableBees, flowers);
        if (flowers > MIN_FLOWERS && (breedTickChanceInverted == 0 || level.random.nextInt(breedTickChanceInverted) == 0))
        {
            IBee parent1 = null;
            IBee parent2 = null;
            IBee uninitializedBee = null;
            for (int i = 0; i < SLOTS; i++)
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
                if (Helpers.isBlock(state, FLTags.Blocks.BEE_PLANTS))
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
                if (cropAffinity > 1)
                {
                    final float nitrogen = level.random.nextFloat() * cropAffinity * 0.02f; // 0 -> 1 scale
                    final float potassium = level.random.nextFloat() * cropAffinity * 0.02f;
                    final float phosphorous = level.random.nextFloat() * cropAffinity * 0.02f;
                    final float cap = (cropAffinity / 10) * 0.5f; // max that can possibly be set by bee fertilization, 0 -> 5 scale
                    receiveNutrients(farmland, cap, nitrogen, phosphorous, potassium);
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
        float p = farmland.getNutrient(N); if (p < cap) farmland.setNutrient(P, Math.min(p + phosphorous, cap));
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
        for (int i = 0; i < SLOTS; i++)
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
        return stack.getCapability(BeeCapability.CAPABILITY).isPresent();
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
    public long getLastUpdateTick()
    {
        return lastPlayerTick;
    }

    @Override
    public void setLastUpdateTick(long tick)
    {
        lastPlayerTick = tick;
    }
}
