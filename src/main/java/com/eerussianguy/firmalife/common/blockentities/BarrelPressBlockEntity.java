package com.eerussianguy.firmalife.common.blockentities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import com.eerussianguy.firmalife.FirmaLife;
import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.FLFluids;
import com.eerussianguy.firmalife.common.capabilities.FLComponents;
import com.eerussianguy.firmalife.common.capabilities.wine.WineComponent;
import com.eerussianguy.firmalife.common.capabilities.wine.WineType;
import com.eerussianguy.firmalife.common.container.BarrelPressContainer;
import com.eerussianguy.firmalife.common.items.FLFood;
import com.eerussianguy.firmalife.common.items.FLFoodTraits;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.overworld.SolarCalculator;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.ItemCapabilities;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodTrait;
import net.dries007.tfc.common.component.food.FoodTraits;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.climate.KoppenClimateClassification;

public class BarrelPressBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler>
{
    public static void tick(Level level, BlockPos pos, BlockState state, BarrelPressBlockEntity press)
    {
        press.checkForLastTickSync();

        final boolean doneWorking = level.getGameTime() - press.lastPushed > TIME;
        if (!press.didAction && doneWorking)
        {
            press.didAction = true;
            press.squish();
        }
        if (!doneWorking && level.isClientSide && level.getGameTime() % 2 == 0)
        {
            final RandomSource rand = level.random;
            final ItemStack item = press.inventory.getStackInSlot(rand.nextInt(SLOTS));
            if (!item.isEmpty())
                level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, item), pos.getX() + rand.nextFloat(), pos.getY() + 0.2f, pos.getZ() + rand.nextFloat(), rand.nextFloat(), 0.125f + 0.5f * rand.nextFloat(), rand.nextFloat());
        }
        if (press.needsSlotUpdate && press.hasOutput() && press.output != null)
        {
            if (press.inventory.getStackInSlot(SLOT_WINE_OUT).isEmpty())
            {
                final ItemStack current = press.inventory.getStackInSlot(SLOT_WINE_IN);
                final ItemStack newStack = press.tryFillWine(level, pos, current);
                if (!newStack.isEmpty())
                {
                    press.inventory.setStackInSlot(SLOT_WINE_OUT, newStack);
                }
            }
        }
    }


    @Nullable
    private static Item getFilledWine(Item item)
    {
        if (item == FLItems.EMPTY_HEMATITIC_WINE_BOTTLE.get())
            return FLItems.HEMATITIC_WINE_BOTTLE.get();
        if (item == FLItems.EMPTY_OLIVINE_WINE_BOTTLE.get())
            return FLItems.OLIVINE_WINE_BOTTLE.get();
        if (item == FLItems.EMPTY_VOLCANIC_WINE_BOTTLE.get())
            return FLItems.VOLCANIC_WINE_BOTTLE.get();
        return null;
    }

    public static final int SLOTS = 9;
    public static final int SLOT_GRAPES = 0;
    public static final int SLOT_WINE_IN = 5;
    public static final int SLOT_WINE_OUT = 6;
    public static final int SLOT_CORK = 7;
    public static final int SLOT_LABEL = 8;
    public static final long TIME = 40L;

    private boolean didAction = false;
    private boolean needsSlotUpdate = false;
    private long lastPushed = 0L;
    @Nullable private WineOutput output = null;

    public BarrelPressBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.BARREL_PRESS.get(), pos, state, defaultInventory(SLOTS), FirmaLife.MOD_ID);
    }

    public ItemInteractionResult push()
    {
        assert level != null;
        if (level.getGameTime() - lastPushed > TIME)
        {
            didAction = false;
            lastPushed = level.getGameTime();
            markForSync();
            return ItemInteractionResult.sidedSuccess(level.isClientSide);
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    public void squish()
    {
        final ItemStack grapes = inventory.getStackInSlot(SLOT_GRAPES);
        final int servings = grapes.getCount() / 4;
        if (servings == 0 || level == null)
            return;
        final WineType wine = getWineType();
        if (wine == null)
            return;
        final var climate = KoppenClimateClassification.classify(Climate.getAverageTemperature(level, worldPosition), Climate.getRainfall(level, worldPosition), Climate.getRainfallVariance(level, worldPosition), SolarCalculator.getInNorthernHemisphere(worldPosition, level));

        final List<Holder<FoodTrait>> traits = new ArrayList<>();
        final IFood food = FoodCapability.get(grapes);
        if (food != null)
        {
            food.getTraits().forEach(trait -> {
                final Holder<FoodTrait> holder = FoodTraits.REGISTRY.createIntrusiveHolder(trait);
                if (holder.is(FLTags.Traits.WINE))
                {
                    traits.add(holder);
                }
            });
        }
        output = new WineOutput(wine, climate, servings, traits);

        for (int i = SLOT_GRAPES; i < SLOT_WINE_IN; i++)
        {
            inventory.setStackInSlot(i, ItemStack.EMPTY);
        }

        markForSync();
    }

    @Nullable
    public WineType getWineType()
    {
        final ItemStack grapes = inventory.getStackInSlot(SLOT_GRAPES);
        if (FoodCapability.isRotten(grapes))
            return null;
        final Item redGrapes = FLItems.FOODS.get(FLFood.SMASHED_RED_GRAPES).get();
        if (Helpers.isItem(grapes, redGrapes))
        {
            return WineType.RED;
        }
        else if (Helpers.isItem(grapes, FLItems.FOODS.get(FLFood.SMASHED_WHITE_GRAPES).get()))
        {
            if (hasAtLeastThisMuchOfThisInOtherSlots(s -> s.getItem() == redGrapes && FoodCapability.hasTrait(s, FLFoodTraits.FERMENTED) && !FoodCapability.isRotten(s), 1))
            {
                return WineType.ROSE;
            }
            if (hasAtLeastThisMuchOfThisInOtherSlots(s -> Helpers.isItem(s, FLTags.Items.SWEETENER), 2))
            {
                return WineType.DESSERT;
            }
            if (hasAtLeastThisMuchOfThisInOtherSlots(s -> s.getItem() == FLItems.TIRAGE_MIXTURE.get(), 1))
            {
                return WineType.SPARKLING;
            }
            return WineType.WHITE;
        }
        return null;
    }

    public ItemStack tryFillWine(Level level, BlockPos pos, ItemStack current)
    {
        if (output == null || !hasOutput())
            return ItemStack.EMPTY;
        final Item newWine = getFilledWine(current.getItem());
        if (Helpers.isItem(current, FLTags.Items.EMPTY_WINE_BOTTLES) && newWine != null)
        {
            if (inventory.extractItem(SLOT_CORK, 1, false).isEmpty())
                return ItemStack.EMPTY;
            final ItemStack label = inventory.extractItem(SLOT_LABEL, 1, false);
            final ItemStack bottle = newWine.getDefaultInstance();
            bottle.set(FLComponents.WINE, new WineComponent(
                Calendars.get(level).getTicks(),
                -1L,
                Optional.ofNullable(!label.isEmpty() ? label.getHoverName().getString() : null),
                output.wine,
                output.koppen,
                List.copyOf(output.traits)
            ));
            final var cap = bottle.getCapability(ItemCapabilities.FLUID);
            if (cap != null)
            {
                cap.fill((new FluidStack(FLFluids.WINE_FLUIDS.get(output.wine).getSource(), 2000)), IFluidHandler.FluidAction.EXECUTE);
            }

            current.shrink(1);
            int newServings = output.servings - 1;
            inventory.setStackInSlot(SLOT_WINE_IN, ItemStack.EMPTY);
            if (newServings <= 0)
            {
                output = null;
            }
            else
            {
                output = new WineOutput(output.wine, output.koppen, newServings, List.copyOf(output.traits));
            }

            Helpers.playSound(level, pos, SoundEvents.BOTTLE_FILL);
            return bottle;
        }
        return ItemStack.EMPTY;
    }

    private boolean hasAtLeastThisMuchOfThisInOtherSlots(Predicate<ItemStack> test, int count)
    {
        int found = 0;
        for (int i = SLOT_GRAPES + 1; i <= SLOT_WINE_IN - 1; i++)
        {
            if (test.test(inventory.getStackInSlot(i)))
            {
                found++;
                if (found >= count)
                    return true;
            }
        }
        return found >= count;
    }

    @Nullable
    public WineOutput getOutput()
    {
        return output;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return slot == SLOT_GRAPES ? 16 : (slot == SLOT_CORK || slot == SLOT_LABEL) ? 64 : 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == SLOT_GRAPES && Helpers.isItem(stack, FLTags.Items.CAN_BE_PRESSED_LIKE_GRAPES))
        {
            return FoodCapability.hasTrait(stack, FLFoodTraits.FERMENTED);
        }
        if (slot == SLOT_WINE_OUT)
            return false; // only code can add an item here
        if (slot == SLOT_WINE_IN)
            return Helpers.isItem(stack, FLTags.Items.EMPTY_WINE_BOTTLES);
        if (slot == SLOT_CORK)
            return Helpers.isItem(stack, FLItems.CORK.get());
        if (slot == SLOT_LABEL)
            return Helpers.isItem(stack, FLItems.BOTTLE_LABEL.get());
        return !Helpers.isItem(stack, FLItems.CORK.get()) && !Helpers.isItem(stack, FLItems.BOTTLE_LABEL.get()) && !Helpers.isItem(stack, FLTags.Items.EMPTY_WINE_BOTTLES);
    }

    public boolean hasOutput()
    {
        if (output != null && output.servings <= 0)
        {
            output = null;
            return false;
        }
        return output != null;
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider access)
    {
        super.saveAdditional(tag, access);
        tag.putLong("pushed", this.lastPushed);
        tag.putBoolean("didAction", didAction);
        if (output != null)
            output.save(tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider access)
    {
        super.loadAdditional(tag, access);
        this.lastPushed = tag.getLong("pushed");
        this.didAction = tag.getBoolean("didAction");
        if (tag.contains("output", Tag.TAG_COMPOUND))
            output = WineOutput.of(tag.getCompound("output"));

        needsSlotUpdate = true;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsSlotUpdate = true;
    }

    public float sinceWeLastTouched(float partialTick)
    {
        assert level != null;
        return (level.getGameTime() - lastPushed) + partialTick;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return BarrelPressContainer.create(this, inv, windowID);
    }

    public record WineOutput(
        WineType wine,
        KoppenClimateClassification koppen,
        int servings,
        List<Holder<FoodTrait>> traits
    )
    {
        public static WineOutput of(CompoundTag tag)
        {
            final List<Holder<FoodTrait>> traits = new ArrayList<>();
            FLHelpers.readTraitList(traits, tag, "traits");
            return new WineOutput(
                WineType.VALUES[tag.getInt("wineType")],
                WineType.KOPPEN_VALUES[tag.getInt("climate")],
                tag.getInt("servings"),
                traits
            );
        }

        public void save(CompoundTag tag)
        {
            final CompoundTag outputTag = new CompoundTag();
            outputTag.putInt("wineType", wine.ordinal());
            outputTag.putInt("climate", koppen.ordinal());
            outputTag.putInt("servings", servings);
            FLHelpers.writeTraitList(traits, outputTag, "traits");
            tag.put("output", outputTag);
        }
    }
}
