package racoonman.r3d.render.memory;

import java.util.ArrayList;
import java.util.List;

import racoonman.r3d.render.api.objects.IDeviceBuffer;
import racoonman.r3d.render.api.types.BufferUsage;
import racoonman.r3d.render.api.types.Property;
import racoonman.r3d.render.core.Service;
import racoonman.r3d.render.core.Driver;

public class Allocation {
	private List<BufferUsage> usage;
	private List<Property> properties;
	private long size;
	
	private Allocation() {
		this.usage = new ArrayList<>();
		this.properties = new ArrayList<>();
	}
	
	public Allocation withSize(long size) {
		this.size = size;
		return this;
	}
	
	public Allocation withUsage(BufferUsage... usages) {
		for(BufferUsage usage : usages) {
			this.usage.add(usage);
		}
		return this;
	}
	
	public Allocation withProperties(Property... properties) {
		for(Property property : properties) {
			this.properties.add(property);
		}
		return this;
	}
	
	public IDeviceBuffer allocate() {
		return Driver.allocate(this.size, this.usage.toArray(BufferUsage[]::new), this.properties.toArray(Property[]::new));
	}
	
	public IDeviceBuffer allocate(Service service) {
		return service.allocate(this.size, this.usage.toArray(BufferUsage[]::new), this.properties.toArray(Property[]::new));
	}

	public static Allocation of() {
		return new Allocation();
	}
	
	public static Allocation ofSize(long size) {
		return new Allocation().withSize(size);
	}
}
