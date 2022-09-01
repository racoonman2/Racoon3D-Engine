package racoonman.r3d.render.api.vulkan;

import org.lwjgl.system.NativeResource;
import org.lwjgl.system.Pointer;

interface IDispatchableHandle<T extends Pointer> extends NativeResource {
	T get();
}
