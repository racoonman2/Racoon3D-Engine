package racoonman.r3d.render.api.vulkan;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

class CommandRecorder {
	private VkRenderContext context;
	private List<Consumer<CommandBuffer>> commands;
	
	public CommandRecorder(VkRenderContext context) {
		this.context = context;
		this.commands = new ArrayList<>();
	}
	
	public void submit(CommandBuffer cmdBuffer) {
		for(Consumer<CommandBuffer> command : this.commands) {
			command.accept(cmdBuffer);
		}
	}

	public void record(Consumer<CommandBuffer> cmd) {
		/*
		 * 	if (command.isBind() && RenderPass.isDependency(command.getTexture()) {
		 * 		this.activeRenderPass.addDependency(command);
		 * 	}
		 */
		this.commands.add(cmd);
	}
}
