package electrodynamics.common.tile.pipelines.fluids;

import java.util.ArrayList;
import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Sets;

import electrodynamics.api.network.cable.type.IFluidPipe;
import electrodynamics.common.block.connect.util.EnumConnectType;
import electrodynamics.common.network.type.FluidNetwork;
import electrodynamics.common.network.utils.FluidUtilities;
import electrodynamics.prefab.network.AbstractNetwork;
import electrodynamics.prefab.tile.components.type.ComponentTickable;
import electrodynamics.prefab.tile.types.GenericConnectTile;
import electrodynamics.prefab.utilities.Scheduler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public abstract class GenericTileFluidPipe extends GenericConnectTile implements IFluidPipe {

	public FluidNetwork fluidNetwork;
	private ArrayList<IFluidHandler> handler = new ArrayList<>();

	@Override
	@Nonnull
	public <T> @NotNull LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (capability == ForgeCapabilities.FLUID_HANDLER) {
			return LazyOptional.of(() -> handler.get((facing == null ? Direction.UP : facing).ordinal())).cast();
		}
		return LazyOptional.empty();
	}

	@Override
	public AbstractNetwork<?, ?, ?, ?> getAbstractNetwork() {
		return fluidNetwork;
	}

	protected GenericTileFluidPipe(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		addComponent(new ComponentTickable(this));
		for (Direction dir : Direction.values()) {
			handler.add(new IFluidHandler() {

				@Override
				public int getTanks() {
					return 1;
				}

				@Override
				public FluidStack getFluidInTank(int tank) {
					return new FluidStack(Fluids.WATER, 0);
				}

				@Override
				public int getTankCapacity(int tank) {
					return 0;
				}

				@Override
				public boolean isFluidValid(int tank, FluidStack stack) {
					return stack != null;
				}

				@Override
				public int fill(FluidStack resource, FluidAction action) {
					if (action == FluidAction.SIMULATE || getNetwork() == null) {
						return 0;
					}
					ArrayList<BlockEntity> ignored = new ArrayList<>();
					ignored.add(level.getBlockEntity(new BlockPos(worldPosition).relative(dir)));
					return fluidNetwork.emit(resource, ignored, false).getAmount();
				}

				@Override
				public FluidStack drain(FluidStack resource, FluidAction action) {
					return FluidStack.EMPTY;
				}

				@Override
				public FluidStack drain(int maxDrain, FluidAction action) {
					return FluidStack.EMPTY;
				}
			});
		}
	}

	private HashSet<IFluidPipe> getConnectedConductors() {
		HashSet<IFluidPipe> set = new HashSet<>();
		for (Direction dir : Direction.values()) {
			BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
			if (facing instanceof IFluidPipe p) {
				set.add(p);
			}
		}
		return set;
	}

	@Override
	public FluidNetwork getNetwork() {
		return getNetwork(true);
	}

	@Override
	public FluidNetwork getNetwork(boolean createIfNull) {
		if (fluidNetwork == null && createIfNull) {
			HashSet<IFluidPipe> adjacentCables = getConnectedConductors();
			HashSet<FluidNetwork> connectedNets = new HashSet<>();
			for (IFluidPipe wire : adjacentCables) {
				if (wire.getNetwork(false) != null && wire.getNetwork() instanceof FluidNetwork f) {
					connectedNets.add(f);
				}
			}
			if (connectedNets.isEmpty()) {
				fluidNetwork = new FluidNetwork(Sets.newHashSet(this));
			} else {
				if (connectedNets.size() == 1) {
					fluidNetwork = (FluidNetwork) connectedNets.toArray()[0];
				} else {
					fluidNetwork = new FluidNetwork(connectedNets, false);
				}
				fluidNetwork.conductorSet.add(this);
			}
		}
		return fluidNetwork;
	}

	@Override
	public void setNetwork(AbstractNetwork<?, ?, ?, ?> network) {
		if (fluidNetwork != network && network instanceof FluidNetwork f) {
			removeFromNetwork();
			fluidNetwork = f;
		}
	}

	@Override
	public void refreshNetwork() {
		if (!level.isClientSide) {
			updateAdjacent();
			ArrayList<FluidNetwork> foundNetworks = new ArrayList<>();
			for (Direction dir : Direction.values()) {
				BlockEntity facing = level.getBlockEntity(new BlockPos(worldPosition).relative(dir));
				if (facing instanceof IFluidPipe p && p.getNetwork() instanceof FluidNetwork n) {
					foundNetworks.add(n);
				}
			}
			if (!foundNetworks.isEmpty()) {
				foundNetworks.get(0).conductorSet.add(this);
				fluidNetwork = foundNetworks.get(0);
				if (foundNetworks.size() > 1) {
					foundNetworks.remove(0);
					for (FluidNetwork network : foundNetworks) {
						getNetwork().merge(network);
					}
				}
			}
			getNetwork().refresh();
		}
	}

	@Override
	public void removeFromNetwork() {
		if (fluidNetwork != null) {
			fluidNetwork.removeFromNetwork(this);
		}
	}

	private boolean[] connections = new boolean[6];
	private BlockEntity[] tileConnections = new BlockEntity[6];

	public boolean updateAdjacent() {
		boolean flag = false;
		for (Direction dir : Direction.values()) {
			BlockEntity tile = level.getBlockEntity(worldPosition.relative(dir));
			boolean is = FluidUtilities.isFluidReceiver(tile, dir.getOpposite());
			if (connections[dir.ordinal()] != is) {
				connections[dir.ordinal()] = is;
				tileConnections[dir.ordinal()] = tile;
				flag = true;
			}

		}
		return flag;
	}

	@Override
	public BlockEntity[] getAdjacentConnections() {
		return tileConnections;
	}

	@Override
	public void refreshNetworkIfChange() {
		if (updateAdjacent()) {
			refreshNetwork();
		}
	}

	@Override
	public void destroyViolently() {
	}

	@Override
	public void setRemoved() {
		if (!level.isClientSide && fluidNetwork != null) {
			getNetwork().split(this);
		}
		super.setRemoved();
	}

	@Override
	public void onChunkUnloaded() {
		if (!level.isClientSide && fluidNetwork != null) {
			getNetwork().split(this);
		}
	}

	@Override
	public void onLoad() {
		super.onLoad();
		// TODO remove in next version release; this is a temp hack for now
		if (super.connections.get() == 0) {
			for (Direction dir : Direction.values()) {
				EnumConnectType connection = EnumConnectType.NONE;
				BlockEntity otherTile = level.getBlockEntity(getBlockPos().relative(dir));
				if (otherTile instanceof IFluidPipe) {
					connection = EnumConnectType.WIRE;
				} else if (FluidUtilities.isFluidReceiver(otherTile, dir.getOpposite())) {
					connection = EnumConnectType.INVENTORY;
				}
				writeConnection(dir, connection);
			}
		}
		// end temp hack
		Scheduler.schedule(1, this::refreshNetwork);
	}
}
