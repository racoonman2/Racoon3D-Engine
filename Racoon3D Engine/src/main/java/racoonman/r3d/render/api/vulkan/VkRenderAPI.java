package racoonman.r3d.render.api.vulkan;

import racoonman.r3d.core.R3DRuntime;
import racoonman.r3d.render.core.IRenderAPI;
import racoonman.r3d.render.core.IRenderPlatform;
import racoonman.r3d.render.core.RenderService;

public class VkRenderAPI implements IRenderAPI {

	@Override
	public String getName() {
		return "vulkan";
	}

	@Override
	public RenderService initService() {
		return new VkRenderService(IRenderPlatform.R3D, R3DRuntime.getBoolOr("r3d.debug", false));
	}
}
