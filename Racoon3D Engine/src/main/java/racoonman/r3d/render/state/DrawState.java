package racoonman.r3d.render.state;

import java.util.Optional;

import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.api.types.ColorComponent;
import racoonman.r3d.render.api.types.CullMode;
import racoonman.r3d.render.api.types.FrontFace;
import racoonman.r3d.render.api.types.Mode;
import racoonman.r3d.render.api.types.PolygonMode;
import racoonman.r3d.render.api.types.SampleCount;
import racoonman.r3d.util.OptionalFloat;

public class DrawState {
	private Optional<IShaderProgram> program;
	private Optional<CullMode> cullMode;
	private OptionalFloat lineWidth;
	private Optional<Mode> mode;
	private Optional<SampleCount> sampleCount;
	private Optional<ColorComponent[]> writeMask;
	private Optional<PolygonMode> polygonMode;
	private Optional<FrontFace> frontFace;
	
	public DrawState() {
		this.program = Optional.empty();
		this.cullMode = Optional.empty();
		this.lineWidth = OptionalFloat.empty();
		this.mode = Optional.empty();
		this.sampleCount = Optional.empty();
		this.writeMask = Optional.empty();
		this.polygonMode = Optional.empty();
		this.frontFace = Optional.empty();
	}
	
	public DrawState setProgram(IShaderProgram program) {
		this.program = Optional.of(program);
		return this;
	}
	
	public DrawState setCullMode(CullMode cullMode) {
		this.cullMode = Optional.of(cullMode);
		return this;
	}
	
	public DrawState setLineWidth(float width) {
		this.lineWidth = OptionalFloat.of(width);
		return this;
	}
	
	public DrawState setMode(Mode topology) {
		this.mode = Optional.of(topology);
		return this;
	}
	
	public DrawState setSamples(SampleCount samples) {
		this.sampleCount = Optional.of(samples);
		return this;
	}
	
	public DrawState setWriteMask(ColorComponent... mask) {
		this.writeMask = Optional.of(mask);
		return this;
	}
	
	public DrawState setPolygonMode(PolygonMode mode) {
		this.polygonMode = Optional.of(mode);
		return this;
	}
	
	public DrawState setFrontFace(FrontFace face) {
		this.frontFace = Optional.of(face);
		return this;
	}
	
	public void bind(IState state) {
		this.program.ifPresent(state::bindProgram);
		this.cullMode.ifPresent(state::setCullMode);
		this.lineWidth.ifPresent(state::setLineWidth);
		this.mode.ifPresent(state::setTopology);
		this.sampleCount.ifPresent(state::setSamples);
		this.writeMask.ifPresent(state::setWriteMask);
		this.polygonMode.ifPresent(state::setPolygonMode);
		this.frontFace.ifPresent(state::setFrontFace);
	}
	
	public static DrawState getDefault() {
		return new DrawState()
			.setCullMode(CullMode.BACK)
			.setLineWidth(1.0F)
			.setMode(Mode.TRIANGLE_LIST)
			.setSamples(SampleCount.COUNT_1)
			.setWriteMask(ColorComponent.values())
			.setPolygonMode(PolygonMode.FILL)
			.setFrontFace(FrontFace.CW);
	}
}
