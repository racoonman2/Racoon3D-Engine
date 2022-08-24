package racoonman.r3d.render.core;

import racoonman.r3d.render.api.vulkan.VkRenderAPI;

public interface IRenderAPI {
	IRenderAPI VULKAN = new VkRenderAPI();
	
	String getName();
	
	Service initService();
}
