package com.eerussianguy.firmalife.common.blockentities;

import com.eerussianguy.firmalife.common.FLHelpers;
import com.eerussianguy.firmalife.common.blocks.CompostTumblerBlock;
import com.eerussianguy.firmalife.common.items.FLItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import com.eerussianguy.firmalife.config.FLConfig;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blockentities.InventoryBlockEntity;
import net.dries007.tfc.common.blockentities.rotation.RotationSinkBlockEntity;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.climate.Climate;
import net.dries007.tfc.util.rotation.NetworkAction;
import net.dries007.tfc.util.rotation.Node;
import net.dries007.tfc.util.rotation.SinkNode;

public class CompostTumblerBlockEntity extends InventoryBlockEntity<ItemStackHandler> implements RotationSinkBlockEntity
{
    public static final int MAX_COMPOST = 32;
    public static final int MIN_COMPOST = 16;
    public static final int SLOT_COMPOST = 0;

    protected long lastUpdateTick = Integer.MIN_VALUE;
    private int green, brown, fish, bones, pottery, charcoal;
    private boolean rotten = false;

    private final Node node;

    public CompostTumblerBlockEntity(BlockPos pos, BlockState state)
    {
        super(FLBlockEntities.COMPOST_TUMBLER.get(), pos, state, defaultInventory(1), FLHelpers.blockEntityName("compost_tumbler"));

        final Direction connection = state.getValue(CompostTumblerBlock.FACING).getOpposite();

        this.node = new SinkNode(pos, connection) {
            @Override
            public String toString()
            {
                return "Tumbler[pos=%s]".formatted(pos());
            }
        };
    }

    public InteractionResult use(ItemStack stack, Player player, boolean client)
    {
        assert level != null;
        if (isReady() && node.rotation() == null)
        {
            ItemHandlerHelper.giveItemToPlayer(player, inventory.extractItem(SLOT_COMPOST, 64, false));
            reset();
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        if (!inventory.getStackInSlot(SLOT_COMPOST).isEmpty())
            return InteractionResult.PASS;
        final int total = getTotal();
        if (total >= MAX_COMPOST || rotten)
        {
            return InteractionResult.PASS;
        }
        final Compost compost = getCompost(stack);
        if (compost.isEmpty())
            return InteractionResult.PASS;
        final int toAdd = Math.min(MAX_COMPOST - compost.amount, compost.amount);
        switch (compost.type)
        {
            case BROWN -> brown += toAdd;
            case GREEN -> green += toAdd;
            case BONE -> bones += toAdd;
            case CHARCOAL -> charcoal += toAdd;
            case FISH -> fish += toAdd;
            case POTTERY -> pottery += toAdd;
            case POISON -> rotten = true;
        }
        if (!player.isCreative())
            stack.shrink(1);
        markForSync();
        resetCounter();
        return InteractionResult.sidedSuccess(client);
    }

    public void randomTick()
    {
        if (!canWork())
            return;
        final int total = getTotal();
        if (isReady())
        {
            ItemStack result;
            calculateRotten();
            getTotal();
            final int count = total == MAX_COMPOST ? 3 : total > 24 ? 2 : 1;
            if (rotten)
            {
                result = new ItemStack(TFCItems.ROTTEN_COMPOST.get(), count);
            }
            else
            {
                result = new ItemStack(TFCItems.COMPOST.get(), count);
            }
            inventory.setStackInSlot(SLOT_COMPOST, result);
            reset();
            markForSync();
        }
    }

    public boolean canWork()
    {
        if (node.rotation() == null || !inventory.getStackInSlot(SLOT_COMPOST).isEmpty())
        {
            resetCounter();
            return false;
        }
        final int total = getTotal();
        if (total < 16)
        {
            resetCounter();
            return false;
        }
        return true;
    }

    @Override
    public void loadAdditional(CompoundTag nbt)
    {
        green = nbt.getInt("green");
        brown = nbt.getInt("brown");
        fish = nbt.getInt("fish");
        bones = nbt.getInt("bones");
        pottery = nbt.getInt("pottery");
        charcoal = nbt.getInt("charcoal");
        rotten = nbt.getBoolean("rotten");
        lastUpdateTick = nbt.getLong("tick");
        super.loadAdditional(nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.putInt("green", green);
        nbt.putInt("brown", brown);
        nbt.putInt("fish", fish);
        nbt.putInt("bones", bones);
        nbt.putInt("pottery", pottery);
        nbt.putInt("charcoal", charcoal);
        nbt.putBoolean("rotten", rotten);
        nbt.putLong("tick", lastUpdateTick);
        super.saveAdditional(nbt);
    }

    public int getTotal()
    {
        return green + brown + fish + bones + pottery + charcoal;
    }

    public boolean isRotten()
    {
        return rotten || inventory.getStackInSlot(SLOT_COMPOST).getItem() == TFCItems.ROTTEN_COMPOST.get();
    }

    public boolean isReady()
    {
        return (getTicksSinceUpdate() > getReadyTicks() && getTotal() >= MIN_COMPOST) || !inventory.getStackInSlot(SLOT_COMPOST).isEmpty();
    }

    public void reset()
    {
        green = brown = fish = bones = pottery = charcoal = 0;
        rotten = false;
        resetCounter();
    }

    public void calculateRotten()
    {
        final float bonePct = bones / (float) MAX_COMPOST;
        final float fishPct = fish / (float) MAX_COMPOST;
        final float charcoalPct = charcoal / (float) MAX_COMPOST;
        final float potterPct = pottery / (float) MAX_COMPOST;
        final int greenBrownDiff = Mth.abs(green - brown);
        if (bonePct > 0.15f || fishPct > 0.15f || charcoalPct > 0.2f || potterPct > 0.15f || greenBrownDiff > 10)
        {
            rotten = true;
        }
    }

    public long getReadyTicks()
    {
        assert level != null;
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        cursor.set(getBlockPos());
        final float rainfall = Climate.getRainfall(level, cursor);
        long readyTicks = FLConfig.SERVER.compostTumblerTicks.get();
        if (rainfall < 150f) // inverted trapezoid wave
        {
            readyTicks *= (long) ((150f - rainfall) / 50f + 1f);
        }
        else if (rainfall > 350f)
        {
            readyTicks *= (long) ((rainfall - 350f) / 50f + 1f);
        }
        final long originalReadyTicks = readyTicks;

        final int greenBrownDifference = Mth.abs(green - brown);
        readyTicks *= Mth.clampedMap(greenBrownDifference, 0, 5, 0.8f, 1.2f);
        readyTicks *= 0.1f * fish + 1f;
        readyTicks *= 0.1f * bones + 1f;
        readyTicks *= 0.1f * pottery + 0.7f;
        readyTicks *= 0.1f * charcoal + 0.7f;

        if (readyTicks > (originalReadyTicks * 3))
        {
            readyTicks = originalReadyTicks;
        }

        return readyTicks;
    }

    public Compost getCompost(ItemStack stack)
    {
        if (Helpers.isItem(stack, Items.BONE))
        {
            return new Compost(AdditionType.BONE, 1);
        }
        if (Helpers.isItem(stack, ItemTags.FISHES))
        {
            return new Compost(AdditionType.FISH, 1);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_POISONS))
        {
            return new Compost(AdditionType.POISON, 0);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_LOW))
        {
            return new Compost(AdditionType.BROWN, 1);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS))
        {
            return new Compost(AdditionType.BROWN, 2);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_BROWNS_HIGH))
        {
            return new Compost(AdditionType.BROWN, 4);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_LOW))
        {
            return new Compost(AdditionType.GREEN, 1);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS))
        {
            return new Compost(AdditionType.GREEN, 2);
        }
        if (Helpers.isItem(stack, TFCTags.Items.COMPOST_GREENS_HIGH))
        {
            return new Compost(AdditionType.GREEN, 4);
        }
        if (Helpers.isItem(stack, FLItems.POTTERY_SHERD.get()) || Helpers.isItem(stack, ItemTags.DECORATED_POT_SHERDS))
        {
            return new Compost(AdditionType.POTTERY, 1);
        }
        if (Helpers.isItem(stack, Items.CHARCOAL))
        {
            return new Compost(AdditionType.CHARCOAL, 1);
        }
        return new Compost(AdditionType.NONE, 0);
    }

    public void resetCounter()
    {
        lastUpdateTick = Calendars.SERVER.getTicks();
        setChanged();
    }

    public long getTicksSinceUpdate()
    {
        assert level != null;
        return Calendars.get(level).getTicks() - lastUpdateTick;
    }

    @Override
    public Node getRotationNode()
    {
        return node;
    }

    @Override
    protected void onLoadAdditional()
    {
        performNetworkAction(NetworkAction.ADD);
    }

    @Override
    protected void onUnloadAdditional()
    {
        performNetworkAction(NetworkAction.REMOVE);
    }

    public float getPercentage(AdditionType type)
    {
        final int total = getTotal();
        if (total < 1)
            return 0f;
        return switch (type)
        {
            case GREEN -> green;
            case BROWN -> brown;
            case FISH -> fish;
            case BONE -> bones;
            case POTTERY -> pottery;
            case CHARCOAL -> charcoal;
            default -> 0f;
        } / (float) total;
    }

    public record Compost(AdditionType type, int amount)
    {
        public boolean isEmpty()
        {
            return type == AdditionType.NONE || amount < 1;
        }
    }

    public enum AdditionType
    {
        NONE,
        POISON,
        GREEN,
        BROWN,
        FISH,
        BONE,
        POTTERY,
        CHARCOAL;

        public static final AdditionType[] VALUES = values();
    }
}
