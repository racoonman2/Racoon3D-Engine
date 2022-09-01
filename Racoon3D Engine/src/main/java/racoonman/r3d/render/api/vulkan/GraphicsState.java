package racoonman.r3d.render.api.vulkan;

import java.util.Arrays;
import java.util.Comparator;

import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.types.ColorComponent;
import racoonman.r3d.render.api.types.CullMode;
import racoonman.r3d.render.api.types.FrontFace;
import racoonman.r3d.render.api.types.Mode;
import racoonman.r3d.render.api.types.PolygonMode;
import racoonman.r3d.render.api.types.SampleCount;
import racoonman.r3d.render.vertex.VertexFormat;

record GraphicsState(IShaderProgram program, VertexFormat[] formats, Mode mode, PolygonMode polygonMode, CullMode cullMode, float lineWidth, FrontFace frontFace, SampleCount sampleCount, ColorComponent[] writeMask, IFramebuffer framebuffer) {
	public static final Comparator<GraphicsState> COMPARATOR = (o1, o2) ->
		o1 == o2 ? 1 : 
		equals(o1, o2) ? 0 
		: -1;
	
	public boolean isValid() {
		return this.program != null && 
			   this.formats != null && 
			   this.mode != null &&
			   this.polygonMode != null &&
			   this.cullMode != null &&
			   this.frontFace != null &&
			   this.sampleCount != null &&
			   this.writeMask != null &&
			   this.framebuffer != null;
	}
	
	private static boolean equals(GraphicsState o1, GraphicsState o2) {
		return o1.program().equals(o2.program()) && 
			   Arrays.equals(o1.formats(), o2.formats()) && 
			   o1.mode().equals(o2.mode()) && 
			   o1.polygonMode().equals(o2.polygonMode()) &&
			   o1.cullMode().equals(o2.cullMode()) && 
			   o1.lineWidth() == o2.lineWidth() &&
			   o1.frontFace().equals(o2.frontFace()) &&
			   o1.sampleCount().equals(o2.sampleCount()) &&
			   Arrays.equals(o1.writeMask(), o2.writeMask()) && 
			   o1.framebuffer().equals(o2.framebuffer());
	}
}
