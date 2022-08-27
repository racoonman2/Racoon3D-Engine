package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.VK10.vkCreateGraphicsPipelines;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;
import java.util.Optional;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkGraphicsPipelineCreateInfo;
import org.lwjgl.vulkan.VkPipelineColorBlendAttachmentState;
import org.lwjgl.vulkan.VkPipelineColorBlendStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDepthStencilStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineDynamicStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineInputAssemblyStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineMultisampleStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRasterizationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineRenderingCreateInfo;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkPipelineTessellationStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineVertexInputStateCreateInfo;
import org.lwjgl.vulkan.VkPipelineViewportStateCreateInfo;
import org.lwjgl.vulkan.VkVertexInputAttributeDescription;
import org.lwjgl.vulkan.VkVertexInputBindingDescription;

import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.types.BindPoint;
import racoonman.r3d.render.api.types.BlendFactor;
import racoonman.r3d.render.api.types.BlendOp;
import racoonman.r3d.render.api.types.CompareOp;
import racoonman.r3d.render.api.types.CullMode;
import racoonman.r3d.render.api.types.Format;
import racoonman.r3d.render.api.types.FrontFace;
import racoonman.r3d.render.api.types.IVkType;
import racoonman.r3d.render.api.types.LogicOp;
import racoonman.r3d.render.api.types.PolygonMode;
import racoonman.r3d.render.api.types.SampleCount;
import racoonman.r3d.render.api.types.Topology;
import racoonman.r3d.render.vertex.VertexFormat;
import racoonman.r3d.render.vertex.VertexFormat.Attribute;
import racoonman.r3d.resource.codec.ArrayCodec;
import racoonman.r3d.resource.codec.ICodec;
import racoonman.r3d.resource.codec.PrimitiveCodec;

class GraphicsPipeline implements IPipeline {
	private Device device;
	private IShaderProgram shaderProgram;
	private PipelineLayout layout;
	private VertexInfo vertexInfo;
	private AssemblyInfo assemblyInfo;
	private ViewportInfo viewportInfo;
	private RasterizationInfo rasterizationInfo;
	private MultisampleInfo multisampleInfo;
	private BlendStateInfo[] blendStateInfo;
	private BlendAttachmentInfo blendAttachmentInfo;
	private DynamicStateInfo dynamicStateInfo;
	private Optional<DepthStencilInfo> depthStencilInfo;
	private Optional<TessellationInfo> tessellationInfo;
	private RenderingInfo renderingInfo;
	private long handle;

	public GraphicsPipeline(Device device, IShaderProgram program, VertexInfo vertexInfo, AssemblyInfo assemblyInfo, ViewportInfo viewportInfo, RasterizationInfo rasterizationInfo, MultisampleInfo multisampleInfo, BlendStateInfo[] blendStateInfo, BlendAttachmentInfo blendAttachmentInfo, DynamicStateInfo dynamicStateInfo, Optional<DepthStencilInfo> depthStencilInfo, Optional<TessellationInfo> tessellationInfo, RenderingInfo renderingInfo, PipelineLayout layout) {
		this.device = device;
		this.layout = layout;
		this.shaderProgram = program;
		this.vertexInfo = vertexInfo;
		this.assemblyInfo = assemblyInfo;
		this.viewportInfo = viewportInfo;
		this.rasterizationInfo = rasterizationInfo;
		this.multisampleInfo = multisampleInfo;
		this.blendStateInfo = blendStateInfo;
		this.blendAttachmentInfo = blendAttachmentInfo;
		this.dynamicStateInfo = dynamicStateInfo;
		this.depthStencilInfo = depthStencilInfo;
		this.tessellationInfo = tessellationInfo;
		this.renderingInfo = renderingInfo;
		
		try (MemoryStack stack = stackPush()) {
			VkDevice vkDevice = device.get();

			IShader[] shaders = program.getShaders();
			VkPipelineShaderStageCreateInfo.Buffer stages = VkPipelineShaderStageCreateInfo.calloc(shaders.length, stack);

			for (int i = 0; i < shaders.length; i++) {
				IShader shader = shaders[i];

				stages.get(i)
					.sType$Default()
					.stage(shader.stage().getVkType())
					.module(shader.asLong())
					.pName(stack.UTF8(shader.name()));
			}
			
			VertexFormat[] formats = vertexInfo.formats();
			VkVertexInputBindingDescription.Buffer bindings = VkVertexInputBindingDescription.calloc(formats.length, stack);
			
			int attribCount = 0;
			
			for(VertexFormat format : formats) {
				attribCount += format.getAttributeCount();
			}
			
			VkVertexInputAttributeDescription.Buffer attributes = VkVertexInputAttributeDescription.calloc(attribCount, stack);
			
			int location = 0;
			
			for(int i = 0; i < formats.length; i++) {
				VertexFormat format = formats[i];
				
				bindings.get(i)
					.binding(i)
					.stride(format.getStride())
					.inputRate(format.getInputRate().getVkType());
				
				int offset = 0;
				
				for(Attribute att : format.getAttributes()) {
					attributes.get(location)
						.binding(i)
						.location(location)
						.format(att.format().getVkType())
						.offset(offset);
					offset += att.size();
					location++;
				}
			}
			
			VkPipelineVertexInputStateCreateInfo vertexStateInfo = VkPipelineVertexInputStateCreateInfo.calloc()
				.sType$Default()	
				.pVertexBindingDescriptions(bindings)
				.pVertexAttributeDescriptions(attributes);

			VkPipelineInputAssemblyStateCreateInfo assemblyStateInfo = VkPipelineInputAssemblyStateCreateInfo.calloc(stack)
				.sType$Default()
				.topology(assemblyInfo.topology().getVkType());

			VkPipelineViewportStateCreateInfo viewportStateInfo = VkPipelineViewportStateCreateInfo.calloc(stack)
				.sType$Default()
				.viewportCount(viewportInfo.viewportCount())
				.scissorCount(viewportInfo.scissorCount());

			VkPipelineRasterizationStateCreateInfo rasterizationStateInfo = VkPipelineRasterizationStateCreateInfo.calloc(stack)
				.sType$Default()
				.polygonMode(rasterizationInfo.polygonMode().getVkType())
				.cullMode(rasterizationInfo.cullMode().getVkType())
				.frontFace(rasterizationInfo.frontFace().getVkType())
				.lineWidth(rasterizationInfo.lineWidth());

			VkPipelineMultisampleStateCreateInfo multisampleStateInfo = VkPipelineMultisampleStateCreateInfo.calloc(stack)
				.sType$Default()
				.rasterizationSamples(multisampleInfo.sampleCount().getVkType());

			VkPipelineColorBlendAttachmentState.Buffer blendAttStateInfo = VkPipelineColorBlendAttachmentState.calloc(blendStateInfo.length, stack);

			for (int i = 0; i < blendStateInfo.length; i++) {
				BlendStateInfo blendInfo = blendStateInfo[i];

				blendAttStateInfo.get(i)
					.colorWriteMask(blendInfo.writeMask())
					.blendEnable(blendInfo.blendEnable())
					.colorBlendOp(blendInfo.colorBlendOp().getVkType())
					.srcColorBlendFactor(blendInfo.srcColorBlendFactor().getVkType())
					.dstColorBlendFactor(blendInfo.dstColorBlendFactor().getVkType())
					.alphaBlendOp(blendInfo.alphaBlendOp().getVkType())
					.srcAlphaBlendFactor(blendInfo.srcAlphaBlendFactor().getVkType())
					.dstAlphaBlendFactor(blendInfo.dstAlphaBlendFactor().getVkType());
			}

			VkPipelineColorBlendStateCreateInfo blendInfo = VkPipelineColorBlendStateCreateInfo.calloc(stack)
				.sType$Default()
				.logicOpEnable(blendAttachmentInfo.logicOpEnable())
				.logicOp(blendAttachmentInfo.logicOp().getVkType())
				.blendConstants(stack.floats(blendAttachmentInfo.blendConstants()))
				.pAttachments(blendAttStateInfo);

			VkPipelineDynamicStateCreateInfo dynamicInfo = VkPipelineDynamicStateCreateInfo.calloc(stack)
				.sType$Default()
				.pDynamicStates(stack.ints(dynamicStateInfo.dynamicStates()));
			
			VkPipelineRenderingCreateInfo renderingCreateInfo = VkPipelineRenderingCreateInfo.calloc(stack)
				.sType$Default()
				.pColorAttachmentFormats(stack.ints(IVkType.asInts(renderingInfo.colorFormats)))
				.colorAttachmentCount(renderingInfo.colorFormats.length)
				.depthAttachmentFormat(renderingInfo.depthFormat.getVkType())
				.stencilAttachmentFormat(renderingInfo.stencilFormat.getVkType());

			VkGraphicsPipelineCreateInfo.Buffer pipelineInfo = VkGraphicsPipelineCreateInfo.calloc(1, stack)
				.sType$Default()
				.pStages(stages)
				.pVertexInputState(vertexStateInfo)
				.pInputAssemblyState(assemblyStateInfo)
				.pViewportState(viewportStateInfo)
				.pRasterizationState(rasterizationStateInfo)
				.pMultisampleState(multisampleStateInfo)
				.pColorBlendState(blendInfo)
				.pDynamicState(dynamicInfo)
				.layout(layout.asLong())
				.pNext(renderingCreateInfo);

			tessellationInfo.ifPresent((info) -> {
				pipelineInfo.pTessellationState(VkPipelineTessellationStateCreateInfo.calloc(stack)
					.sType$Default()
					.flags(info.flags())
					.patchControlPoints(info.patchControlPoints()));
			});

			depthStencilInfo.ifPresent((info) -> {
				pipelineInfo.pDepthStencilState(VkPipelineDepthStencilStateCreateInfo.calloc(stack)
					.sType$Default()
					.depthTestEnable(info.depthTestEnable())
					.depthWriteEnable(info.depthWriteEnable())
					.depthCompareOp(info.depthCompareOp().getVkType())
					.depthBoundsTestEnable(info.depthBoundsTestEnable())
					.stencilTestEnable(info.stencilTestEnable()));
			});
			
			LongBuffer pointer = stack.mallocLong(1);
			vkAssert(vkCreateGraphicsPipelines(vkDevice, 0L, pipelineInfo, null, pointer), "Error creating graphics pipelines");
			this.handle = pointer.get(0);
		}
	}
	
	public IShaderProgram getShaderProgram() {
		return this.shaderProgram;
	}
	
	public VertexInfo getVertexInfo() {
		return this.vertexInfo;
	}
	
	public AssemblyInfo getAssemblyInfo() {
		return this.assemblyInfo;
	}
	
	public ViewportInfo getViewportInfo() {
		return this.viewportInfo;
	}
	
	public RasterizationInfo getRasterizationInfo() {
		return this.rasterizationInfo;
	}
	
	public MultisampleInfo getMultisampleInfo() {
		return this.multisampleInfo;
	}
	
	public BlendStateInfo[] getBlendStateInfo() {
		return this.blendStateInfo;
	}
	
	public BlendAttachmentInfo getBlendAttachmentInfo() {
		return this.blendAttachmentInfo;
	}
	
	public DynamicStateInfo getDynamicStateInfo() {
		return this.dynamicStateInfo;
	}
	
	public Optional<DepthStencilInfo> getDepthStencilInfo() {
		return this.depthStencilInfo;
	}
	
	public Optional<TessellationInfo> getTessellationInfo() {
		return this.tessellationInfo;
	}
	
	public RenderingInfo getRenderingInfo() {
		return this.renderingInfo;
	}
	
	@Override
	public PipelineLayout getLayout() {
		return this.layout;
	}

	@Override
	public BindPoint getBindPoint() {
		return BindPoint.GRAPHICS;
	}
	
	@Override
	public Device getDevice() {
		return this.device;
	}

	@Override
	public long asLong() {
		return this.handle;
	}

	//TODO remove all of these records
	
	public static record AssemblyInfo(Topology topology) {
		public static final ICodec<AssemblyInfo> CODEC = ICodec.simple(Topology.ORDINAL_CODEC.fetch("topology", AssemblyInfo::topology), AssemblyInfo::new);
	}

	public static record ViewportInfo(int viewportCount, int scissorCount) {
		public static final ICodec<ViewportInfo> CODEC = ICodec.simple(PrimitiveCodec.INT.fetch("viewport_count", ViewportInfo::viewportCount), PrimitiveCodec.INT.fetch("scissor_count", ViewportInfo::scissorCount), ViewportInfo::new);
	}

	public static record RasterizationInfo(PolygonMode polygonMode, CullMode cullMode, float lineWidth, FrontFace frontFace) {
		public static final ICodec<RasterizationInfo> CODEC = ICodec.simple(PolygonMode.ORDINAL_CODEC.fetch("polygon_mode", RasterizationInfo::polygonMode), CullMode.ORDINAL_CODEC.fetch("cull_mode", RasterizationInfo::cullMode), PrimitiveCodec.FLOAT.fetch("line_width", RasterizationInfo::lineWidth), FrontFace.ORDINAL_CODEC.fetch("front_face", RasterizationInfo::frontFace), RasterizationInfo::new);
	}

	public static record MultisampleInfo(SampleCount sampleCount) {
		public static final ICodec<MultisampleInfo> CODEC = ICodec.simple(SampleCount.ORDINAL_CODEC.fetch("sample_count", MultisampleInfo::sampleCount), MultisampleInfo::new);
	}

	public static record BlendStateInfo(int writeMask, boolean blendEnable, BlendOp colorBlendOp, BlendFactor srcColorBlendFactor, BlendFactor dstColorBlendFactor, BlendOp alphaBlendOp, BlendFactor srcAlphaBlendFactor, BlendFactor dstAlphaBlendFactor) {
		public static final ICodec<BlendStateInfo> CODEC = ICodec.simple(PrimitiveCodec.INT.fetch("write_mask", BlendStateInfo::writeMask), PrimitiveCodec.BOOL.fetch("blend_enable", BlendStateInfo::blendEnable), BlendOp.ORDINAL_CODEC.fetch("color_blend_op", BlendStateInfo::colorBlendOp), BlendFactor.ORDINAL_CODEC.fetch("src_color_blend_factor", BlendStateInfo::srcColorBlendFactor), BlendFactor.ORDINAL_CODEC.fetch("dst_color_blend_factor", BlendStateInfo::dstColorBlendFactor), BlendOp.ORDINAL_CODEC.fetch("alpha_blend_op", BlendStateInfo::alphaBlendOp), BlendFactor.ORDINAL_CODEC.fetch("src_alpha_blend_factor", BlendStateInfo::srcAlphaBlendFactor), BlendFactor.ORDINAL_CODEC.fetch("dst_alpha_blend_factor", BlendStateInfo::dstAlphaBlendFactor), BlendStateInfo::new);
	}

	public static record BlendAttachmentInfo(boolean logicOpEnable, LogicOp logicOp, float[] blendConstants) {
		public static final ICodec<BlendAttachmentInfo> CODEC = ICodec.simple(PrimitiveCodec.BOOL.fetch("logic_op_enable", BlendAttachmentInfo::logicOpEnable), LogicOp.ORDINAL_CODEC.fetch("logic_op", BlendAttachmentInfo::logicOp), ArrayCodec.FLOAT.fetch("blend_constants", BlendAttachmentInfo::blendConstants), BlendAttachmentInfo::new);
	}

	public static record DynamicStateInfo(int[] dynamicStates) {
		public static final ICodec<DynamicStateInfo> CODEC = ICodec.simple(ArrayCodec.INT.fetch("dynamic_states", DynamicStateInfo::dynamicStates), DynamicStateInfo::new);
	}

	public static record DepthStencilInfo(boolean depthTestEnable, boolean depthWriteEnable, CompareOp depthCompareOp, boolean depthBoundsTestEnable, boolean stencilTestEnable) {
		public static final ICodec<DepthStencilInfo> CODEC = ICodec.simple(PrimitiveCodec.BOOL.fetch("depth_test_enable", DepthStencilInfo::depthTestEnable), PrimitiveCodec.BOOL.fetch("depth_write_enable", DepthStencilInfo::depthWriteEnable), CompareOp.ORDINAL_CODEC.fetch("depth_compare_op", DepthStencilInfo::depthCompareOp), PrimitiveCodec.BOOL.fetch("depth_bounds_test_enable", DepthStencilInfo::depthBoundsTestEnable), PrimitiveCodec.BOOL.fetch("stencil_test_enable", DepthStencilInfo::stencilTestEnable), DepthStencilInfo::new);
	}

	public static record TessellationInfo(int flags, int patchControlPoints) {
		public static final ICodec<TessellationInfo> CODEC = ICodec.simple(PrimitiveCodec.INT.fetch("flags", TessellationInfo::flags), PrimitiveCodec.INT.fetch("patch_control_points", TessellationInfo::patchControlPoints), TessellationInfo::new);
	}

	public static record RenderingInfo(Format[] colorFormats, Format depthFormat, Format stencilFormat) {
		public static final ICodec<RenderingInfo> CODEC = ICodec.simple(ArrayCodec.of(Format.ORDINAL_CODEC, Format[]::new).fetch("color_formats", RenderingInfo::colorFormats), Format.ORDINAL_CODEC.fetch("depth_format", RenderingInfo::depthFormat), Format.ORDINAL_CODEC.fetch("stencil_format", RenderingInfo::stencilFormat), RenderingInfo::new);
	}
	
	public static record VertexInfo(VertexFormat[] formats) {
		public static final ICodec<VertexInfo> CODEC = ICodec.simple(ArrayCodec.of(VertexFormat.CODEC, VertexFormat[]::new).fetch("formats", VertexInfo::formats), VertexInfo::new);
	}
}
