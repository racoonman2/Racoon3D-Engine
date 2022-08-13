package racoonman.r3d.render.api.opengl;

import racoonman.r3d.render.core.IRenderAPI;
import racoonman.r3d.render.core.RenderService;

public class OpenGlRenderAPI implements IRenderAPI {

	@Override
	public String getName() {
		return "opengl";
	}

	@Override
	public RenderService initService() {
		throw new UnsupportedOperationException("OPENGL NOT SUPPORTED (yet)");
	}
}
