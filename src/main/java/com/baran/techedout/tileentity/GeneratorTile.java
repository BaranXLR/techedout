package com.baran.techedout.tileentity;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.common.ForgeHooks;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;


import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.baran.techedout.init.Config;
import com.baran.techedout.tools.CustomEnergyStorage;

import static com.baran.techedout.init.Registration.GENERATOR_TILE;

import java.util.concurrent.atomic.AtomicInteger;

public class GeneratorTile extends TileEntity implements ITickableTileEntity{
	
    private ItemStackHandler itemHandler = createHandler();
    private CustomEnergyStorage energyStorage = createEnergy();
    
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);
    
    private int counter = 0;
    private int lastBurnTime = 0;
	
	public GeneratorTile() {
		super(GENERATOR_TILE.get());
	}
	
    public void remove() {
        super.remove();
        handler.invalidate();
        energy.invalidate();
    }
	
	public void tick() {
		if(world.isRemote)
			return;
		if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
			if (counter > 0) {
				counter--;
				energyStorage.addEnergy(Config.GENERATOR_GENERATE.get());
				markDirty();
			}
			if (counter <= 0) {
				ItemStack stack = itemHandler.getStackInSlot(0);
				if (ForgeHooks.getBurnTime(stack) > 0) {
					counter = ForgeHooks.getBurnTime(stack);
					lastBurnTime = counter;
					if(stack.getItem() == Items.LAVA_BUCKET) {
						itemHandler.setStackInSlot(0, new ItemStack(Items.BUCKET));
					} else {
						itemHandler.extractItem(0, 1, false);
					}
				}
				markDirty();
			}
		}
		BlockState blockState = world.getBlockState(pos);
		if(blockState.get(BlockStateProperties.POWERED) != counter > 0) {
			world.setBlockState(pos,blockState.with(BlockStateProperties.POWERED, counter > 0),
					Constants.BlockFlags.NOTIFY_NEIGHBORS + Constants.BlockFlags.BLOCK_UPDATE);
		}
		sendOutPower();
	}

	
    private void sendOutPower() {
        AtomicInteger capacity = new AtomicInteger(energyStorage.getEnergyStored());
        if (capacity.get() > 0) {
            for (Direction direction : Direction.values()) {
                TileEntity te = world.getTileEntity(pos.offset(direction));
                if (te != null) {
                    boolean doContinue = te.getCapability(CapabilityEnergy.ENERGY, direction).map(handler -> {
                                if (handler.canReceive()) {
                                    int received = handler.receiveEnergy(Math.min(capacity.get(), Config.GENERATOR_SEND.get()), false);
                                    capacity.addAndGet(-received);
                                    energyStorage.consumeEnergy(received);
                                    markDirty();
                                    return capacity.get() > 0;
                                } else {
                                    return true;
                                }
                            }
                    ).orElse(true);
                    if (!doContinue) {
                        return;
                    }
                }
            }
        }
    }
    
    public final IntArray fields = new IntArray(4) {
        public int get(int index) {
            switch (index) {
                case 0:
                	return GeneratorTile.this.energyStorage.getEnergyStored();
                case 1:
                	return GeneratorTile.this.energyStorage.getMaxEnergyStored();
            	case 2:
            		return GeneratorTile.this.counter;
                case 3:
                	return GeneratorTile.this.lastBurnTime;
                default:
                    return 0;
            }
        }

        public void set(int index, int value) {
            switch (index) {
            	case 0:
            		GeneratorTile.this.energyStorage.setEnergy(value);
            	case 1:
            		return;
            	case 2:
            		GeneratorTile.this.counter = value;
                case 3:
                	GeneratorTile.this.lastBurnTime = value;
                default:
                	return;
                	
            }

        }

        public int size() {
            return 4;
        }
    };
	
    public void read(BlockState state, CompoundNBT tag) {
        itemHandler.deserializeNBT(tag.getCompound("inv"));
        energyStorage.deserializeNBT(tag.getCompound("energy"));
        counter = tag.getInt("counter");
        lastBurnTime = tag.getInt("lastBurnTime");
        super.read(state, tag);
    }
    
    public CompoundNBT write(CompoundNBT tag) {
        tag.put("inv", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        tag.putInt("counter", counter);
        tag.putInt("lastBurnTime", lastBurnTime);
        return super.write(tag);
    }
	
    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                // To make sure the TE persists when the chunk is saved later we need to
                // mark it dirty every time the item handler changes
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return ForgeHooks.getBurnTime(stack) > 0;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (ForgeHooks.getBurnTime(stack) <= 0) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }
    
    private CustomEnergyStorage createEnergy() {
        return new CustomEnergyStorage(Config.GENERATOR_MAXPOWER.get(), 0) {
            @Override
            protected void onEnergyChanged() {
                markDirty();
            }
        };
    }
    
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }
	
}
