package com.haoict.tiab.capabilities;

import net.minecraftforge.energy.IEnergyStorage;

public abstract class AbstractEnergyStorage implements IEnergyStorage {
    private final int capacity;
    private final int maxOutput;
    private final int maxInput;
    protected int energy;

    public AbstractEnergyStorage(int capacity, int maxInput, int maxOutput) {
        this.capacity = capacity;
        this.maxInput = maxInput;
        this.maxOutput = maxOutput;
        this.energy = 0;
    }

    public AbstractEnergyStorage(int capacity, int maxIO) {
        this(capacity, maxIO, maxIO);
    }

    @Override
    public int getEnergyStored() {
        updateEnergyFromNBT();
        return getEnergyStoredCache();
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (maxReceive < 0) return 0;
        int energyReceived = evaluateEnergyReceived(maxReceive);
        if (!simulate) {
            setEnergy(energyReceived + getEnergyStored());
            writeEnergyToNBT();
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (maxExtract < 0) return 0;
        int energyExtracted = evaluateEnergyExtracted(maxExtract);
        if (!simulate) {
            setEnergy(getEnergyStored() - energyExtracted);
            writeEnergyToNBT();
        }
        return energyExtracted;
    }

    /**
     * You may not want to use extractEnergy(int, boolean) internally and override it to return 0.
     * Use this method instead to stops the items from being used like batteries.
     */
    public int consumeEnergy(int maxExtract, boolean simulate) {
        if (maxExtract < 0) return 0;

        int energyExtracted = evaluateEnergyExtracted(maxExtract);
        if (!simulate) {
            setEnergy(getEnergyStored() - energyExtracted);
            writeEnergyToNBT();
        }
        return energyExtracted;
    }

    /**
     * Returns if this storage can have energy extracted.
     * If this is false, then any calls to extractEnergy will return 0.
     */
    @Override
    public boolean canExtract() {
        return maxOutput > 0;
    }

    /**
     * Used to determine if this storage can receive energy.
     * If this is false, then any calls to receiveEnergy will return 0.
     */
    @Override
    public boolean canReceive() {
        return maxInput > 0;
    }

    protected int getMaxOutput() {
        return maxOutput;
    }

    protected int getMaxInput() {
        return maxInput;
    }

    protected int evaluateEnergyExtracted(int maxExtract) {
        return Math.min(getEnergyStored(), Math.min(maxExtract, getMaxOutput()));
    }

    protected int evaluateEnergyReceived(int maxReceive) {
        return Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(maxReceive, getMaxInput()));
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    protected int getEnergyStoredCache() {
        return energy;
    }

    protected abstract void writeEnergyToNBT();

    protected abstract void updateEnergyFromNBT();

    protected void updateMaxEnergy() {
        this.energy = Math.min(getMaxEnergyStored(), energy);
    }
}
