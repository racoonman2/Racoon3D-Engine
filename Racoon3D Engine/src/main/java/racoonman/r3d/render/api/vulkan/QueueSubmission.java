package racoonman.r3d.render.api.vulkan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import racoonman.r3d.render.api.objects.IDeviceSync;
import racoonman.r3d.render.api.objects.IHostSync;
import racoonman.r3d.render.api.types.Stage;
import racoonman.r3d.util.Holder;
import racoonman.r3d.util.IPair;

//TODO this shouldn't be a record
record QueueSubmission(List<CommandBuffer> commandBuffers, Set<IPair<IDeviceSync, Stage>> waits, Set<IDeviceSync> signals, Holder<IHostSync> hostSync) {
	
	public QueueSubmission withBuffers(CommandBuffer... buffers) {
		for(CommandBuffer buffer : buffers) {
			this.commandBuffers.add(buffer);
		}
		return this;
	}
	
	public QueueSubmission withWait(IDeviceSync sync, Stage stage) {
		this.waits.add(IPair.of(sync, stage));
		return this;
	}
	
	public QueueSubmission withSignal(IDeviceSync... syncs) {
		Collections.addAll(this.signals, syncs);
		return this;
	}
	
	public QueueSubmission withHostSync(IHostSync sync) {
		this.hostSync.map(sync);
		return this;
	}
	
	public static QueueSubmission of() {
		return new QueueSubmission(
			new ArrayList<>(),
			new HashSet<>(),
			new HashSet<>(),
			new Holder<>());
	}
}
