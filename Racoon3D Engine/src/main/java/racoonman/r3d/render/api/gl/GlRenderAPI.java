package racoonman.r3d.render.api.gl;

import racoonman.r3d.render.core.IRenderAPI;
import racoonman.r3d.render.core.RenderService;

public class GlRenderAPI implements IRenderAPI {

	@Override
	public String getName() {
		return "opengl";
	}

	@Override
	public RenderService initService() {
		throw new UnsupportedOperationException("OPENGL NOT SUPPORTED (yet)");
	}
}
