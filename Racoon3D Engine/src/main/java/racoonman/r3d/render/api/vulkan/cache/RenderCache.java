package racoonman.r3d.render.api.vulkan.cache;

import static org.lwjgl.vulkan.VK13.*;
import static org.lwjgl.vulkan.KHRPushDescriptor.*;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import racoonman.r3d.render.api.objects.IAttachment;
import racoonman.r3d.render.api.vulkan.DescriptorSetLayout;
import racoonman.r3d.render.api.vulkan.DescriptorSetLayout.DescriptorBinding;
import racoonman.r3d.render.api.vulkan.Device;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.AssemblyInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.BlendAttachmentInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.BlendStateInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.DepthStencilInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.DynamicStateInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.MultisampleInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.RasterizationInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.RenderingInfo;
import racoonman.r3d.render.api.vulkan.GraphicsPipeline.ViewportInfo;
import racoonman.r3d.render.api.vulkan.IPipeline;
import racoonman.r3d.render.api.vulkan.PipelineLayout;
import racoonman.r3d.render.api.vulkan.VertexInfo;
import racoonman.r3d.render.api.vulkan.types.BlendFactor;
import racoonman.r3d.render.api.vulkan.types.BlendOp;
import racoonman.r3d.render.api.vulkan.types.CompareOp;
import racoonman.r3d.render.api.vulkan.types.DescriptorType;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.IVkType;
import racoonman.r3d.render.api.vulkan.types.LogicOp;
import racoonman.r3d.render.shader.ShaderStage;

public class RenderCache {
	private Device device;
	private Map<PipelineState, IPipeline> pipelines;
	
	public RenderCache(Device device) {
		this.device = device;
		this.pipelines = new TreeMap<>(PipelineState.COMPARATOR);
	}
	
	public IPipeline get(PipelineState state) {
		return this.pipelines.computeIfAbsent(state, (s) -> {
			return new GraphicsPipeline(this.device, 
				s.program(), 
				new VertexInfo(s.formats()),
				new AssemblyInfo(s.topology()),
				new ViewportInfo(1, 1),
				new RasterizationInfo(s.polygonMode(), s.cullMode(), s.lineWidth(), s.frontFace()), 
				new MultisampleInfo(s.sampleCount()),
				new BlendStateInfo[] {
					new BlendStateInfo(
						IVkType.bitMask(s.writeMask()), 
						false, 
						BlendOp.ADD, 
						BlendFactor.ONE_MINUS_SRC_COLOR, 
						BlendFactor.ONE_MINUS_DST_COLOR, 
						BlendOp.ADD, 
						BlendFactor.ONE_MINUS_SRC_ALPHA, 
						BlendFactor.ONE_MINUS_DST_ALPHA
					)
				}, 
				new BlendAttachmentInfo(false, LogicOp.AND, new float[0]),
				new DynamicStateInfo(new int[] { VK_DYNAMIC_STATE_VIEWPORT, VK_DYNAMIC_STATE_SCISSOR }),
				Optional.of(new DepthStencilInfo(false, false, CompareOp.NEVER, false, false)),
				Optional.empty(), 
				new RenderingInfo(s.framebuffer().getColorAttachments().stream().map(IAttachment::getFormat).toArray(Format[]::new), Format.D32_SFLOAT_S8_UINT, Format.D32_SFLOAT_S8_UINT), 
				new PipelineLayout(this.device,
					new DescriptorSetLayout(this.device, VK_DESCRIPTOR_SET_LAYOUT_CREATE_PUSH_DESCRIPTOR_BIT_KHR, 
						new DescriptorBinding(0, DescriptorType.UNIFORM_BUFFER, 1, new ShaderStage[] {
							ShaderStage.VERTEX 
						})
					)
				)
			);	
		});
	}
}
