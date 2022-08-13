package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO;

import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.render.vertex.VertexFormat.Attribute;
import racoonman.r3d.resource.codec.ArrayCodec;
import racoonman.r3d.resource.codec.ICodec;

//TODO remove this class
public class VertexInfo {
	public static final ICodec<VertexInfo> CODEC = ICodec.simple(
		ArrayCodec.of(VertexFormat.CODEC, VertexFormat[]::new).fetch("formats", VertexInfo::getFormats),
		VertexInfo::new
	);
	
	private VertexFormat[] formats;
	private VkVertexInputAttributeDescription.Buffer attributes;
	private VkVertexInputBindingDescription.Buffer bindings;
	private VkPipelineVertexInputStateCreateInfo info;
	
	public VertexInfo(VertexFormat...formats) {
		this.formats = formats;

		this.bindings = VkVertexInputBindingDescription.calloc(formats.length);
		
		int attribCount = 0;
		
		for(VertexFormat format : formats) {
			attribCount += format.getAttributeCount();
		}
		
		this.attributes = VkVertexInputAttributeDescription.calloc(attribCount);
		
		int location = 0;
		
		for(int i = 0; i < formats.length; i++) {
			VertexFormat format = formats[i];
			
			this.bindings.get(i)
				.binding(i)
				.stride(format.getStride())
				.inputRate(format.getInputRate().getVkType());
			
			int offset = 0;
			
			for(Attribute att : format.getAttributes()) {
				this.attributes.get(location)
					.binding(i)
					.location(location)
					.format(att.format().getVkType())
					.offset(offset);
				offset += att.size();
				location++;
			}
		}
		
		this.info = VkPipelineVertexInputStateCreateInfo.calloc();
		this.info.sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO)
			.pVertexBindingDescriptions(this.bindings)
			.pVertexAttributeDescriptions(this.attributes);
	}

	public VertexFormat[] getFormats() {
		return this.formats;
	}
	
	public VkPipelineVertexInputStateCreateInfo getCreateInfo() {
		return this.info;
	}
	
	public void free() {
		this.bindings.free();
		this.attributes.free();
		this.info.free();
	}
}
