package racoonman.r3d.render.shader;

import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_compilation_error;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_configuration_error;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_internal_error;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_invalid_assembly;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_invalid_stage;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_null_result_object;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_success;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_transformation_error;
import static org.lwjgl.util.shaderc.Shaderc.shaderc_compilation_status_validation_error;

public enum CompilationStatus implements IShadercType {
	SUCCESS(shaderc_compilation_status_success),
	INVALID_STAGE(shaderc_compilation_status_invalid_stage),
	COMPILATION_ERROR(shaderc_compilation_status_compilation_error),
	INTERNAL_ERROR(shaderc_compilation_status_internal_error),
	NULL_RESULT_ERROR(shaderc_compilation_status_null_result_object),
	INVALID_ASSEMBLY(shaderc_compilation_status_invalid_assembly),
	VALIDATION_ERROR(shaderc_compilation_status_validation_error),
	TRANSFORMATION_ERROR(shaderc_compilation_status_transformation_error),
	CONFIGURATION_ERROR(shaderc_compilation_status_configuration_error);

	private int shadercType;
	
	private CompilationStatus(int shadercType) {
		this.shadercType = shadercType;
	}
	
	@Override
	public int getShadercType() {
		return this.shadercType;
	}
}
