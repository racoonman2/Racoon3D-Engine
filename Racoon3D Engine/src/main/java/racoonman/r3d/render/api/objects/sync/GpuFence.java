package racoonman.r3d.render.api.objects.sync;

import java.util.ArrayList;
import java.util.List;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.vulkan.types.Dependency;
import racoonman.r3d.render.api.vulkan.types.Stage;

//TODO add other types
public class GpuFence {
	private List<ImageFence> imageFences;
	private int srcStages;
	private int dstStages;
	private int dependencies;
	
	public GpuFence() {
		this.imageFences = new ArrayList<>();
	}
	
	public int getSrcStages() {
		return this.srcStages;
	}
	
	public int getDstStages() {
		return this.dstStages;
	}
	
	public int getDependencies() {
		return this.dependencies;
	}
	
	public List<ImageFence> getImageFences() {
		return this.imageFences;
	}
	
	public GpuFence withImage(ImageFence fence) {
		this.imageFences.add(fence);
		return this;
	}
	
	public GpuFence withSrc(Stage... stages) {
		for(Stage stage : stages) {
			this.srcStages |= stage.getVkType();
		}
		return this;
	}
	
	public GpuFence withDst(Stage... stages) {
		for(Stage stage : stages) {
			this.dstStages |= stage.getVkType();
		}
		return this;
	}
	
	public GpuFence withDependency(Dependency dependency) {
		this.dependencies |= dependency.getVkType();
		return this;
	}
	
	public void insert(Context ctx) {
		ctx.insert(this);
	}
}
