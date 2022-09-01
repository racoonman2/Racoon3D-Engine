package racoonman.r3d.render.api.vulkan;

import java.util.Comparator;

import racoonman.r3d.render.api.objects.IShader;

public record ComputeState(IShader shader) {
	public static final Comparator<ComputeState> COMPARATOR = (o1, o2) ->
		o1.shader() == o2.shader() ? 1 : 
		o1.shader().equals(o2.shader()) ? 0 
		: -1;
}
