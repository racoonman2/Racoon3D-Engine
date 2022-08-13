package racoonman.r3d.render.api.objects;

import racoonman.r3d.render.api.vulkan.types.AddressMode;
import racoonman.r3d.render.api.vulkan.types.BorderColor;
import racoonman.r3d.render.api.vulkan.types.CompareOp;
import racoonman.r3d.render.api.vulkan.types.Filter;
import racoonman.r3d.render.api.vulkan.types.MipmapMode;

public class TextureState {
	public static final TextureState DEFAULT = new TextureState(Filter.NEAREST, Filter.LINEAR, AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, AddressMode.CLAMP_TO_EDGE, BorderColor.INT_TRANSPARENT_BLACK, false, false, CompareOp.LESS, MipmapMode.LINEAR, 0.0F, 1.0F, 0.0F, false, 1.0F);
	private Filter magFilter; 
	private Filter minFilter; 
	private AddressMode addressModeU; 
	private AddressMode addressModeV; 
	private AddressMode addressModeW; 
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

	public TextureState(Filter magFilter, Filter minFilter, AddressMode addressModeU, AddressMode addressModeV, AddressMode addressModeW, BorderColor borderColor, boolean unnormalizedCoords, boolean compareEnable, CompareOp compareOp, MipmapMode mipmapMode, float minLod, float maxLod, float mipLodBias, boolean anisotropyEnable, float maxAnisotropy) {
		this.magFilter = magFilter;
		this.minFilter = minFilter;
		this.addressModeU = addressModeU;
		this.addressModeV = addressModeV;
		this.addressModeW = addressModeW;
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
	}
	
	public Filter magFilter() {
		return this.magFilter;
	}
	
	public Filter minFilter() {
		return this.minFilter;
	}
	
	public AddressMode addressModeU() {
		return this.addressModeU;
	}
	
	public AddressMode addressModeV() {
		return this.addressModeV;
	}
	
	public AddressMode addressModeW() {
		return this.addressModeW;
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
	
	public void magFilter(Filter filter) {
		this.magFilter = filter;
	}
	
	public void minFilter(Filter filter) {
		this.minFilter = filter;
	}
	
	public void addressModeU(AddressMode mode) {
		this.addressModeU = mode;
	}
	
	public void addressModeV(AddressMode mode) {
		this.addressModeV = mode;
	}
	
	public void addressModeW(AddressMode mode) {
		this.addressModeW = mode;
	}
	
	public void borderColor(BorderColor color) {
		this.borderColor = color;
	}
	
	public void unnormalizedCoords(boolean unnormalizedCoords) {
		this.unnormalizedCoords = unnormalizedCoords;
	}
	
	public void compareEnable(boolean compareEnable) {
		this.compareEnable = compareEnable;
	}
	
	public void compareOp(CompareOp compareOp) {
		this.compareOp = compareOp;
	}
	
	public void mipmapMode(MipmapMode mode) {
		this.mipmapMode = mode;
	}
	
	public void minLod(float minLod) {
		this.minLod = minLod;
	}
	
	public void maxLod(float maxLod) {
		this.maxLod = maxLod;
	}
	
	public void mipLodBias(float mipLodBias) {
		this.mipLodBias = mipLodBias;
	}
	
	public void anisotropyEnable(boolean enable) {
		this.anisotropyEnable = enable;
	}
	
	public void maxAnisotropy(float anisotropy) {
		this.maxAnisotropy = anisotropy;
	}
	
	public TextureState copy() {
		return new TextureState(this.magFilter, this.minFilter, this.addressModeU, this.addressModeV, this.addressModeW, this.borderColor, this.unnormalizedCoords, this.compareEnable, this.compareOp, this.mipmapMode, this.minLod, this.maxLod, this.mipLodBias, this.anisotropyEnable, this.maxAnisotropy);
	}
	
	@Override
	public String toString() {
		return new StringBuilder("TextureState[")
			.append("magFilter=").append(this.magFilter).append(",")
			.append("minFilter=").append(this.minFilter).append(",")
			.append("addressModeU=").append(this.addressModeU).append(",")
			.append("addressModeV=").append(this.addressModeV).append(",")
			.append("addressModeW=").append(this.addressModeW).append(",")
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
