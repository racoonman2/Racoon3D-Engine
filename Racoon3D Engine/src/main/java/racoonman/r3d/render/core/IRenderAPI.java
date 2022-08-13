package racoonman.r3d.render.core;

import racoonman.r3d.render.api.opengl.OpenGlRenderAPI;
import racoonman.r3d.render.api.vulkan.VkRenderAPI;

public interface IRenderAPI {
	IRenderAPI VULKAN = new VkRenderAPI();
	IRenderAPI OPENGL = new OpenGlRenderAPI();
	
	String getName();
	
	RenderService initService();
}
