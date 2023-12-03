package com.eerussianguy.firmalife.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.FLTags;
import com.eerussianguy.firmalife.common.blocks.MixingBowlBlock;
import com.eerussianguy.firmalife.common.recipes.FLRecipeTypes;
import com.eerussianguy.firmalife.common.recipes.MixingBowlRecipe;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.capabilities.*;
import net.dries007.tfc.common.fluids.FluidHelpers;
import net.dries007.tfc.common.fluids.TFCFluids;
import net.dries007.tfc.common.recipes.inventory.EmptyInventory;
import net.dries007.tfc.util.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MixingBowlBlockEntity extends TickableInventoryBlockEntity<MixingBowlBlockEntity.MixingBowlInventory>
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, MixingBowlBlockEntity bowl)
    {
        bowl.checkForLastTickSync();

        if (bowl.rotationTimer > 0)
        {
            ServerLevel serverLevel = (ServerLevel) level;
            final int slotParticle = level.random.nextInt(SLOTS);
            final ItemStack particleStack = bowl.inventory.getStackInSlot(slotParticle);
            if (!particleStack.isEmpty())
            {
                sendParticle(serverLevel, pos, particleStack, 1);
            }
            if (level.getGameTime() % 5 == 0)
            {
                Helpers.playSound(level, pos, SoundEvents.SLIME_SQUISH_SMALL);
            }

            bowl.rotationTimer--;
            if (bowl.rotationTimer == 0)
            {
                bowl.finishMixing();
                Helpers.playSound(level, pos, SoundEvents.ARMOR_STAND_FALL);
            }
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, MixingBowlBlockEntity bowl)
    {
        if (bowl.rotationTimer > 0)
        {
            bowl.rotationTimer--;
        }
    }

    private static void sendParticle(ServerLevel level, BlockPos pos, ItemStack item, int count)
    {
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item), pos.getX() + 0.5D, pos.getY() + 0.13D, pos.getZ() + 0.5D, count, Helpers.triangle(level.random) / 2.0D, level.random.nextDouble() / 4.0D, Helpers.triangle(level.random) / 2.0D, 0.15f);
    }

    public static final int SLOTS = 5;

    private final SidedHandler.Builder<IFluidHandler> sidedFluidInventory;
    private int rotationTimer = -1;

    public MixingBowlBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.MIXING_BOWL.get(), pos, state, MixingBowlInventory::new, FLHelpers.blockEntityName("mixing_bowl"));

        sidedInventory
            .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3, 4), Direction.Plane.HORIZONTAL)
            .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3, 4), Direction.UP);

        sidedFluidInventory = new SidedHandler.Builder<IFluidHandler>(inventory)
            .on(inventory, Direction.Plane.HORIZONTAL);
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        super.loadAdditional(nbt);
        this.rotationTimer = nbt.getInt("rotationTimer");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putInt("rotationTimer", rotationTimer);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    public boolean isMixing()
    {
        return rotationTimer > 0;
    }

    public int getRotationTimer()
    {
        return rotationTimer;
    }

    public boolean startMixing(Player player)
    {
        assert level != null;
        BlockState state = level.getBlockState(worldPosition);
        if (state.getBlock() instanceof MixingBowlBlock && !state.getValue(MixingBowlBlock.SPOON))
        {
            return complain(player, "spoon");
        }
        if (isMixing())
        {
            return complain(player, "mixing");
        }
        final MixingBowlRecipe recipe = getRecipe();
        if (recipe == null)
        {
            return complain(player, "no_recipe");
        }
        if (!recipe.matches(inventory, level))
        {
            return complain(player, "matching_recipe");
        }
        else
        {
            rotationTimer = 120;
            markForSync();
            return true;
        }
    }

    private boolean complain(Player player, String name)
    {
        player.displayClientMessage(Component.translatable("firmalife.bowl." + name), true);
        return false;
    }

    private void finishMixing()
    {
        assert level != null;
        final MixingBowlRecipe recipe = getRecipe();
        if (recipe != null && recipe.matches(inventory, level))
        {
            ItemStack outputStack = recipe.assemble(inventory, level.registryAccess());
            int count = outputStack.getCount();
            for (int i = 0; i < SLOTS; i++)
            {
                if (inventory.getStackInSlot(i).hasCraftingRemainingItem())
                {
                    Helpers.spawnItem(level, worldPosition, inventory.getStackInSlot(i).getCraftingRemainingItem().copy());
                }
                inventory.setStackInSlot(i, ItemStack.EMPTY);
            }
            for (int i = 0; i < SLOTS; i++)
            {
                if (count > 0)
                {
                    inventory.setStackInSlot(i, outputStack.copyWithCount(1));
                    count--;
                }
                else
                {
                    break;
                }
            }
            inventory.drain(FluidHelpers.BUCKET_VOLUME, IFluidHandler.FluidAction.EXECUTE);
            inventory.fill(recipe.getResultFluid(), IFluidHandler.FluidAction.EXECUTE);
        }
        markForSync();
    }

    @Nullable
    public MixingBowlRecipe getRecipe()
    {
        assert level != null;
        return level.getRecipeManager().getRecipeFor(FLRecipeTypes.MIXING_BOWL.get(), inventory, level).orElse(null);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side)
    {
        if (cap == Capabilities.FLUID)
        {
            return sidedFluidInventory.getSidedHandler(side).cast();
        }
        return super.getCapability(cap, side);
    }

    public static class MixingBowlInventory implements EmptyInventory, DelegateItemHandler, DelegateFluidHandler, INBTSerializable<CompoundTag>
    {
        private final MixingBowlBlockEntity bowl;
        private final ItemStackHandler inventory;
        private final FluidTank tank;

        public MixingBowlInventory(InventoryBlockEntity<MixingBowlInventory> entity)
        {
            this.bowl = (MixingBowlBlockEntity) entity;
            this.inventory = new InventoryItemHandler(entity, SLOTS);
            this.tank = new FluidTank(FluidHelpers.BUCKET_VOLUME, fluid -> Helpers.isFluid(fluid.getFluid(), FLTags.Fluids.USABLE_IN_MIXING_BOWL));
        }

        public FluidStack readFluid()
        {
            return tank.getFluidInTank(0);
        }

        @NotNull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate)
        {
            return inventory.extractItem(slot, amount, simulate);
        }

        @Override
        public IFluidHandler getFluidHandler()
        {
            return tank;
        }

        @Override
        public IItemHandlerModifiable getItemHandler()
        {
            return inventory;
        }

        @Override
        public CompoundTag serializeNBT()
        {
            CompoundTag nbt = new CompoundTag();
            nbt.put("inventory", inventory.serializeNBT());
            nbt.put("tank", tank.writeToNBT(new CompoundTag()));
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt)
        {
            inventory.deserializeNBT(nbt.getCompound("inventory"));
            tank.readFromNBT(nbt.getCompound("tank"));
        }
    }
}
