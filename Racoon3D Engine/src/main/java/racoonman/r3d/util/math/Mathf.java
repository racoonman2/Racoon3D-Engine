package racoonman.r3d.util.math;

import org.joml.Vector3f;

public class Mathf {
	public static float PI = (float) Math.PI;

	public static Vector3f tangent(Vector3f normal) {
		 Vector3f t1 = normal.cross(0.0F, 0.0F, 1.0F, new Vector3f());
		 Vector3f t2 = normal.cross(0.0F, 1.0F, 0.0F, new Vector3f());
		 
		 return t1.length() > t2.length() ? t1 : t2;
	}
	
	public static float sin(double f) {
		return (float) Math.sin(f);
	}
	
	public static float cos(double f) {
		return (float) Math.cos(f);
	}
	
	public static float sin(float f) {
		return (float) Math.sin(f);
	}
	
	public static float cos(float f) {
		return (float) Math.cos(f);
	}
	
	public static float log2(int n) {
        return (float) (Math.log(n) / Math.log(2));
    }
	
	public static float dist(float x1, float z1, float x2, float z2) {
        float dx = x1 - x2;
        float dz = z1 - z2;
        return (float) Math.sqrt(Math.fma(dx, dx, Math.fma(0, 0, dz * dz)));
	}

	public static int floor(double d) {
		int i = (int) d;
		return d < (double) i ? i - 1 : i;		
	}
	
	public static int floor(float f) {
		int i = (int)f;
		return f < (float) i ? i - 1 : i;
	}
	
	public static float dist(float f1, float f2) {
		return Math.abs(f2 - f1);
	}

	public static float toRadians(float deg) {
		return (float) Math.toRadians(deg);
	}
	
	public static float toRadians(double deg) {
		return (float) Math.toRadians(deg);
	}
	
	public static float clamp(float min, float max, float value) {
		return Math.max(min, Math.min(max, value));
	}
	
	public static double clamp(double min, double max, double value) {
		return Math.max(min, Math.min(max, value));
	}

	public static float lerp(float time, float to, float from) {
		return Math.fma(to - from, time, from);
	}
	
	public static float sum(float...fs) {
		float res = 0.0F;
		for(float f : fs)
			res += f;
		return res;
	}
	
	public static float average(float...fs) {
		return sum(fs) / fs.length;
	}
}
