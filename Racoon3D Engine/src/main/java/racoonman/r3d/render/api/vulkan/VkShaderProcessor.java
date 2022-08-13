package racoonman.r3d.render.api.vulkan;

import java.util.stream.Stream;

import io.github.douira.glsl_transformer.ast.node.Identifier;
import io.github.douira.glsl_transformer.ast.node.TranslationUnit;
import io.github.douira.glsl_transformer.ast.node.declaration.InterfaceBlockDeclaration;
import io.github.douira.glsl_transformer.ast.node.external_declaration.DeclarationExternalDeclaration;
import io.github.douira.glsl_transformer.ast.node.type.FullySpecifiedType;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.LayoutQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.NamedLayoutQualifierPart;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.StorageQualifier;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.StorageQualifier.StorageType;
import io.github.douira.glsl_transformer.ast.node.type.qualifier.TypeQualifier;
import io.github.douira.glsl_transformer.ast.node.type.specifier.BuiltinNumericTypeSpecifier;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructBody;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructDeclarator;
import io.github.douira.glsl_transformer.ast.node.type.struct.StructMember;
import io.github.douira.glsl_transformer.ast.transform.ASTInjectionPoint;
import io.github.douira.glsl_transformer.util.Type;
import racoonman.r3d.render.shader.parsing.IShaderProcessor;

public class VkShaderProcessor implements IShaderProcessor {
	private static final String PV_BLOCK_NAME = "R3DPV"; // projection & view matrices are stored in ubo
	private static final String MN_BLOCK_NAME = "R3DMN";   // model & normal matrices are stored in push constants
	private static final String PV_BLOCK_VARIABLE_NAME = "r3d_pv";
	private static final String MN_BLOCK_VARIABLE_NAME = "r3d_mn";
	private static final String PROJECTION_MATRIX = "p";
	private static final String VIEW_MATRIX = "v";
	private static final String MODEL_MATRIX = "m";
	private static final String NORMAL_MATRIX = "n";
	
	@Override
	public String process(String srcCode) {
		return reroutGlMatrixReferences(srcCode);
	}
	
	@Override
	public void transform(TranslationUnit unit, String processedCode) {
		if(
			processedCode.contains(PV_BLOCK_VARIABLE_NAME + "." + PROJECTION_MATRIX) || 
			processedCode.contains(PV_BLOCK_VARIABLE_NAME + "." + VIEW_MATRIX) 	     
		) { 
			injectPVBlock(unit);
		}
		
		if(
			processedCode.contains(MN_BLOCK_VARIABLE_NAME + "." + MODEL_MATRIX) || 
			processedCode.contains(MN_BLOCK_VARIABLE_NAME + "." + NORMAL_MATRIX) 	     
		) { 
			injectMNBlock(unit);
		}
	}

	private static void injectPVBlock(TranslationUnit unit) {
		DeclarationExternalDeclaration blockDeclaration = new DeclarationExternalDeclaration(
			new InterfaceBlockDeclaration(
				new TypeQualifier(
					Stream.of(
						new StorageQualifier(StorageType.UNIFORM)
					)
				),
				new Identifier(PV_BLOCK_NAME),
				new StructBody(
					Stream.of(
						makeStructMember(Type.F32MAT4X4, PROJECTION_MATRIX),
						makeStructMember(Type.F32MAT4X4, VIEW_MATRIX)
					)
				),
				new Identifier(PV_BLOCK_VARIABLE_NAME)
			)
		);
		
		unit.injectNode(ASTInjectionPoint.BEFORE_ALL, blockDeclaration);
	}
	
	private static void injectMNBlock(TranslationUnit unit) {
		DeclarationExternalDeclaration blockDeclaration = new DeclarationExternalDeclaration(
			new InterfaceBlockDeclaration(
				new TypeQualifier(
					Stream.of(
						new StorageQualifier(StorageType.UNIFORM),
						new LayoutQualifier(
							Stream.of(
								new NamedLayoutQualifierPart(
									new Identifier("push_constant")
								)
							)
						)
					)
				),
				new Identifier(MN_BLOCK_NAME),
				new StructBody(
					Stream.of(
						makeStructMember(Type.F32MAT4X4, MODEL_MATRIX),
						makeStructMember(Type.F32MAT3X3, NORMAL_MATRIX)
					)
				),
				new Identifier(MN_BLOCK_VARIABLE_NAME)
			)
		);
		unit.injectNode(ASTInjectionPoint.BEFORE_ALL, blockDeclaration);
	}
	
	private static String reroutGlMatrixReferences(String srcCode) {
		return srcCode
			.replaceAll("gl_ProjectionMatrix", PV_BLOCK_VARIABLE_NAME + "." + PROJECTION_MATRIX)
			.replaceAll("gl_ViewMatrix", 	   PV_BLOCK_VARIABLE_NAME + "." + VIEW_MATRIX)
			.replaceAll("gl_ModelMatrix",	   MN_BLOCK_VARIABLE_NAME + "." + MODEL_MATRIX)
			.replaceAll("gl_NormalMatrix",	   MN_BLOCK_VARIABLE_NAME + "." + NORMAL_MATRIX);
	}
	
	private static StructMember makeStructMember(Type type, String name) {
		return new StructMember(
			new FullySpecifiedType(
				new TypeQualifier(
					Stream.of()
				),
				new BuiltinNumericTypeSpecifier(type)
			),
			Stream.of(
				new StructDeclarator(
					new Identifier(name)
				)
			)
		);
	}
}
