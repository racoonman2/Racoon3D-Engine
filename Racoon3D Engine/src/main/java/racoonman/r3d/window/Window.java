package racoonman.r3d.window;

import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_NO_API;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.glfwGetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowMonitor;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.MemoryStack;

import racoonman.r3d.core.libraries.Libraries;
import racoonman.r3d.render.api.objects.IFramebuffer;
import racoonman.r3d.render.api.vulkan.types.PresentMode;
import racoonman.r3d.render.core.RenderSystem;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.util.math.Mathi;

public class Window implements IHandle {
	private long handle;
	private Monitor monitor;
	private List<ICloseCallback> closeCallbacks;
	private List<IWindowResizeCallback> resizeCallbacks;
	private List<IKeyCallback> keyPressCallbacks;
	private List<IKeyCallback> keyReleaseCallbacks;
	private List<IMouseCallback> mousePressCallbacks;
	private List<IMouseCallback> mouseReleaseCallbacks;
	private int lastX;
	private int lastY;
	private int x;
	private int y;
	private int lastWidth;
	private int lastHeight;
	private int width;
	private int height;
	private int frameCount;
	private Mouse mouse;
	private Keyboard keyboard;
	private PresentMode presentMode;
	private IFramebuffer target;

	public Window(int w, int h, CharSequence title, PresentMode presentMode, int frameCount) {
		this(w, h, title, null, null, presentMode, frameCount);
	}

	public Window(int w, int h, CharSequence title, Monitor monitor, Window share, PresentMode presentMode, int frameCount) {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);

		this.handle = glfwCreateWindow(w, h, title, monitor != null ? monitor.asLong() : 0L, share != null ? share.asLong() : 0L);
		this.monitor = monitor;

		this.closeCallbacks = new ArrayList<>();
		this.resizeCallbacks = new ArrayList<>();
		this.keyPressCallbacks = new ArrayList<>();
		this.keyReleaseCallbacks = new ArrayList<>();
		this.mousePressCallbacks = new ArrayList<>();
		this.mouseReleaseCallbacks = new ArrayList<>();

		glfwSetWindowSizeCallback(this.handle, (long window, int width, int height) -> {
			this.width = width;
			this.height = height;

			for (IWindowResizeCallback callback : this.resizeCallbacks) {
				callback.invoke(width, height);
			}
		});
		glfwSetWindowCloseCallback(this.handle, (long window) -> {
			for (ICloseCallback callback : this.closeCallbacks) {
				callback.invoke();
			}
		});
		glfwSetWindowPosCallback(this.handle, (long window, int xPos, int yPos) -> {
			this.x = xPos;
			this.y = yPos;
		});
		glfwSetKeyCallback(this.handle, (long window, int key, int scancode, int action, int mods) -> {
			switch (action) {
				case GLFW_PRESS -> {
					for (IKeyCallback callback : this.keyPressCallbacks) {
						callback.invoke(key, scancode, action, mods);
					}
				}
				case GLFW_RELEASE -> {
					for (IKeyCallback callback : this.keyReleaseCallbacks) {
						callback.invoke(key, scancode, action, mods);
					}
				}
			}
		});
		glfwSetMouseButtonCallback(this.handle, (long window, int button, int action, int mods) -> {
			switch (action) {
				case GLFW_PRESS -> {
					for (IMouseCallback callback : this.mousePressCallbacks) {
						callback.invoke(button, action, mods);
					}
				}
				case GLFW_RELEASE -> {
					for (IMouseCallback callback : this.mouseReleaseCallbacks) {
						callback.invoke(button, action, mods);
					}
				}
			}
		});

		this.x = this.getX();
		this.y = this.getY();
		this.width = this.getDirectWidth();
		this.height = this.getDirectHeight();
		this.mouse = new Mouse(this);
		this.keyboard = new Keyboard(this);
		this.frameCount = frameCount;
		this.presentMode = presentMode;

		this.target = RenderSystem.createFramebuffer(this);
	}
	
	public void acquire() {
		this.target.acquire();
	}
	
	public boolean present() {
		boolean resized;
		if(resized = this.target.present()) {
			this.resize();
		}
		return resized;
	}

	public Monitor getMonitor() {
		return this.monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;

		if (monitor == null) {
			glfwSetWindowMonitor(this.handle, 0L, this.lastX, this.lastY, this.lastWidth, this.lastHeight, GLFW_DONT_CARE);
		} else {
			this.lastX = this.x;
			this.lastY = this.y;

			this.lastWidth = this.width;
			this.lastHeight = this.height;

			glfwSetWindowMonitor(this.handle, monitor.asLong(), 0, 0, monitor.getWidth(), monitor.getHeight(), monitor.getRefreshRate());
		}
	}

	public Monitor getCurrentMonitor() {
		int rightX = this.x + this.width;
		int topY = this.y + this.height;
		int prev = -1;
		Monitor result = Monitor.getPrimaryMonitor();

		for (Monitor monitor : Monitor.getMonitors()) {
			try (MemoryStack stack = stackPush()) {
				IntBuffer x = stack.mallocInt(1);
				IntBuffer y = stack.mallocInt(1);
				monitor.getPos(x, y);

				int mX = x.get(0);
				int mXOffset = mX + monitor.getWidth();
				int mY = y.get(0);

				int mYOffset = mY + monitor.getHeight();
				int lowestX = Mathi.clamp(mX, mXOffset, this.x);
				int maxX = Mathi.clamp(mX, mXOffset, rightX);
				int lowestY = Mathi.clamp(mY, mYOffset, this.y);
				int maxY = Mathi.clamp(mY, mYOffset, topY);
				int deltaX = Math.max(0, maxX - lowestX);
				int deltaY = Math.max(0, maxY - lowestY);
				int sqr = deltaX * deltaY;

				if (sqr > prev) {
					result = monitor;
					prev = sqr;
				}
			}
		}

		return result;
	}

	public Window onResize(IWindowResizeCallback callback) {
		this.resizeCallbacks.add(callback);
		return this;
	}

	public Window onClose(ICloseCallback callback) {
		this.closeCallbacks.add(callback);
		return this;
	}

	public Window onKeyPress(IKeyCallback callback) {
		this.keyPressCallbacks.add(callback);
		return this;
	}

	public Window onKeyRelease(IKeyCallback callback) {
		this.keyReleaseCallbacks.add(callback);
		return this;
	}

	public Window onMousePress(IMouseCallback callback) {
		this.mousePressCallbacks.add(callback);
		return this;
	}

	public Window onMouseRelease(IMouseCallback callback) {
		this.mouseReleaseCallbacks.add(callback);
		return this;
	}

	public Status getMouseButton(MouseButton mouseButton) {
		return Status.lookup(glfwGetMouseButton(this.handle, mouseButton.asInt()));
	}

	public Status getKey(Key key) {
		return Status.lookup(glfwGetKey(this.handle, key.asInt()));
	}

	public void setCursorPos(double x, double y) {
		glfwSetCursorPos(this.handle, x, y);
	}

	public void setInputMode(int mode, int value) {
		glfwSetInputMode(this.handle, mode, value);
	}

	public void getCursorPos(DoubleBuffer x, DoubleBuffer y) {
		glfwGetCursorPos(this.handle, x, y);
	}

	public void getCursorPos(double[] x, double[] y) {
		glfwGetCursorPos(this.handle, x, y);
	}

	public void getSize(IntBuffer w, IntBuffer h) {
		glfwGetWindowSize(this.handle, w, h);
	}

	public void getSize(int[] x, int[] y) {
		glfwGetWindowSize(this.handle, x, y);
	}

	public void getPos(IntBuffer x, IntBuffer y) {
		glfwGetWindowPos(this.handle, x, y);
	}

	public void getPos(int[] x, int[] y) {
		glfwGetWindowPos(this.handle, x, y);
	}

	public int getX() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			this.getPos(x, null);
			return x.get(0);
		}
	}

	public int getY() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer y = stack.mallocInt(1);
			this.getPos(null, y);
			return y.get(0);
		}
	}

	public boolean isPressed(IAction action) {
		return action.getStatus(this) == Status.PRESS;
	}

	public int getWidth() {
		return this.width;
	}

	public int getHeight() {
		return this.height;
	}

	public int getDirectWidth() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer width = stack.mallocInt(1);
			this.getSize(width, null);
			return width.get(0);
		}
	}

	public int getDirectHeight() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer height = stack.mallocInt(1);
			this.getSize(null, height);
			return height.get(0);
		}
	}
	
	public boolean isOpen() {
		return !this.shouldClose();
	}

	public boolean shouldClose() {
		return glfwWindowShouldClose(this.handle);
	}

	@Override
	public long asLong() {
		return this.handle;
	}

	public Mouse getMouse()	{
		return this.mouse;
	}
	
	public Keyboard getKeyboard() {
		return this.keyboard;
	}

	public int getFrameCount() {
		return this.frameCount;
	}
	
	public PresentMode getPresentMode() {
		return this.presentMode;
	}
	
	public void free() {
		glfwDestroyWindow(this.handle);
	}
	
	public IFramebuffer getTarget() {
		return this.target;
	}

	public void resize() {
		RenderSystem.free(this.target);
		this.target = RenderSystem.createFramebuffer(this);
	}

	public static void pollEvents() {
		glfwPollEvents();
	}

	static {
		Libraries.init();
	}
}
