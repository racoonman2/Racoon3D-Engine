package racoonman.r3d.render;

import java.util.Optional;

import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.vulkan.types.ColorComponent;
import racoonman.r3d.render.api.vulkan.types.CullMode;
import racoonman.r3d.render.api.vulkan.types.FrontFace;
import racoonman.r3d.render.api.vulkan.types.PolygonMode;
import racoonman.r3d.render.api.vulkan.types.SampleCount;
import racoonman.r3d.render.api.vulkan.types.Topology;
import racoonman.r3d.util.OptionalFloat;

public class DrawState {
	private Optional<IShaderProgram> program;
	private Optional<CullMode> cullMode;
	private OptionalFloat lineWidth;
	private Optional<Topology> topology;
	private Optional<SampleCount> sampleCount;
	private Optional<ColorComponent[]> writeMask;
	private Optional<PolygonMode> polygonMode;
	private Optional<FrontFace> frontFace;
	
	public DrawState() {
		this.program = Optional.empty();
		this.cullMode = Optional.empty();
		this.lineWidth = OptionalFloat.empty();
		this.topology = Optional.empty();
		this.sampleCount = Optional.empty();
		this.writeMask = Optional.empty();
		this.polygonMode = Optional.empty();
		this.frontFace = Optional.empty();
	}
	
	public DrawState withProgram(IShaderProgram program) {
		this.program = Optional.of(program);
		return this;
	}
	
	public DrawState withCullMode(CullMode cullMode) {
		this.cullMode = Optional.of(cullMode);
		return this;
	}
	
	public DrawState withLineWidth(float width) {
		this.lineWidth = OptionalFloat.of(width);
		return this;
	}
	
	public DrawState withTopology(Topology topology) {
		this.topology = Optional.of(topology);
		return this;
	}
	
	public DrawState withSamples(SampleCount samples) {
		this.sampleCount = Optional.of(samples);
		return this;
	}
	
	public DrawState withWriteMask(ColorComponent... mask) {
		this.writeMask = Optional.of(mask);
		return this;
	}
	
	public DrawState withPolygonMode(PolygonMode mode) {
		this.polygonMode = Optional.of(mode);
		return this;
	}
	
	public DrawState withFrontFace(FrontFace face) {
		this.frontFace = Optional.of(face);
		return this;
	}
	
	public void bind(Context ctx) {
		this.program.ifPresent(ctx::program);
		this.cullMode.ifPresent(ctx::cullMode);
		this.lineWidth.ifPresent(ctx::lineWidth);
		this.topology.ifPresent(ctx::topology);
		this.sampleCount.ifPresent(ctx::samples);
		this.writeMask.ifPresent(ctx::writeMask);
		this.polygonMode.ifPresent(ctx::polygonMode);
		this.frontFace.ifPresent(ctx::frontFace);
	}
	
	public static DrawState getDefault() {
		return new DrawState()
			.withCullMode(CullMode.BACK)
			.withLineWidth(1.0F)
			.withTopology(Topology.TRIANGLE_LIST)
			.withSamples(SampleCount.COUNT_1)
			.withWriteMask(ColorComponent.values())
			.withPolygonMode(PolygonMode.FILL)
			.withFrontFace(FrontFace.CW);
	}
}
