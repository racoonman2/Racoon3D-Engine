package racoonman.r3d.render.resource;

import java.util.HashMap;
import java.util.Map;

import racoonman.r3d.render.api.objects.IShader;
import racoonman.r3d.render.api.objects.IShaderProgram;
import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.shader.ShaderCompiler;
import racoonman.r3d.render.shader.ShaderCompiler.Result;
import racoonman.r3d.render.shader.ShaderStage;
import racoonman.r3d.render.shader.parsing.IShaderProcessor;
import racoonman.r3d.resource.format.IEncodingFormat;
import racoonman.r3d.resource.io.ClassPathReader;

public class ShaderLoader {
	private ShaderCompiler compiler;
	private Map<String, IShaderProgram> programCache;
	private Map<String, IShader> shaderCache;
	private Map<String, Result> resultCache;
	
	public ShaderLoader(IShaderProcessor shaderProcessor) {
		this.programCache = new HashMap<>();
		this.shaderCache = new HashMap<>();
		this.resultCache = new HashMap<>();
		this.compiler = new ShaderCompiler(shaderProcessor);
		this.compiler.getOptions().autobind(true).autoLayout(true);
	}

	public void flushResultCache() {
		for(Result result : this.resultCache.values()) {
			result.free();
		}
	}
	
	public IShaderProgram loadProgram(String path, String... args) {
		return this.programCache.computeIfAbsent(path, (k) -> ClassPathReader.decode(Decoders.forProgram(args), IEncodingFormat.JSON, path));
	}
	
	public IShader loadShader(ShaderStage stage, String entry, String path, String... args) {
		return this.shaderCache.computeIfAbsent(path, (k) -> Driver.createShader(stage, entry, path, ClassPathReader.readContents(path), args));
	}

	public Result createShader(ShaderStage stage, String entry, String file, String src, String... args) {
		return this.resultCache.computeIfAbsent(file, (k) -> this.compiler.compile(src, stage, file, entry, args));
	}
}
