package racoonman.r3d.render.api.objects;

import java.util.function.Consumer;
import java.util.function.Function;

import racoonman.r3d.render.Context;
import racoonman.r3d.render.api.sync.LocalSync;
import racoonman.r3d.render.api.types.Stage;

public interface ISwapchain extends IFramebuffer {
	boolean present();
	
	ISwapchain copy();
	
	IWindowSurface getSurface();
	
	IDeviceSync getAvailable();
	
	IDeviceSync getFinish();
	
	default void sync(Context ctx, Function<IAttachment, LocalSync> sync) {
		ctx.alert(this.getFinish());
		
		ctx.await(this.getAvailable(), Stage.OUTPUT);
		ctx.await(sync.apply(this.getColorAttachment(0)));
	}
	
	default boolean isValid() {
		return this.getSurface().getWidth() > 0 && this.getSurface().getHeight() > 0;
	}
	
	default void wrapFrame(Consumer<ISwapchain> consumer) {
		if(this.isValid()) {
			this.acquire();
			consumer.accept(this);
			this.present();
		}
	}
}
