package racoonman.r3d.render.shader.parsing;

import io.github.douira.glsl_transformer.ast.node.TranslationUnit;

public interface IShaderProcessor {
	default String process(String srcCode) {
		return srcCode;
	}
	
	default void transform(TranslationUnit unit, String processedCode) {
	}
}
