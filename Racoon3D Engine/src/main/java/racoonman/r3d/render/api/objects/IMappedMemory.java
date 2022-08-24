package racoonman.r3d.render.api.objects;

public interface IMappedMemory {
	IDeviceBuffer allocate(long size);
}
