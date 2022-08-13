package racoonman.r3d.render.api.objects;

public interface IMappedMemoryRegion {
	IDeviceBuffer allocate(long size);
}
