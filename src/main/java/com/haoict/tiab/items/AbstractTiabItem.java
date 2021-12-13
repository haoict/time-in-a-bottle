package com.haoict.tiab.items;

import com.haoict.tiab.config.Constants;
import com.haoict.tiab.config.TiabConfig;
import com.haoict.tiab.entities.TimeAcceleratorEntity;
import com.haoict.tiab.utils.PlaySound;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class AbstractTiabItem extends Item {
    private static final int THIRTY_SECONDS = Constants.TICK_CONST * TiabConfig.COMMON.eachUseDuration.get();
    private static final String[] NOTES = {"C", "D", "E", "F", "G2", "A2", "B2", "C2", "D2", "E2", "F2"};

    public AbstractTiabItem() {
        super(new Item.Properties().tab(TiabCreativeTab.TAB).stacksTo(1));
    }

    @Override
    @Nonnull
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();

        if (level.isClientSide) {
            return InteractionResult.PASS;
        }

        BlockPos pos = context.getClickedPos();
        BlockState blockState = level.getBlockState(pos);
        BlockEntity targetTE = level.getBlockEntity(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (!blockState.isRandomlyTicking() && (targetTE == null || !(targetTE instanceof Tickable || targetTE instanceof BlockEntity))) {
            return InteractionResult.FAIL;
        }

        int nextRate = 1;
        int energyRequired = getEnergyCost(nextRate);
        boolean isCreativeMode = player != null && player.isCreative();

        Optional<TimeAcceleratorEntity> o = level.getEntitiesOfClass(TimeAcceleratorEntity.class, new AABB(pos)).stream().findFirst();

        if (o.isPresent()) {
            TimeAcceleratorEntity entityTA = o.get();
            int currentRate = entityTA.getTimeRate();
            int usedUpTime = THIRTY_SECONDS - entityTA.getRemainingTime();

            if (currentRate >= Math.pow(2, TiabConfig.COMMON.maxTimeRatePower.get() - 1)) {
                return InteractionResult.SUCCESS;
            }

            nextRate = currentRate * 2;
            int timeAdded = usedUpTime / 2;
            energyRequired = getEnergyCost(nextRate);

            if (!canUse(stack, isCreativeMode, energyRequired)) {
                return InteractionResult.SUCCESS;
            }

            entityTA.setTimeRate(nextRate);
            entityTA.setRemainingTime(entityTA.getRemainingTime() + timeAdded);
        } else {
            // First use
            if (!canUse(stack, isCreativeMode, energyRequired)) {
                return InteractionResult.SUCCESS;
            }

            TimeAcceleratorEntity entityTA = new TimeAcceleratorEntity(level, pos, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            entityTA.setRemainingTime(THIRTY_SECONDS);
            level.addFreshEntity(entityTA);
        }

        if (!isCreativeMode) {
            this.applyDamage(stack, energyRequired);
        }
        playSound(level, pos, nextRate);

        return InteractionResult.SUCCESS;
    }

    public int getEnergyCost(int timeRate) {
        if (timeRate <= 1) return THIRTY_SECONDS;
        return timeRate / 2 * THIRTY_SECONDS;
    }

    public boolean canUse(ItemStack stack, boolean isCreativeMode, int energyRequired) {
        return getStoredEnergy(stack) >= energyRequired || isCreativeMode;
    }

    protected abstract int getStoredEnergy(ItemStack stack);

    protected abstract void setStoredEnergy(ItemStack stack, int energy);

    protected abstract void applyDamage(ItemStack stack, int damage);

    public void playSound(Level level, BlockPos pos, int nextRate) {
        switch (nextRate) {
            case 1:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[0]);
                break;
            case 2:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[1]);
                break;
            case 4:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[2]);
                break;
            case 8:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[3]);
                break;
            case 16:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[4]);
                break;
            case 32:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[5]);
                break;
            case 64:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[6]);
                break;
            case 128:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[7]);
                break;
            case 256:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[8]);
                break;
            case 512:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[9]);
                break;
            default:
                PlaySound.playNoteBlockHarpSound(level, pos, NOTES[10]);
        }
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return !ItemStack.isSame(oldStack, newStack);
    }
}
