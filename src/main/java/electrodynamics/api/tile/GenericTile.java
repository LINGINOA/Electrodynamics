package electrodynamics.api.tile;

import java.util.EnumMap;

import electrodynamics.api.References;
import electrodynamics.api.tile.components.Component;
import electrodynamics.api.tile.components.ComponentType;
import electrodynamics.api.tile.components.type.ComponentName;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.IntArray;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class GenericTile extends TileEntity implements INameable {
    private EnumMap<ComponentType, Component> componentMap = new EnumMap<>(ComponentType.class);

    public boolean hasComponent(ComponentType type) {
	return componentMap.containsKey(type);
    }

    public <T extends Component> T getComponent(ComponentType type) {
	return !hasComponent(type) ? null : (T) componentMap.get(type);
    }

    public GenericTile addComponent(Component component) {
	component.setHolder(this);
	if (hasComponent(component.getType())) {
	    throw new ExceptionInInitializerError("Component of type: " + component.getType().name() + " already registered!");
	}
	componentMap.put(component.getType(), component);
	return this;
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
	super.read(state, compound);
	for (Component component : componentMap.values()) {
	    component.setHolder(this);
	    component.loadFromNBT(state, compound);
	}
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
	for (Component component : componentMap.values()) {
	    component.setHolder(this);
	    component.saveToNBT(compound);
	}
	return super.write(compound);
    }

    protected GenericTile(TileEntityType<?> tileEntityTypeIn) {
	super(tileEntityTypeIn);
    }

    @Override
    public ITextComponent getName() {
	return hasComponent(ComponentType.Name) ? this.<ComponentName>getComponent(ComponentType.Name).getName()
		: new StringTextComponent(References.ID + ".default.tile.name");
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
	for (Component component : componentMap.values()) {
	    component.setHolder(this);
	    if (component.hasCapability(cap, side)) {
		return component.getCapability(cap, side);
	    }
	}
	return super.getCapability(cap, side);
    }

    @Override
    public void remove() {
	super.remove();
	for (Component component : componentMap.values()) {
	    component.setHolder(this);
	    component.remove();
	}
    }

    public IntArray getCoordsArray() {
	IntArray array = new IntArray(3);
	array.set(0, pos.getX());
	array.set(1, pos.getY());
	array.set(2, pos.getZ());
	return array;
    }

    @Override
    public BlockPos getPos() {
	return pos;
    }

    @Override
    public double getMaxRenderDistanceSquared() {
	return 256;
    }
}