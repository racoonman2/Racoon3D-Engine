package racoonman.r3d.core.util;

import racoonman.r3d.resource.codec.ArrayCodec;
import racoonman.r3d.resource.codec.ICodec;

public class Version {
	public static final ICodec<Version> CODEC = ArrayCodec.INT.map((ints) -> {
		return new Version(
			getOr(ints, 0, 1),
			getOr(ints, 1, 0),
			getOr(ints, 2, 0)
		);
	}, (version) -> {
		return new int[] {
			version.getMajor(),
			version.getMinor(),
			version.getPatch()
		};
	});
	
	private int major;
	private int minor;
	private int patch;
	
	public Version(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}
	
	public int getMinor() {
		return this.minor;
	}
	
	public int getMajor() {
		return this.major;
	}
	
	public int getPatch() {
		return this.patch;
	}
	
	public int pack() {
		return (this.major << 22) | (this.minor << 12) | this.patch;
	}
	
	@Override
	public String toString() {
		return this.major + "." + this.minor;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Version version) {
			return this.major == version.getMajor() && this.minor == version.getMinor() && this.patch == version.getPatch();
		} else {
			return false;
		}
	}
	
	private static int getOr(int[] ints, int index, int fallback) {
		return index >= 0 && index < ints.length ? ints[index] : fallback;
	}
}
