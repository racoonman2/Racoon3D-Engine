package racoonman.r3d.render.api.vulkan;

import java.util.List;

public record ValidationLayer(String name, ValidationLayer[]... children) {
	public static final ValidationLayer KHRONOS = new ValidationLayer("VK_LAYER_KHRONOS_validation");
	public static final ValidationLayer LUNARG_STANDARD = new ValidationLayer("VK_LAYER_LUNARG_standard_validation");
	
	
	public boolean isSupported(List<String> supportedLayers) {
		return supportedLayers.contains(this.name);
	}
}
