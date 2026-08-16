package com.eerussianguy.firmalife.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.FLStateProperties;
import com.eerussianguy.firmalife.common.blocks.KegCoreBlock;
import com.eerussianguy.firmalife.common.container.KegContainer;
import com.eerussianguy.firmalife.common.recipes.KegRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.particle.FluidParticleOption;
import net.dries007.tfc.client.particle.TFCParticles;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.BarrelInventoryCallback;
import net.dries007.tfc.common.blockentities.IRecipeTimer;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.DelegateFluidHandler;
import net.dries007.tfc.common.capabilities.DelegateItemHandler;
import net.dries007.tfc.common.capabilities.FluidTankCallback;
import net.dries007.tfc.common.capabilities.InventoryFluidTank;
import net.dries007.tfc.common.capabilities.InventoryItemHandler;
import net.dries007.tfc.common.capabilities.PartialFluidHandler;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.capabilities.SidedHandler;
import net.dries007.tfc.common.component.CachedMut;
import net.dries007.tfc.common.component.size.IItemSize;
import net.dries007.tfc.common.component.size.ItemSizeManager;
import net.dries007.tfc.common.component.size.Size;
import net.dries007.tfc.common.component.size.Weight;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.recipes.BarrelRecipe;
import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.common.recipes.SealedBarrelRecipe;
import net.dries007.tfc.common.recipes.input.BarrelInventory;
import net.dries007.tfc.common.recipes.input.NonEmptyInput;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.CalendarTransaction;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.ICalendarTickable;

public class KegBlockEntity extends TickableInventoryBlockEntity<KegBlockEntity.KegInventory> implements BarrelInventoryCallback, IRecipeTimer, ICalendarTickable
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, KegBlockEntity barrel)
    {
        barrel.getRecipe(); // Cache the recipe, so any further operations are done on a loaded recipe cache
        barrel.checkForLastTickSync();
        barrel.checkForCalendarUpdate();

        // Fill / drain from the fluid IO slots every 5 ticks
        if (level.getGameTime() % 5 == 0)
        {
            barrel.updateFluidIOSlots();
        }

        // If the barrel contains excess stacks, try and move them back into the inventory. Anything that doesn't fit
        // stays in the excess - note that the excess must be *replaced*, not appended to, as the stacks we just
        // inserted have already been consumed.
        final List<ItemStack> excess = barrel.inventory.excess;
        if (!excess.isEmpty())
        {
            final List<ItemStack> leftover = new ArrayList<>();
            barrel.inventory.whileMutable(() -> {
                for (ItemStack stack : excess)
                {
                    final ItemStack left = Helpers.insertSlots(barrel.inventory, stack, SLOT_INPUT_START, SLOT_INPUT_END + 1);
                    if (!left.isEmpty())
                        leftover.add(left);
                }
            });
            excess.clear();
            excess.addAll(leftover);
            barrel.markForSync();
        }

        final SealedBarrelRecipe recipe = barrel.getRecipe();
        final boolean sealed = state.getValue(KegCoreBlock.SEALED);
        final Direction facing = state.getValue(KegCoreBlock.FACING);
        if (recipe != null && sealed)
        {
            final int durationSealed = (int) (Calendars.SERVER.getTicks() - barrel.recipeTick);
            if (!recipe.isInfinite() && durationSealed > recipe.getDuration())
            {
                if (KegRecipe.matches(recipe, barrel.inventory))
                {
                    // Recipe completed, so fill outputs
                    KegRecipe.assemble(recipe, barrel.inventory);
                    Helpers.playSound(level, barrel.getBlockPos(), recipe.getCompleteSound());
                }

                // In both cases, update the recipe and sync
                barrel.updateRecipe();
                barrel.markForSync();

                // If a new recipe exists, then apply onSeal effects. This is for cases such as pickling -> vinegar preservation
                final @Nullable SealedBarrelRecipe nextRecipe = barrel.getRecipe();
                if (nextRecipe != null)
                {
                    KegRecipe.onSealed(nextRecipe, barrel.inventory); // We're in a sequential recipe, so apply sealed affects to the new recipe
                    if (recipe == nextRecipe)
                    {
                        // Used by recipes that have the same output as input e.g. leather dyeing
                        // Otherwise, every tick they will craft the recipe
                        barrel.resetTickTimer(level);
                    }
                }
            }
        }

        if (barrel.needsInstantRecipeUpdate)
        {
            barrel.needsInstantRecipeUpdate = false;
            if (barrel.inventory.excess.isEmpty()) // Excess must be empty for instant recipes to apply
            {
                RecipeHolder<? extends BarrelRecipe> instantRecipe = KegRecipe.getInstant(barrel.inventory, level);
                if (instantRecipe == null)
                {
                    instantRecipe = KegRecipe.getInstantFluid(barrel.inventory, level);
                }
                if (instantRecipe != null)
                {
                    KegRecipe.assemble(instantRecipe.value(), barrel.inventory);
                    if (barrel.soundCooldownTicks == 0)
                    {
                        Helpers.playSound(level, barrel.getBlockPos(), instantRecipe.value().getCompleteSound());
                        barrel.soundCooldownTicks = 5;
                        if (instantRecipe.value().getCompleteSound() == SoundEvents.FIRE_EXTINGUISH && level instanceof ServerLevel server)
                        {
                            final double x = pos.getX() + 0.5;
                            final double y = pos.getY();
                            final double z = pos.getZ() + 0.5;
                            final RandomSource random = level.getRandom();
                            server.sendParticles(TFCParticles.BUBBLE.get(), x + random.nextFloat() * 0.375 - 0.1875, y + 15f / 16f, z + random.nextFloat() * 0.375 - 0.1875, 6, 0, 0, 0, 1);
                            server.sendParticles(TFCParticles.STEAM.get(), x + random.nextFloat() * 0.375 - 0.1875, y + 15f / 16f, z + random.nextFloat() * 0.375 - 0.1875, 6, 0, 0, 0, 1);
                        }
                    }
                }
                barrel.markForSync();
            }
        }

        if (barrel.soundCooldownTicks > 0)
        {
            barrel.soundCooldownTicks--;
        }

        barrel.tickPouring(level, pos, sealed, facing);
        barrel.tickPouring(level, pos.relative(state.getValue(KegCoreBlock.FACING).getClockWise()), sealed, facing);
    }

    public static final int SLOTS = 38;
    public static final int SLOT_FLUID_CONTAINER_IN = 0;
    public static final int SLOT_FLUID_CONTAINER_OUT = 1;
    public static final int SLOT_INPUT_START = 2;
    public static final int SLOT_INPUT_END = 37;
    public static final int CAPACITY = 80000;

    private final SidedHandler<IFluidHandler> sidedFluidInventory;
    private final CachedMut<RecipeHolder<SealedBarrelRecipe>> recipe = CachedMut.empty();
    private long lastUpdateTick = Integer.MIN_VALUE; // The last tick this barrel was updated in serverTick()
    private long sealedTick; // The tick this barrel was sealed
    private long recipeTick; // The tick this barrel started working on the current recipe
    private int soundCooldownTicks = 0;

    private boolean needsInstantRecipeUpdate; // If the instant recipe needs to be checked again

    public KegBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.KEG.get(), pos, state, KegInventory::new, FirmaLife.MOD_ID);

        final Direction facing = state.getValue(KegCoreBlock.FACING);
        final int[] slots = IntStream.rangeClosed(KegBlockEntity.SLOT_INPUT_START, KegBlockEntity.SLOT_INPUT_END).toArray();
        sidedInventory
            .on(new PartialItemHandler(inventory).insert(slots), d -> d != facing.getOpposite() && d != Direction.DOWN)
            .on(new PartialItemHandler(inventory).extract(slots), d -> d == facing.getOpposite() || d == Direction.DOWN);

        sidedFluidInventory = new SidedHandler<>(inventory);
        sidedFluidInventory
            .on(PartialFluidHandler::insertOnly, d -> d != facing.getOpposite() && d != Direction.DOWN)
            .on(PartialFluidHandler::extractOnly, d -> d == facing.getOpposite() || d == Direction.DOWN);

//        final Direction facing = state.getValue(KegCoreBlock.FACING);
//        sidedInventory
//            .on(new PartialItemHandler(inventory).extract(IntStream.rangeClosed(SLOT_INPUT_START, SLOT_INPUT_END).toArray()), d -> d == facing.getOpposite() || d == Direction.DOWN);
//
//        sidedFluidInventory = new SidedHandler<>(inventory);
//        sidedFluidInventory
//            .on(PartialFluidHandler::extractOnly, d -> d == facing.getOpposite() || d == Direction.DOWN);
    }

    @Nullable
    public IFluidHandler getSidedFluidInventory(@Nullable Direction dir)
    {
        return sidedFluidInventory.get(dir);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        needsInstantRecipeUpdate = true;
        updateRecipe();
        setChanged();
    }

    @Override
    public void fluidTankChanged()
    {
        needsInstantRecipeUpdate = true;
        updateRecipe();
        setChanged();
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return switch (slot)
        {
            case SLOT_FLUID_CONTAINER_IN -> Helpers.mightHaveCapability(stack, Capabilities.FluidHandler.ITEM);
            case SLOT_FLUID_CONTAINER_OUT -> true;
            default ->
            {
                // We only want to deny heavy/huge (aka things that can hold inventory).
                // Other than that, barrels don't need a size restriction, and should in general be unrestricted, so we can allow any kind of recipe input (i.e. unfired large vessel)
                final IItemSize size = ItemSizeManager.get(stack);
                yield size.getSize(stack).isSmallerThan(Size.HUGE) || size.getWeight(stack).isSmallerThan(Weight.VERY_HEAVY);
            }
        };
    }

    @Override
    public void onCalendarUpdate(long ticks)
    {
        assert level != null;

        try (CalendarTransaction tr = Calendars.SERVER.transaction())
        {
            tr.add(-ticks); // Perform the recipe update in the past
            updateRecipe();
        }

        @Nullable SealedBarrelRecipe recipe = getRecipe();
        if (!getBlockState().getValue(KegCoreBlock.SEALED) || recipe == null || recipe.isInfinite())
        {
            return; // No simulation occurs if we were not sealed, or if we had no recipe, or if we had an infinite recipe.
        }

        // Otherwise, begin simulation by jumping to the end tick of the current recipe. If that was in the past, we simulate and retry.
        final long currentTick = Calendars.SERVER.getTicks();
        long lastKnownTick = recipeTick + recipe.getDuration();
        while (lastKnownTick < currentTick)
        {
            // Need to run the recipe completion, as it occurred in the past
            final long offset = currentTick - lastKnownTick;
            assert offset >= 0; // This event should be in the past

            try (CalendarTransaction tr = Calendars.SERVER.transaction())
            {
                tr.add(-offset);

                if (KegRecipe.matches(recipe, inventory))
                {
                    KegRecipe.assemble(recipe, inventory);
                }
                updateRecipe();
                markForSync();
            }

            // Re-check the recipe. If we have an invalid or infinite recipe, then exit simulation. Otherwise, jump forward to the next recipe completion
            // This handles the case where multiple sequential recipes, such as brining -> pickling -> vinegar preservation would've occurred.
            final @Nullable SealedBarrelRecipe knownRecipe = getRecipe();
            if (knownRecipe == null)
            {
                return;
            }

            knownRecipe.onSealed(inventory); // We're in a sequential recipe, so apply sealed affects to the new recipe
            if (knownRecipe.isInfinite())
            {
                return; // No more simulation can occur
            }
            lastKnownTick += recipe.getDuration();
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putLong("lastUpdateTick", lastUpdateTick);
        nbt.putLong("sealedTick", sealedTick);
        nbt.putLong("recipeTick", recipeTick);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        lastUpdateTick = nbt.getLong("lastUpdateTick");
        sealedTick = nbt.getLong("sealedTick");
        recipeTick = nbt.getLong("recipeTick");
        recipe.unload();
        super.loadAdditional(nbt, provider);
    }

    @Override
    @Deprecated
    public long getLastCalendarUpdateTick()
    {
        return lastUpdateTick;
    }

    @Override
    @Deprecated
    public void setLastCalendarUpdateTick(long tick)
    {
        lastUpdateTick = tick;
    }

    @Override
    public int getRecipeDuration()
    {
        @Nullable SealedBarrelRecipe recipe = getRecipe();
        return recipe != null ? recipe.getDuration() : 0;
    }

    @Override
    public long getRemainingTime()
    {
        return getRemainingTicks();
    }

    @Override
    public void ejectInventory()
    {
        super.ejectInventory();
        assert level != null;
        inventory.excess.stream().filter(item -> !item.isEmpty()).forEach(item -> Helpers.spawnItem(level, worldPosition, item));

        final FluidStack fluid = inventory.tank.getFluid();
        if (level instanceof ServerLevel server && !fluid.isEmpty())
        {
            final double fill = (double) inventory.getFluidInTank(0).getAmount() / inventory.getTankCapacity(0);
            final VoxelShape shape = getBlockState().getShape(level, worldPosition);
            Helpers.playSound(level, worldPosition, SoundEvents.PLAYER_SPLASH);

            for (int i = 0; i < Math.ceil(25 * fill); i++)
            {
                RandomSource random = server.getRandom();
                final double xMax = shape.max(Direction.Axis.X);
                final double xMin = shape.min(Direction.Axis.X);
                final double zMax = shape.max(Direction.Axis.Z);
                final double zMin = shape.min(Direction.Axis.Z);
                final double dx = xMin + (xMax - xMin) * random.nextDouble();
                final double dy = shape.max(Direction.Axis.Y) * fill * random.nextDouble();
                final double dz = zMin + (zMax - zMin) * random.nextDouble();
                server.sendParticles(new FluidParticleOption(TFCParticles.BARREL_SPILL.get(), fluid.getFluid()), worldPosition.getX() + dx, worldPosition.getY() + dy, worldPosition.getZ() + dz, 1, 0, 0, 0, 1f);
            }
        }
    }

    public void tickPouring(Level level, BlockPos pos, boolean sealed, Direction facing)
    {
        if (!sealed && !inventory.tank.isEmpty())
        {
            final BlockPos faucetPos = pos.relative(facing.getOpposite());
            if (level.getBlockState(faucetPos).isAir())
            {
                final IFluidHandler fluidHandler = level.getCapability(Capabilities.FluidHandler.BLOCK, faucetPos.below(), Direction.UP);
                if (fluidHandler == null)
                    return;
                if (FluidHelpers.transferExact(this.inventory.tank, fluidHandler, 1))
                {
                    if (level.getGameTime() % 12 == 0)
                    {
                        Helpers.playSound(level, pos, TFCSounds.BARREL_DRIP.get());
                    }
                }
            }
        }
    }

    public void onSeal()
    {
        assert level != null;
        if (!level.isClientSide())
        {
            // Drop container items, but allow the main slot to be filled
            Helpers.spawnItem(level, worldPosition, Helpers.removeStack(inventory, SLOT_FLUID_CONTAINER_IN));
            Helpers.spawnItem(level, worldPosition, Helpers.removeStack(inventory, SLOT_FLUID_CONTAINER_OUT));
        }

        sealedTick = Calendars.get(level).getTicks();
        updateRecipe();

        final @Nullable SealedBarrelRecipe recipe = getRecipe();
        if (recipe != null)
        {
            KegRecipe.onSealed(recipe, inventory);
            recipeTick = sealedTick;
        }

        markForSync();
        Helpers.playSound(level, worldPosition, TFCSounds.CLOSE_BARREL.get());
    }

    public void onUnseal()
    {
        assert level != null;

        sealedTick = 0L;
        recipeTick = 0L;

        final @Nullable SealedBarrelRecipe recipe = getRecipe();
        if (recipe != null)
        {
            KegRecipe.onUnsealed(recipe, inventory);
        }

        updateRecipe();
        markForSync();
        Helpers.playSound(level, worldPosition, TFCSounds.OPEN_BARREL.get());
    }

    @Override
    public boolean canModify()
    {
        return !getBlockState().getValue(KegCoreBlock.SEALED);
    }

    private void updateFluidIOSlots()
    {
        assert level != null;
        final ItemStack input = inventory.getStackInSlot(SLOT_FLUID_CONTAINER_IN);
        if (!input.isEmpty() && inventory.getStackInSlot(SLOT_FLUID_CONTAINER_OUT).isEmpty())
        {
            FluidHelpers.transferBetweenBlockEntityAndItem(input, this, level, worldPosition, (newOriginalStack, newContainerStack) -> {
                if (newContainerStack.isEmpty())
                {
                    // No new container was produced, so shove the first stack in the output, and clear the input
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, ItemStack.EMPTY);
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_OUT, newOriginalStack);
                }
                else
                {
                    // We produced a new container - this will be the 'filled', so we need to shove *that* in the output
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_IN, newOriginalStack);
                    inventory.setStackInSlot(SLOT_FLUID_CONTAINER_OUT, newContainerStack);
                }
            });
        }
    }

    private void updateRecipe()
    {
        assert level != null;

        final @Nullable SealedBarrelRecipe oldRecipe = RecipeHelpers.unbox(recipe.value());
        recipe.unload();
        final @Nullable SealedBarrelRecipe newRecipe = getRecipe(); // Trigger the update

        if (oldRecipe != null && newRecipe != null && oldRecipe != newRecipe)
        {
            // The recipe has changed to a new one, so update the recipe ticks
            resetTickTimer(level);
        }
    }

    private void resetTickTimer(Level level)
    {
        recipeTick = Calendars.get(level).getTicks();
        markForSync();
    }

    /**
     * Returns the current sealed barrel recipe. This might cause a recipe lookup if the recipe name is currently cached.
     */
    @Nullable
    public SealedBarrelRecipe getRecipe()
    {
        assert level != null;
        if (!recipe.isLoaded())
        {
            // Only find a recipe if we have an empty excess inventory
            recipe.load(inventory.excess.isEmpty()
                ? KegRecipe.getSealed(inventory, level)
                : null);
        }
        return RecipeHelpers.unbox(recipe.value());
    }

    @Nullable
    public Component getRecipeTooltip()
    {
        getRecipe(); // Load recipe if present
        final RecipeHolder<SealedBarrelRecipe> holder = recipe.value();
        return holder != null
            ? Component.translatable("tfc.recipe.barrel." + holder.id().getNamespace() + "." + holder.id().getPath().replace('/', '.'))
            : null;
    }

    public long getSealedTick()
    {
        return sealedTick;
    }

    public long getRecipeTick()
    {
        return recipeTick;
    }

    public long getRemainingTicks()
    {
        assert level != null;

        final @Nullable SealedBarrelRecipe recipe = getRecipe();
        return recipe != null
            ? recipe.getDuration() - (Calendars.get(level).getTicks() - recipeTick)
            : 0;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player)
    {
        return KegContainer.create(this, player.getInventory(), containerId);
    }

    public static class KegInventory implements DelegateItemHandler, DelegateFluidHandler, NonEmptyInput, FluidTankCallback, BarrelInventory, INBTSerializable<CompoundTag>
    {
        private final BarrelInventoryCallback callback;
        private final InventoryItemHandler inventory;
        private final List<ItemStack> excess;
        private final InventoryFluidTank tank;
        private boolean mutable; // If the inventory is pretending to be mutable, despite the barrel being sealed and preventing extractions / insertions

        KegInventory(InventoryBlockEntity<?> inventory)
        {
            this((BarrelInventoryCallback) inventory);
        }

        KegInventory(BarrelInventoryCallback inventory)
        {
            this.callback = inventory;
            this.inventory = new InventoryItemHandler(inventory, SLOTS);
            tank = new InventoryFluidTank(CAPACITY, stack -> Helpers.isFluid(stack.getFluid(), TFCTags.Fluids.USABLE_IN_BARREL), this);
            excess = new ArrayList<>();
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @Override
        public IFluidHandler getFluidHandler()
        {
            return tank;
        }

        @Override
        public int fill(FluidStack resource, FluidAction action)
        {
            return canModify() ? tank.fill(resource, action) : 0;
        }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action)
        {
            return canModify() ? tank.drain(resource, action) : FluidStack.EMPTY;
        }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            return canModify() ? tank.drain(maxDrain, action) : FluidStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
        {
            return canModify() ? inventory.insertItem(slot, stack, simulate) : stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return canModify() ? inventory.extractItem(slot, amount, simulate) : ItemStack.EMPTY;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack)
        {
            return canModify() && DelegateItemHandler.super.isItemValid(slot, stack);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider provider)
        {
            final CompoundTag nbt = new CompoundTag();
            nbt.put("inventory", inventory.serializeNBT(provider));
            nbt.put("tank", tank.writeToNBT(provider, new CompoundTag()));
            nbt.put("excess", Helpers.writeItemStacksToNbt(provider, excess));
            return nbt;
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt)
        {
            inventory.deserializeNBT(provider, nbt.getCompound("inventory"));
            tank.readFromNBT(provider, nbt.getCompound("tank"));
            Helpers.readItemStacksFromNbt(provider, excess, nbt.getList("excess", Tag.TAG_COMPOUND));
        }

        @Override
        public void fluidTankChanged()
        {
            callback.fluidTankChanged();
        }

        private boolean canModify()
        {
            return mutable || callback.canModify();
        }

        @Override
        public void whileMutable(Runnable action)
        {
            try
            {
                mutable = true;
                action.run();
            }
            finally
            {
                mutable = false;
            }
        }

        @Override
        public void insertItemWithOverflow(ItemStack stack)
        {
            stack = Helpers.insertSlots(inventory, stack, SLOT_INPUT_START, SLOT_INPUT_END + 1);
            if (!stack.isEmpty())
            {
                excess.add(stack);
            }
        }
    }
}
