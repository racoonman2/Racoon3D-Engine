package racoonman.r3d.render.api.vulkan;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.vulkan.EXTDebugUtils.VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT;
import static org.lwjgl.vulkan.EXTDebugUtils.vkCreateDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.VK10.VK_FALSE;
import static racoonman.r3d.render.api.vulkan.VkUtils.vkAssert;

import java.nio.LongBuffer;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCreateInfoEXT;

import racoonman.r3d.render.api.types.IVkType;
import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.debug.IDebugLogger;
import racoonman.r3d.render.debug.IDebugLogger.Severity;
import racoonman.r3d.render.debug.IDebugLogger.Type;
import racoonman.r3d.render.natives.IHandle;


class DebugMessengerUtils implements IHandle {
	private Vulkan vulkan;
	private boolean validate;
	private long handle;

	public DebugMessengerUtils(Vulkan vulkan, boolean validate) {
		this.vulkan = vulkan;
		this.validate = validate;
		
		if (this.validate) {
			try (MemoryStack stack = stackPush()) {
				IDebugLogger debugLogger = Driver.getDebugLogger();
				VkDebugUtilsMessengerCreateInfoEXT info = VkDebugUtilsMessengerCreateInfoEXT.calloc(stack)
					.sType(VK_STRUCTURE_TYPE_DEBUG_UTILS_MESSENGER_CREATE_INFO_EXT)
					.messageSeverity(IVkType.bitMask(debugLogger.getSeverities()))
					.messageType(IVkType.bitMask(debugLogger.getTypes()))
					.pfnUserCallback((int messageSeverity, int messageType, long pCallbackData, long pUserData) -> {
						VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
						String message = callbackData.pMessageString();
						debugLogger.log(IVkType.fromBitMask(messageSeverity, Severity.values()), IVkType.fromBitMask(messageType, Type.values()), message, callbackData.messageIdNumber());
						return VK_FALSE;
					});
				
				LongBuffer longBuf = stack.mallocLong(1);
				vkAssert(vkCreateDebugUtilsMessengerEXT(this.vulkan.get(), info, null, longBuf), "Error intializing debug utils");
				this.handle = longBuf.get(0);
			}
		}
	}
	
	@Override
	public long asLong() {
		return this.handle;
	}
	
	@Override
	public void free() {
		vkDestroyDebugUtilsMessengerEXT(this.vulkan.get(), this.handle, null);
	}
}
