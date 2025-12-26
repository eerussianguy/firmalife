package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.blocks.bee.CentrifugeBlock;
import com.eerussianguy.firmalife.common.recipes.CentrifugeRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.TickableInventoryBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.RotationSinkBlockEntity;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.SinkNode;

public class CentrifugeBlockEntity extends TickableInventoryBlockEntity<ItemStackHandler> implements RotationSinkBlockEntity
{
    public static void serverTick(Level level, BlockPos pos, BlockState state, CentrifugeBlockEntity cent)
    {
        cent.checkForLastTickSync();
        final ServerLevel serverLevel = (ServerLevel) level;

        final boolean wasGrinding = cent.recipeTimer > 0;

        clientTick(level, pos, state, cent);

        if (wasGrinding)
        {
            final ItemStack inputStack = cent.inventory.getStackInSlot(level.random.nextInt(4));
            if (!inputStack.isEmpty())
            {
                sendParticle(serverLevel, pos, inputStack, 1);
            }
        }

        if (wasGrinding && cent.recipeTimer <= 0)
        {
            cent.finishGrinding();
            Helpers.playSound(level, pos, SoundEvents.ARMOR_STAND_FALL);

            if (cent.isConnectedToNetwork())
            {
                // If possible, immediately restart
                cent.startWorking();
            }
        }

        if (cent.isConnectedToNetwork() && !cent.isWorking() && level.getGameTime() % 10 == 0)
        {
            cent.startWorking();
        }
    }

    public static void clientTick(Level level, BlockPos pos, BlockState state, CentrifugeBlockEntity cent)
    {
        if (cent.recipeTimer > 0)
        {
            if (cent.node.rotation() != null)
            {
                cent.previousRotationDirection = cent.node.rotation().direction() == Direction.UP ? 1 : -1;
                cent.previousRotationSpeed = cent.getRotationSpeed();
            }
            cent.recipeTimer -= cent.isConnectedToNetwork()
                ? cent.getRotationSpeed() * NETWORK_RECIPE_PER_SPEED
                : MANUAL_RECIPE_PER_TICK;
        }
    }

    private static void sendParticle(ServerLevel level, BlockPos pos, ItemStack item, int count)
    {
        level.sendParticles(new ItemParticleOption(ParticleTypes.ITEM, item), pos.getX() + 0.5D, pos.getY() + 1.1D, pos.getZ() + 0.5D, count, Helpers.triangle(level.random) / 2.0D, level.random.nextDouble() / 4.0D, Helpers.triangle(level.random) / 2.0D, 0.15f);
    }

    public static final int MANUAL_TICKS = 90;
    public static final float MANUAL_SPEED = Mth.TWO_PI / MANUAL_TICKS; // In radians / tick

    private static final float MANUAL_RECIPE_PER_TICK = 1f; // Exactly 90 ticks at 1/tick
    private static final float NETWORK_RECIPE_PER_SPEED = MANUAL_TICKS / Mth.TWO_PI; // progress / radian

    public static final int SLOTS = 4;

    private final Node node;
    private float recipeTimer;
    private float previousRotationDirection = 1;
    private float previousRotationSpeed = MANUAL_SPEED;

    public CentrifugeBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.CENTRIFUGE.get(), pos, state, defaultInventory(SLOTS));

        this.node = new SinkNode(pos, Direction.DOWN)
        {
            @Override
            public String toString()
            {
                return "SinkNode[pos=%s]".formatted(pos);
            }
        };

        sidedInventory
            .on(new PartialItemHandler(inventory).extract(0, 1, 2, 3), d -> d != Direction.UP)
            .on(new PartialItemHandler(inventory).insert(0, 1, 2, 3), d -> d == Direction.UP);
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        markForSync();
    }

    public boolean isWorking()
    {
        return recipeTimer > 0;
    }

    public boolean startWorking()
    {
        assert level != null;

        for (int i = 0; i < inventory.getSlots(); i++)
        {
            final ItemStack inputStack = inventory.getStackInSlot(i);
            if (!inputStack.isEmpty())
            {
                final CentrifugeRecipe recipe = CentrifugeRecipe.getRecipe(inputStack);
                if (recipe != null && recipe.matches(inputStack))
                {
                    recipeTimer = MANUAL_TICKS;
                    level.playSound(null, worldPosition, TFCSounds.QUERN_DRAG.get(), SoundSource.BLOCKS, 1, 1 + ((level.random.nextFloat() - level.random.nextFloat()) / 16));
                    markForSync();
                    previousRotationDirection = 1;
                    previousRotationSpeed = MANUAL_SPEED;
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        super.loadAdditional(nbt, provider);
        recipeTimer = nbt.getFloat("recipeTimer");
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider provider)
    {
        nbt.putFloat("recipeTimer", recipeTimer);
        super.saveAdditional(nbt, provider);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return CentrifugeRecipe.getRecipe(stack) != null;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    protected void onLoadAdditional()
    {
        this.performNetworkAction(NetworkAction.ADD);
    }

    @Override
    protected void onUnloadAdditional()
    {
        this.performNetworkAction(NetworkAction.REMOVE);
    }

    public float getRotationSpeed()
    {
        return node.rotation() != null ? Mth.abs(node.rotation().speed()) : (isWorking() ? previousRotationSpeed : 0f);
    }


    public boolean isConnectedToNetwork()
    {
        return node.rotation() != null;
    }

    @Override
    public float getRotationAngle(float partialTick)
    {
        return isConnectedToNetwork()
            ? RotationSinkBlockEntity.super.getRotationAngle(partialTick)
            : -recipeTimer * previousRotationSpeed * previousRotationDirection;
    }

    @Override
    public Node getRotationNode()
    {
        return node;
    }

    private void finishGrinding()
    {
        assert level != null;
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            final ItemStack inputStack = inventory.getStackInSlot(i);
            if (!inputStack.isEmpty())
            {
                final CentrifugeRecipe recipe = CentrifugeRecipe.getRecipe(inputStack);
                if (recipe != null && recipe.matches(inputStack))
                {
                    inventory.setStackInSlot(i, inputStack.getCraftingRemainingItem().copy());
                    ItemStack outputStack = recipe.assemble(inputStack);

                    final BlockPos offsetPos = worldPosition.relative(getBlockState().getValue(CentrifugeBlock.FACING));

                    if (!level.isClientSide)
                        Helpers.spawnItem(level, level.getBlockState(offsetPos).canBeReplaced() ? offsetPos : worldPosition.above(), outputStack, 0.2, 0, 0, 0);
                }
            }
        }
        recipeTimer = 0f;
        markForSync();
    }
}
