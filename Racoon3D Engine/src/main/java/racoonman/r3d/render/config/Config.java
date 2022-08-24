package racoonman.r3d.render.config;

import racoonman.r3d.core.R3DRuntime;
import racoonman.r3d.render.core.IRenderAPI;
import racoonman.r3d.util.Bytes;

public class Config {
	public static final int FRAME_COUNT = R3DRuntime.getIntOr("frame_count", 3);
	public static final int MATRIX_STACK_SIZE = R3DRuntime.getIntOr("matrix_stack_size", 200);
	public static final boolean CACHE_PIPELINES = R3DRuntime.getBoolOr("cache_pipelines", false);
	public static final long MAPPED_REGION_SIZE = R3DRuntime.getIntOr("mapped_region_size", Bytes.mb(10));
	public static final String RENDER_API = R3DRuntime.getStringOr("r3d.renderApi", IRenderAPI.VULKAN.getName());
}
 