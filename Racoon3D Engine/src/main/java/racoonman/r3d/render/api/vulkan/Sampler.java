package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateSampler;
import static org.lwjgl.vulkan.VK10.vkDestroySampler;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkSamplerCreateInfo;

import racoonman.r3d.render.api.vulkan.types.AddressMode;
import racoonman.r3d.render.api.vulkan.types.BorderColor;
import racoonman.r3d.render.api.vulkan.types.CompareOp;
import racoonman.r3d.render.api.vulkan.types.Filter;
import racoonman.r3d.render.api.vulkan.types.MipmapMode;
import racoonman.r3d.render.natives.IHandle;

class Sampler implements IHandle {
	private Device device;
	private Filter magFilter;
	private Filter minFilter;
	private AddressMode u;
	private AddressMode v;
	private AddressMode w;
	private BorderColor borderColor;
	private boolean unnormalizedCoords;
	private boolean compareEnable;
	private CompareOp compareOp;
	private MipmapMode mipmapMode;
	private float minLod;
	private float maxLod;
	private float mipLodBias;
	private boolean anisotropyEnable;
	private float maxAnisotropy;
	private long handle;
	
	public Sampler(Device device, Filter magFilter, Filter minFilter, AddressMode addressModeU, AddressMode addressModeV, AddressMode addressModeW, BorderColor borderColor, boolean unnormalizedCoords, 
			boolean compareEnable, CompareOp compareOp, MipmapMode mipmapMode, float minLod, float maxLod, float mipLodBias, boolean anisotropyEnable, float maxAnisotropy) {
		try(MemoryStack stack = stackPush()) {
			this.device = device;
			this.magFilter = magFilter;
			this.minFilter = minFilter;
			this.u = addressModeU;
			this.v = addressModeV;
			this.w = addressModeW;
			this.borderColor = borderColor;
			this.unnormalizedCoords = unnormalizedCoords;
			this.compareEnable = compareEnable;
			this.compareOp = compareOp;
			this.mipmapMode = mipmapMode;
			this.minLod = minLod;
			this.maxLod = maxLod;
			this.mipLodBias = mipLodBias;
			this.anisotropyEnable = anisotropyEnable;
			this.maxAnisotropy = maxAnisotropy;
			
			VkSamplerCreateInfo info = VkSamplerCreateInfo.calloc(stack)
				.sType(VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
				.magFilter(magFilter.getVkType())
				.minFilter(minFilter.getVkType())
				.addressModeU(addressModeU.getVkType())
				.addressModeV(addressModeV.getVkType())
				.addressModeW(addressModeW.getVkType())
				.borderColor(borderColor.getVkType())
				.unnormalizedCoordinates(unnormalizedCoords)
				.compareEnable(compareEnable)
				.compareOp(compareOp.getVkType())
				.mipmapMode(mipmapMode.getVkType())
				.minLod(minLod)
				.maxLod(maxLod)
				.mipLodBias(mipLodBias)
				.anisotropyEnable(device.getFeatures().features().samplerAnisotropy() && anisotropyEnable)
				.maxAnisotropy(maxAnisotropy);
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateSampler(device.get(), info, null, pointer), "Error creating sampler");
			this.handle = pointer.get(0);
		}
	}
	
	public Filter magFilter() {
		return this.magFilter;
	}
	
	public Filter minFilter() {
		return this.minFilter;
	}
	
	public AddressMode addressModeU() {
		return this.u;
	}
	
	public AddressMode addressModeV() {
		return this.v;
	}
	
	public AddressMode addressModeW() {
		return this.w;
	}
	
	public BorderColor borderColor() {
		return this.borderColor;
	}
	
	public boolean unnormalizedCoords() {
		return this.unnormalizedCoords;
	}
	
	public boolean compareEnable() {
		return this.compareEnable;
	}
	
	public CompareOp compareOp() {
		return this.compareOp;
	}
	
	public MipmapMode mipmapMode() {
		return this.mipmapMode;
	}
	
	public float minLod() {
		return this.minLod;
	}
	
	public float maxLod() {
		return this.maxLod;
	}
	
	public float mipLodBias() {
		return this.mipLodBias;
	}
	
	public boolean anisotropyEnable() {
		return this.anisotropyEnable;
	}
	
	public float maxAnisotropy() {
		return this.maxAnisotropy;
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}
	
	@Override
	public void free() {
		vkDestroySampler(this.device.get(), this.handle, null);
	}

	@Override
	public String toString() {
		return new StringBuilder("Sampler[")
			.append("magFilter=").append(this.magFilter).append(",")
			.append("minFilter=").append(this.minFilter).append(",")
			.append("addressModeU=").append(this.u).append(",")
			.append("addressModeV=").append(this.v).append(",")
			.append("addressModeW=").append(this.w).append(",")
			.append("borderColor=").append(this.borderColor).append(",")
			.append("unnormalizedCoords=").append(this.unnormalizedCoords).append(",")
			.append("compareEnable=").append(this.compareEnable).append(",")
			.append("compareOp=").append(this.compareOp).append(",")
			.append("mipmapMode").append(this.mipmapMode).append(",")
			.append("minLod=").append(this.minLod).append(",")
			.append("maxLod=").append(this.maxLod).append(",")
			.append("mipLodBias=").append(this.mipLodBias).append(",")
			.append("anisotropyEnable=").append(this.anisotropyEnable).append(",")
			.append("maxAnisotropy=").append(this.maxAnisotropy)
			.append("]")
			.toString();
	}
}
