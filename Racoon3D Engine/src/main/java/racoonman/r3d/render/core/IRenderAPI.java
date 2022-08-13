package racoonman.r3d.render.core;

import racoonman.r3d.render.api.gl.GlRenderAPI;
import racoonman.r3d.render.api.vulkan.VkRenderAPI;

public interface IRenderAPI {
	IRenderAPI VULKAN = new VkRenderAPI();
	IRenderAPI OPENGL = new GlRenderAPI();
	
	String getName();
	
	RenderService initService();
}
