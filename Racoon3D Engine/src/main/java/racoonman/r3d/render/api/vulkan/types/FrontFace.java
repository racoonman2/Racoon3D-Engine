package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.VK10.VK_FRONT_FACE_CLOCKWISE;
import static org.lwjgl.vulkan.VK10.VK_FRONT_FACE_COUNTER_CLOCKWISE;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum FrontFace implements IVkType {
	CCW(VK_FRONT_FACE_COUNTER_CLOCKWISE),
	CW(VK_FRONT_FACE_CLOCKWISE);

	public static final ICodec<FrontFace> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<FrontFace> NAME_CODEC = EnumCodec.byName(FrontFace::valueOf);
	
	private int vkType;
	
	private FrontFace(int vkType) {
		this.vkType = vkType;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
}
