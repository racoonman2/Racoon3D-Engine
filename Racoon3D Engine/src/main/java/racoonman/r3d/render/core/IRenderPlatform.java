package racoonman.r3d.render.core;

import racoonman.r3d.core.R3DRuntime;
import racoonman.r3d.core.util.Version;

public interface IRenderPlatform {
	IRenderPlatform R3D = new IRenderPlatform() {
		public static final Version VERSION = new Version(1, 3, 0);
		
		@Override
		public IRenderEngine getEngine() {
			return IRenderEngine.R3D;
		}
		
		@Override
		public String getAppName() {
			return R3DRuntime.getClient().name();
		}
		
		@Override
		public Version getApiVersion() {
			return VERSION;
		}
	};
	
	Version getApiVersion();
	
	String getAppName();
	
	IRenderEngine getEngine();
	
	default Version getEngineVersion() {
		return this.getEngine().getVersion();
	}
	
	default String getEngineName() {
		return this.getEngine().getName();
	}
	
	public static IRenderPlatform of(Version version, String appName, IRenderEngine engine) {
		return new IRenderPlatform() {
			
			@Override
			public IRenderEngine getEngine() {
				return engine;
			}
			
			@Override
			public String getAppName() {
				return appName;
			}
			
			@Override
			public Version getApiVersion() {
				return version;
			}
		};
	}
}
