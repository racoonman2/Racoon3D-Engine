package racoonman.r3d.render.util;

import racoonman.r3d.util.math.Mathi;

public record Color(int red, int green, int blue, int alpha) {
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Color) {
			Color other = (Color) obj;
			return this.red   == other.red   && 
				   this.green == other.green && 
				   this.blue  == other.blue  && 
				   this.alpha == other.alpha;
		} else {
			return false;
		}
	}
	
	public static float normalize(int value) {
		return Mathi.clamp(0, 255, value) / 255.0F;
	}
	
	public static Color rgb(int r, int g, int b) {
		return new Color(r, g, b, 255);
	}
	
	public static Color rgba(int r, int g, int b, int a) {
		return new Color(r, g, b, a);
	}
}
