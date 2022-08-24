package racoonman.r3d.render.shader;

import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compile_into_preprocessed_text;
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
import static org.lwjgl.util.shaderc.Shaderc.*;

import java.nio.ByteBuffer;

import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.shaderc.ShadercIncludeResult;

import io.github.douira.glsl_transformer.GLSLParser;
import io.github.douira.glsl_transformer.GLSLParser.TypeAndInitDeclarationContext;
import io.github.douira.glsl_transformer.GLSLParserBaseListener;
import io.github.douira.glsl_transformer.ast.query.Root;
import io.github.douira.glsl_transformer.ast.transform.ASTTransformer;
import io.github.douira.glsl_transformer.basic.EnhancedParser;
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
		shaderc_compile_options_set_target_env(this.options.asLong(), shaderc_target_env_vulkan, shaderc_env_version_vulkan_1_3);
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

		long preprocessed = shaderc_compile_into_preprocessed_text(this.handle, srcCode, stage.getShadercType(), name, entryPoint, this.options.asLong());
		String shadercProcessed = MemoryUtil.memUTF8(shaderc_result_get_bytes(preprocessed));
		
		String processedCode = this.processor.process(shadercProcessed);
		shaderc_result_release(preprocessed);
		this.transformer.setTransformation((unit) -> {
			Root.indexBuildSession(unit, () -> {
				this.processor.transform(unit, processedCode);
			});
		});
		
		String transformedCode = this.transformer.transform(processedCode);
		System.out.println(transformedCode);
		Result result = new Result(this.parseLayout(transformedCode), shaderc_compile_into_spv(this.handle, transformedCode, stage.getShadercType(), name, entryPoint, this.options.asLong()));
		
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

	private ShaderLayout parseLayout(String srcCode) {
		EnhancedParser parser = new EnhancedParser();
		GLSLParser glslParser = parser.getParser();
		ParseTreeListener listener = new GLSLParserBaseListener() {

			@Override
			public void exitTypeAndInitDeclaration(TypeAndInitDeclarationContext ctx) {
				
				
				ctx.declarationMember().forEach((member) -> System.out.println(member.getText()));
				super.enterTypeAndInitDeclaration(ctx);
			}
		};

		glslParser.addParseListener(listener);
		parser.parse(srcCode);
		return new ShaderLayout();
	}
	
	public static class Result implements IHandle {
		private ShaderLayout layout;
		private long handle;
		private ByteBuffer data;

		public Result(ShaderLayout layout, long handle) {
			this.layout = layout;
			this.handle = handle;
			this.data = shaderc_result_get_bytes(handle);
		}
		
		public ShaderLayout getLayout() {
			return this.layout;
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
				return ShadercIncludeResult.calloc().set(memByteBuffer(requested_source, requested.length()), Util.toBuffer(ClassPathReader.readContents(requested)), user_data).address();
			}, (userData, resultAddress) -> {
				ShadercIncludeResult result = ShadercIncludeResult.create(resultAddress);
				result.free();
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
