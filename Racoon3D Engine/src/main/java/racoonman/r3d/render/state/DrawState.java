package racoonman.r3d.render.state;

import java.util.Optional;

import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.types.ColorComponent;
import racoonman.r3d.render.api.types.CullMode;
import racoonman.r3d.render.api.types.FrontFace;
import racoonman.r3d.render.api.types.PolygonMode;
import racoonman.r3d.render.api.types.SampleCount;
import racoonman.r3d.render.api.types.Topology;
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
	
	public void bind(IState state) {
		this.program.ifPresent(state::bindProgram);
		this.cullMode.ifPresent(state::setCullMode);
		this.lineWidth.ifPresent(state::setLineWidth);
		this.topology.ifPresent(state::setTopology);
		this.sampleCount.ifPresent(state::setSamples);
		this.writeMask.ifPresent(state::setWriteMask);
		this.polygonMode.ifPresent(state::setPolygonMode);
		this.frontFace.ifPresent(state::setFrontFace);
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
