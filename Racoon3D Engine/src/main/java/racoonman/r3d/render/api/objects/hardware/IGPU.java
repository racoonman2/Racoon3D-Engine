package racoonman.r3d.render.api.objects.hardware;

import racoonman.r3d.render.natives.IHandle;

public interface IGPU extends IHandle {
	GPUType getGpuType();
}
