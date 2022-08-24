package racoonman.r3d.render.resource;

import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.vulkan.types.Format;
import racoonman.r3d.render.api.vulkan.types.ImageLayout;
import racoonman.r3d.render.api.vulkan.types.LoadOp;
import racoonman.r3d.render.api.vulkan.types.StoreOp;
import racoonman.r3d.render.api.vulkan.types.ViewType;
import racoonman.r3d.render.core.Service;
import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.resource.codec.ArrayCodec;
import racoonman.r3d.resource.codec.IDecoder;
import racoonman.r3d.resource.codec.PrimitiveCodec;

public class Decoders {
	
	public static IDecoder<IFramebuffer> forFramebuffer(Object... args) {
		record AttachmentInfo(ImageLayout layout, int layers, Format format, ViewType viewType, LoadOp loadOp, StoreOp storeOp) {
			static final IDecoder<AttachmentInfo> DECODER = IDecoder.of(
				ImageLayout.NAME_CODEC.fetch("layout"),
				PrimitiveCodec.INT.fetch("layers"),
				Format.NAME_CODEC.fetch("format"),
				ViewType.NAME_CODEC.fetch("view_type"),
				LoadOp.NAME_CODEC.fetch("load_op"),
				StoreOp.NAME_CODEC.fetch("store_op"),
				AttachmentInfo::new
			); 
		}
		
		return IDecoder.of(
			PrimitiveCodec.INT.fetchOr("width", () -> (int) args[0]),
			PrimitiveCodec.INT.fetchOr("height", () -> (int) args[1]),
			ArrayCodec.of(AttachmentInfo.DECODER, AttachmentInfo[]::new).fetchOr("color_attachments", () -> new AttachmentInfo[0]),
			AttachmentInfo.DECODER.fetchOr("depth_attachment", null),
			(width, height, colorAttachments, depthAttachment) -> {
				IFramebuffer fb = Driver.createFramebuffer(width, height);

				for(AttachmentInfo info : colorAttachments) {
					fb.withColor(
						info.layers(), 
						info.layout(), 
						info.format(), 
						info.viewType());
				}
				
				if(depthAttachment != null) {
					fb.withDepth(
						depthAttachment.layers(), 
						depthAttachment.layout(), 
						depthAttachment.format(), 
						depthAttachment.viewType());
				}
				
				return fb;
			}
		);
	}
	
	public static IDecoder<IShaderProgram> forProgram(Service service) {
		return ArrayCodec.of(IDecoder.of(
			ShaderStage.NAME_CODEC.fetch("stage"),
			PrimitiveCodec.STRING.fetch("entry"), 
			PrimitiveCodec.STRING.fetch("file"), 
			ArrayCodec.STRING.fetchOr("args", () -> new String[0]), 
			service::createShader
		), IShader[]::new).map(service::createProgram);
	}
}
