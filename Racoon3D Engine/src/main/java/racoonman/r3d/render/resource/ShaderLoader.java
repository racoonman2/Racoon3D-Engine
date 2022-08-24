package racoonman.r3d.render.resource;

import java.util.HashMap;
import java.util.Map;

import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.core.Service;
import racoonman.r3d.render.shader.ShaderCompiler;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.shader.ShaderCompiler.Result;
import racoonman.r3d.render.shader.parsing.IShaderProcessor;
import racoonman.r3d.resource.codec.IDecoder;
import racoonman.r3d.resource.format.IEncodingFormat;
import racoonman.r3d.resource.io.ClassPathReader;

public class ShaderLoader {
	private IDecoder<IShaderProgram> decoder;
	private ShaderCompiler compiler;
	private Map<String, IShaderProgram> shaderCache;
	private Map<String, Result> resultCache;
	
	public ShaderLoader(Service service, IShaderProcessor shaderProcessor) {
		this.decoder = Decoders.forProgram(service);
		this.shaderCache = new HashMap<>();
		this.resultCache = new HashMap<>();
		this.compiler = new ShaderCompiler(shaderProcessor);
		this.compiler.getOptions().autobind(true).autoLayout(true);
	}

	public IShaderProgram load(String path) {
		return this.shaderCache.computeIfAbsent(path, (k) -> ClassPathReader.decode(this.decoder, IEncodingFormat.JSON, path));
	}

	public Result compileShader(String file, String entry, ShaderStage stage, String...args) {
		return this.resultCache.computeIfAbsent(file, (k) -> this.compiler.compile(ClassPathReader.readContents(file), stage, file, entry, args));
	}
}
