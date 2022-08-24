package racoonman.r3d.render.api.objects;

import org.joml.Vector4f;

import racoonman.r3d.render.util.Color;

public abstract class RenderPass implements AutoCloseable {
	protected Vector4f clear;
	
	public RenderPass() {
		this.clear = new Vector4f();
	}
	
	public void clear(float r, float g, float b, float a) {
		this.clear.set(r, g, b, a);
	}
	
	public void clear(int r, int g, int b, int a) {
		this.clear(Color.normalize(r), Color.normalize(g), Color.normalize(b), Color.normalize(a));
	}
	
	public abstract RenderPass begin();
	
	public abstract void end();
	
	public abstract IFramebuffer getFramebuffer();
	
	@Override
	public void close() {
		this.end();
	}
}
