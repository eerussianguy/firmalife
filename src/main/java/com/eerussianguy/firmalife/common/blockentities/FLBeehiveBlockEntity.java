package com.eerussianguy.firmalife.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLBeehiveBlock;
import com.eerussianguy.firmalife.common.blocks.greenhouse.LargePlanterBlock;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeAbility;
import com.eerussianguy.firmalife.common.capabilities.bee.BeeCapability;
import com.eerussianguy.firmalife.common.capabilities.bee.IBee;
import com.eerussianguy.firmalife.common.container.BeehiveContainer;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.common.blockentities.TFCBlockEntities;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.blocks.soil.ConnectedGrassBlock;
import net.dries007.tfc.common.blocks.soil.DirtBlock;
import net.dries007.tfc.common.blocks.soil.FarmlandBlock;
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

        // guarding this as the server is the authority on what the block entity has for data
        if (!level.isClientSide && level.getGameTime() % 60 == 0)
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
        lastPlayerTick = lastAreaTick = Calendars.SERVER.getTicks();
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
        honey = nbt.getInt("honey");
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

    /**
     * Main method called periodically to perform bee actions
     */
    @SuppressWarnings("deprecation") // hasChunksAt
    private void updateTick()
    {
        assert level != null;
        final int minX = worldPosition.getX() - 5;
        final int maxX = worldPosition.getX() + 5;
        final int minZ = worldPosition.getZ() - 5;
        final int maxZ = worldPosition.getZ() + 5;
        final float temp = Climate.getTemperature(level, worldPosition);

        // collect bees that exist and have queens
        List<IBee> usableBees = new ArrayList<>();
        int breedTickChanceInverted = 0;
        int honeyChanceInverted = 0;
        for (int b = 0; b < SLOTS; b++)
        {
            final IBee bee = cachedBees[b];
            if (bee != null)
            {
                if (bee.hasQueen() && temp > BeeAbility.getMinTemperature(bee.getAbility(BeeAbility.HARDINESS)))
                {
                    usableBees.add(bee);
                    breedTickChanceInverted += 10 - bee.getAbility(BeeAbility.FERTILITY);
                    honeyChanceInverted += 10 - bee.getAbility(BeeAbility.PRODUCTION);
                }
            }
        }

        // perform area of effect actions
        final boolean empty = usableBees.isEmpty();
        int flowers = 0;
        if (level.hasChunksAt(minX, maxX, minZ, maxZ))
        {
            for (BlockPos pos : BlockPos.betweenClosed(minX, worldPosition.getY() - 5, minZ, maxX, worldPosition.getY() + 5, maxZ))
            {
                if (empty)
                {
                    flowers += tickPosition(pos, null);
                }
                else
                {
                    for (IBee bee : usableBees)
                    {
                        flowers += tickPosition(pos, bee);
                    }
                }
            }
        }

        // breed bees, or, if there's no bees living here, produce a new one
        if (empty)
        {
            breedTickChanceInverted = 20;
        }
        breedTickChanceInverted = Math.max(0, breedTickChanceInverted - flowers);
        if (flowers > MIN_FLOWERS && (breedTickChanceInverted == 0 || level.random.nextInt(breedTickChanceInverted) == 0))
        {
            IBee parent1 = null;
            IBee parent2 = null;
            IBee uninitializedBee = null;
            for (int i = 0; i < SLOTS; i++)
            {
                IBee bee = inventory.getStackInSlot(i).getCapability(BeeCapability.CAPABILITY).resolve().orElse(null);
                if (bee != null)
                {
                    if (bee.hasQueen())
                    {
                        if (parent1 == null)
                        {
                            parent1 = bee;
                        }
                        else if (parent2 == null)
                        {
                            parent2 = bee;
                        }
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
        honeyChanceInverted /= usableBees.size();
        if (flowers > MIN_FLOWERS && honeyChanceInverted > 0 && level.random.nextInt(honeyChanceInverted) == 0)
        {
            addHoney(usableBees.size());
        }
    }

    public void addHoney(int amount)
    {
        honey = Math.min(16, amount + honey);
        markForSync();
    }

    public int takeHoney(int amount)
    {
        final int take = Math.min(amount, honey);
        honey -= take;
        markForSync();
        return take;
    }

    private int tickPosition(BlockPos pos, @Nullable IBee bee)
    {
        assert level != null;
        BlockState state = level.getBlockState(pos);

        boolean beePlant = Helpers.isBlock(state, FLTags.Blocks.BEE_PLANTS);
        if (bee != null)
        {
            final Block block = state.getBlock();
            final boolean soil = block instanceof FarmlandBlock;
            final boolean planter = block instanceof LargePlanterBlock;

            if (planter || soil)
            {
                float cropAffinity = bee.getAbility(BeeAbility.CROP_AFFINITY); // 1 -> 10 scale
                float nitrogen = level.random.nextFloat() * cropAffinity * 0.1f; // 0.1 -> 1 scale
                float potassium = level.random.nextFloat() * cropAffinity * 0.1f;
                float phosphorous = level.random.nextFloat() * cropAffinity * 0.1f;
                float cap = (cropAffinity - 1) * 0.5f; // max that can possibly be set by bee fertilization, 0 -> 4.5 scale

                if (soil)
                {
                    level.getBlockEntity(pos, TFCBlockEntities.FARMLAND.get()).ifPresent(farmland -> receiveNutrients(farmland::getNutrient, farmland::setNutrient, cap, nitrogen, phosphorous, potassium));
                }
                else
                {
                    if (level.getBlockEntity(pos) instanceof LargePlanterBlockEntity planterEntity)
                    {
                        receiveNutrients(planterEntity::getNutrient, planterEntity::setNutrient, cap, nitrogen, phosphorous, potassium);
                    }
                }
            }

            final int restore = bee.getAbility(BeeAbility.NATURE_RESTORATION);
            if (restore > 1)
            {
                if (level.random.nextInt(50 + 50 * (10 - restore)) == 0)
                {
                    BlockPos above = pos.above();
                    boolean airAbove = level.getBlockState(above).isAir();
                    if (airAbove && Helpers.isFluid(state.getFluidState(), Fluids.WATER))
                    {
                        Helpers.getRandomElement(ForgeRegistries.BLOCKS, FLTags.Blocks.BEE_RESTORATION_WATER_PLANTS, level.random).ifPresent(plant -> level.setBlockAndUpdate(pos, plant.defaultBlockState()));
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

        return beePlant ? 1 : 0;
    }

    private void receiveNutrients(Function<FarmlandBlockEntity.NutrientType, Float> getter, BiConsumer<FarmlandBlockEntity.NutrientType, Float> setter, float cap, float nitrogen, float phosphorous, float potassium)
    {
        float n = getter.apply(N); if (n < cap) setter.accept(N, Math.min(n + nitrogen, cap));
        float p = getter.apply(N); if (p < cap) setter.accept(P, Math.min(p + phosphorous, cap));
        float k = getter.apply(K); if (k < cap) setter.accept(K, Math.min(k + potassium, cap));
    }

    public void updateState()
    {
        assert level != null;
        boolean bees = hasBees();
        BlockState state = level.getBlockState(worldPosition);
        if (bees != state.getValue(FLBeehiveBlock.BEES))
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(FLBeehiveBlock.BEES, bees));
        }
        boolean hasHoney = honey > 0;
        if (hasHoney != state.getValue(FLBeehiveBlock.HONEY))
        {
            level.setBlockAndUpdate(worldPosition, state.setValue(FLBeehiveBlock.HONEY, hasHoney));
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
        ItemStack stack = inventory.getStackInSlot(slot);
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
