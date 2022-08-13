package racoonman.r3d.render.config;

//TODO actually implement this
public class Config {
	public static int defaultMatrixStackSize = 200; // maybe this is a little high
	public static boolean cachePipelinesBetweenSessions = true;
	public static long initialMappedRegionSize = 10000000L; // 10 megabytes
	public static double mouseSensitivity = 0.1D;
}
