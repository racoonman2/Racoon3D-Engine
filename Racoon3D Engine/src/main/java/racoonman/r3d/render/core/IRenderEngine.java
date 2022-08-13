package racoonman.r3d.render.core;

import racoonman.r3d.core.util.Version;

public interface IRenderEngine {
	IRenderEngine R3D = of("R3D", new Version(1, 0, 0));

	String getName();
	
	Version getVersion();
	
	public static IRenderEngine of(String name, Version version) {
		return new IRenderEngine() {
			
			@Override
			public String getName() {
				return name;
			}
			
			@Override
			public Version getVersion() {
				return version;
			}
		};
	}
}
