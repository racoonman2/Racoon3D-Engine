package racoonman.r3d.render.api.types;

import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_INSTANCE;
import static org.lwjgl.vulkan.VK10.VK_VERTEX_INPUT_RATE_VERTEX;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum InputRate implements IVkType {
	VERTEX(VK_VERTEX_INPUT_RATE_VERTEX),
	INSTANCE(VK_VERTEX_INPUT_RATE_INSTANCE);

	public static final ICodec<InputRate> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<InputRate> NAME_CODEC = EnumCodec.byName(InputRate::valueOf);
	
	private int vkInputRate;
	
	private InputRate(int vkInputRate) {
		this.vkInputRate = vkInputRate;
	}

	@Override
	public int getVkType() {
		return this.vkInputRate;
	}
}
