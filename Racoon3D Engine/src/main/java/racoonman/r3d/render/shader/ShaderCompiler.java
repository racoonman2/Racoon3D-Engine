package racoonman.r3d.render.shader;

import static org.lwjgl.system.MemoryUtil.memUTF8;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_into_spv;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_add_macro_definition;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_initialize;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_set_auto_bind_uniforms;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_set_auto_map_locations;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_options_set_include_callbacks;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compiler_initialize;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compiler_release;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_bytes;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_compilation_status;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_get_error_message;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_result_release;

import java.nio.ByteBuffer;

import org.lwjgl.util.shaderc.ShadercIncludeResult;

import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTTransformer;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.render.shader.parsing.IShaderProcessor;
import racoonman.r3d.resource.io.ClassPathReader;
import racoonman.r3d.util.Util;

public class ShaderCompiler implements IHandle {
	private long handle;
	private IShaderProcessor processor;
	private Options options;
	private ASTTransformer<?> transformer;
	
	public ShaderCompiler(IShaderProcessor processor) {
		this.handle = shaderc_compiler_initialize();
		this.processor = processor;
		this.options = new Options();
		this.transformer = new ASTTransformer<>();
	}

	public Result compile(String srcCode, ShaderStage stage, String name, String entryPoint, String... args) {
		for (String arg : args) {
			arg = arg.replace("-D", "");
			String[] split = arg.split("=");

			if (split.length >= 2) {
				this.options.define(split[0], split[1]);
			} else {
				throw new RuntimeException("Shader argument [" + arg + "] is formatted incorrectly");
			}
		}
		
		String processedCode = this.processor.process(srcCode);
		this.transformer.setTransformation((unit) -> {
			Root.indexBuildSession(unit, () -> {
				this.processor.transform(unit, processedCode);
			});
		});

		Result result = new Result(shaderc_compile_into_spv(this.handle, this.transformer.transform(processedCode), stage.getShadercType(), name, entryPoint, this.options.asLong()));
		
		CompilationStatus status = result.getCompilationStatus();
		if (status != CompilationStatus.SUCCESS) {
			throw new RuntimeException("Shader [" + name + " - " + stage + "] + compilation failed with status " + status + ": " + result.getErrorMessage());
		}

		return result;
	}

	@Override
	public long asLong() {
		return this.handle;
	}

	public Options getOptions() {
		return this.options;
	}

	@Override
	public void free() {
		shaderc_compiler_release(this.handle);
	}

	public static class Result implements IHandle {
		private long handle;
		private ByteBuffer data;

		public Result(long handle) {
			this.handle = handle;
			this.data = shaderc_result_get_bytes(handle);
		}
		
		public CompilationStatus getCompilationStatus() {
			return IShadercType.byInt(shaderc_result_get_compilation_status(this.handle), CompilationStatus.values());
		}

		public String getErrorMessage() {
			return shaderc_result_get_error_message(this.handle);
		}
		
		@Override
		public long asLong() {
			return this.handle;
		}

		@Override
		public void free() {
			shaderc_result_release(this.handle);
		}

		public ByteBuffer getData() {
			return this.data;
		}
	}

	public static class Options implements IHandle {
		private long handle;

		public Options() {
			this.handle = shaderc_compile_options_initialize();

			shaderc_compile_options_set_include_callbacks(this.handle, (long user_data, long requested_source, int type, long requesting_source, long include_depth) -> {
				String requested = memUTF8(requested_source);
				return ShadercIncludeResult.calloc().set(Util.toBuffer(requested), Util.toBuffer(ClassPathReader.readContents(requested)), user_data).address();
			}, (userData, resultAddress) -> {
				ShadercIncludeResult result = ShadercIncludeResult.create(resultAddress);
				result.free();
				System.out.println("asdasjkd[asjd[");
			}, 0L);
		}

		public Options autobind(boolean value) {
			shaderc_compile_options_set_auto_bind_uniforms(this.handle, value);
			return this;
		}

		public Options autoLayout(boolean value) {
			shaderc_compile_options_set_auto_map_locations(this.handle, value);
			return this;
		}

		public Options define(String name, String value) {
			shaderc_compile_options_add_macro_definition(this.handle, name, value);
			return this;
		}

		@Override
		public long asLong() {
			return this.handle;
		}

		public void free() {
			shaderc_compiler_release(this.handle);
		}
	}
}
