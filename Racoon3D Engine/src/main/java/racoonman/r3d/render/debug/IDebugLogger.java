package racoonman.r3d.render.debug;

import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT;

import java.util.function.Consumer;

import racoonman.r3d.render.api.vulkan.types.IVkType;

public interface IDebugLogger {
	public static final IDebugLogger NOOP = new IDebugLogger() {
		private static final Severity[] SEVERITIES = {};
		private static final Type[] TYPES = {};

		@Override
		public void log(Severity severity, Type type, String message, int id) {
			// NOOP
		}

		@Override
		public Severity[] getSeverities() {
			return SEVERITIES;
		}

		@Override
		public Type[] getTypes() {
			return TYPES;
		}
	};
	
	public static IDebugLogger basic(Consumer<String> printInfo, Consumer<String> printError) {
		return new IDebugLogger() {
			
			@Override
			public void log(Severity severity, Type type, String message, int id) {
				if(severity == Severity.WARNING || severity == Severity.ERROR) {
					printError.accept(message);
				} else {
					printInfo.accept(message);
				}
			}
			
			@Override
			public Type[] getTypes() {
				return Type.values();
			}
			
			@Override
			public Severity[] getSeverities() {
				return Severity.values();
			}
		};
	}

	Severity[] getSeverities();

	Type[] getTypes();

	void log(Severity severity, Type type, String message, int id);

	public static enum Severity implements IVkType {
		VERBOSE(VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT),
		INFO(VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT),
		WARNING(VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT),
		ERROR(VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT);

		private int vkType;

		private Severity(int vkType) {
			this.vkType = vkType;
		}

		@Override
		public int getVkType() {
			return this.vkType;
		}
	}

	public static enum Type implements IVkType {
		GENERAL(VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT),
		VALIDATION(VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT),
		PERFORMANCE(VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT);

		private int vkType;

		private Type(int vkType) {
			this.vkType = vkType;
		}

		@Override
		public int getVkType() {
			return this.vkType;
		}
	}
}
