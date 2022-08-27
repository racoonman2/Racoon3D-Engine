package racoonman.r3d.render.api.sync;

import java.util.ArrayList;
import java.util.List;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.objects.IImage;
import racoonman.r3d.render.api.types.Access;
import racoonman.r3d.render.api.types.Dependency;
import racoonman.r3d.render.api.types.ImageLayout;
import racoonman.r3d.render.api.types.Stage;

//TODO add other types
//TODO this layout is inconsistent with QueueSubmission
public class LocalSync {
	private List<ImageSync> imageSyncs;
	private int srcStages;
	private int dstStages;
	private int dependencies;
	
	public LocalSync() {
		this.imageSyncs = new ArrayList<>();
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
	
	public List<ImageSync> getImageSync() {
		return this.imageSyncs;
	}
	
	public LocalSync withImage(ImageSync sync) {
		this.imageSyncs.add(sync);
		return this;
	}
	
	public LocalSync withSrc(Stage... stages) {
		for(Stage stage : stages) {
			this.srcStages |= stage.getVkType();
		}
		return this;
	}
	
	public LocalSync withDst(Stage... stages) {
		for(Stage stage : stages) {
			this.dstStages |= stage.getVkType();
		}
		return this;
	}
	
	public LocalSync withDependency(Dependency dependency) {
		this.dependencies |= dependency.getVkType();
		return this;
	}
	
	public void await(Context ctx) {
		ctx.sync(this);
	}
	
	public static class ImageSync {
		private int srcAccess;
		private int dstAccess;
		private ImageLayout oldLayout;
		private ImageLayout newLayout;
		private int srcQueueIndex;
		private int dstQueueIndex;
		private IImage image;
		private int baseLayer;
		private int baseMipLevel;
		
		public ImageSync() {
			this.srcQueueIndex = -1;
			this.dstQueueIndex = -1;
		}
		
		public int getSrcAccess() {
			return this.srcAccess;
		}
		
		public int getDstAccess() {
			return this.dstAccess;
		}
		
		public ImageLayout getOldLayout() {
			return this.oldLayout;
		}
		
		public ImageLayout getNewLayout() {
			return this.newLayout;
		}
		
		public int getSrcQueue() {
			return this.srcQueueIndex;
		}
		
		public int getDstQueue() {
			return this.dstQueueIndex;
		}
		
		public IImage getImage() {
			return this.image;
		}
		
		public int getBaseLayer() {
			return this.baseLayer;
		}
		
		public int getBaseMipLevel() {
			return this.baseMipLevel;
		}
		
		public ImageSync withSrc(Access... accesses) {
			for(Access access : accesses) {
				this.srcAccess |= access.getVkType();
			}
			return this;
		}
		
		public ImageSync withDst(Access... accesses) {
			for(Access access : accesses) {
				this.dstAccess |= access.getVkType();
			}
			return this;
		}
		
		public ImageSync withOld(ImageLayout layout) {
			this.oldLayout = layout;
			return this;
		}
		
		public ImageSync withNew(ImageLayout layout) {
			this.newLayout = layout;
			return this;
		}
		
		public ImageSync withSrcQueue(int queue) {
			this.srcQueueIndex = queue;
			return this;
		}
		
		public ImageSync withDstQueue(int queue) {
			this.dstQueueIndex = queue;
			return this;
		}
		
		public ImageSync withImage(IImage image) {
			this.image = image;
			return this;
		}
		
		public ImageSync withBase(int layer) {
			this.baseLayer = layer;
			return this;
		}
		
		public ImageSync withBaseMip(int level) {
			this.baseMipLevel = level;
			return this;
		}
		
		public static ImageSync of(IImage image) {
			return new ImageSync().withImage(image);
		}
	}
}
