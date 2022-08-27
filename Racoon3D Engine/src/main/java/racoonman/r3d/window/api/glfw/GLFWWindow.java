package racoonman.r3d.window.api.glfw;

import static org.lwjgl.glfw.GLFW.GLFW_CLIENT_API;
import static org.lwjgl.glfw.GLFW.GLFW_DONT_CARE;
import static org.lwjgl.glfw.GLFW.GLFW_NO_API;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwFocusWindow;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowCloseCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowFocusCallback;
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

import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.system.MemoryStack;

import racoonman.r3d.core.libraries.Libraries;
import racoonman.r3d.render.api.objects.ISwapchain;
import racoonman.r3d.render.api.objects.IWindowSurface;
import racoonman.r3d.render.api.objects.wrapper.ResizableSwapchain;
import racoonman.r3d.render.api.types.PresentMode;
import racoonman.r3d.render.config.Config;
import racoonman.r3d.render.core.Driver;
import racoonman.r3d.render.natives.IHandle;
import racoonman.r3d.util.NativeImage;
import racoonman.r3d.window.IAction;
import racoonman.r3d.window.ICloseCallback;
import racoonman.r3d.window.IKeyCallback;
import racoonman.r3d.window.IMouseCallback;
import racoonman.r3d.window.IWindow;
import racoonman.r3d.window.IWindowResizeCallback;
import racoonman.r3d.window.InputMode;
import racoonman.r3d.window.Key;
import racoonman.r3d.window.Keyboard;
import racoonman.r3d.window.Monitor;
import racoonman.r3d.window.Mouse;
import racoonman.r3d.window.MouseButton;
import racoonman.r3d.window.Status;

//TODO properly abstract this away
public class GLFWWindow implements IWindow {
	private long handle;
	private Monitor monitor;
	private boolean focused;
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
	private Mouse mouse;
	private Keyboard keyboard;
	private PresentMode presentMode;
	private IWindowSurface surface;
	private ISwapchain swapchain;
	private CharSequence title;
	
	public GLFWWindow(int w, int h, CharSequence title, PresentMode presentMode) {
		this(w, h, title, null, null, presentMode);
	}

	public GLFWWindow(int w, int h, CharSequence title, Monitor monitor, IWindow share, PresentMode presentMode) {
		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);

		this.handle = glfwCreateWindow(w, h, title, IHandle.getSafely(monitor), IHandle.getSafely(share));
		this.monitor = monitor;
		this.title = title;

		this.closeCallbacks = new ArrayList<>();
		this.resizeCallbacks = new ArrayList<>();
		this.keyPressCallbacks = new ArrayList<>();
		this.keyReleaseCallbacks = new ArrayList<>();
		this.mousePressCallbacks = new ArrayList<>();
		this.mouseReleaseCallbacks = new ArrayList<>();

		glfwSetWindowSizeCallback(this.handle, (long window, int width, int height) -> {
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
		glfwSetWindowFocusCallback(this.handle, (long window, boolean focused) -> {
			this.focused = focused;
		});

		this.x = this.getX();
		this.y = this.getY();
		this.mouse = new Mouse(this);
		this.keyboard = new Keyboard(this);
		this.presentMode = presentMode;

		this.surface = Driver.createSurface(this);
		this.swapchain = new ResizableSwapchain(this.surface, Config.FRAME_COUNT);
		
		this.focus();
	}
	
	@Override
	public CharSequence getTitle() {
		return this.title;
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

			this.lastWidth = this.getWidth();
			this.lastHeight = this.getHeight();

			glfwSetWindowMonitor(this.handle, monitor.asLong(), 0, 0, monitor.getWidth(), monitor.getHeight(), monitor.getRefreshRate());
		}
	}

	public GLFWWindow onResize(IWindowResizeCallback callback) {
		this.resizeCallbacks.add(callback);
		return this;
	}

	public GLFWWindow onClose(ICloseCallback callback) {
		this.closeCallbacks.add(callback);
		return this;
	}

	public GLFWWindow onKeyPress(IKeyCallback callback) {
		this.keyPressCallbacks.add(callback);
		return this;
	}

	public GLFWWindow onKeyRelease(IKeyCallback callback) {
		this.keyReleaseCallbacks.add(callback);
		return this;
	}

	public GLFWWindow onMousePress(IMouseCallback callback) {
		this.mousePressCallbacks.add(callback);
		return this;
	}

	public GLFWWindow onMouseRelease(IMouseCallback callback) {
		this.mouseReleaseCallbacks.add(callback);
		return this;
	}

	public Status getMouseButton(MouseButton mouseButton) {
		return Status.lookup(glfwGetMouseButton(this.handle, mouseButton.getGLFWType()));
	}

	public Status getKey(Key key) {
		return Status.lookup(glfwGetKey(this.handle, key.getGLFWType()));
	}

	public void setCursorPos(double x, double y) {
		glfwSetCursorPos(this.handle, x, y);
	}

	public void setInputMode(InputMode mode, IGLFWType value) {
		glfwSetInputMode(this.handle, mode.getGLFWType(), value.getGLFWType());
	}

	public void getCursorPos(DoubleBuffer x, DoubleBuffer y) {
		glfwGetCursorPos(this.handle, x, y);
	}

	public void getSize(IntBuffer w, IntBuffer h) {
		glfwGetWindowSize(this.handle, w, h);
	}

	public void getPos(IntBuffer x, IntBuffer y) {
		glfwGetWindowPos(this.handle, x, y);
	}

	@Override
	public int getX() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer x = stack.mallocInt(1);
			this.getPos(x, null);
			return x.get(0);
		}
	}

	@Override
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

	@Override
	public boolean isFocused() {
		return this.focused;
	}
	
	@Override
	public void focus() {
		this.focused = true;
		
		glfwFocusWindow(this.handle);
	}

	@Override
	public int getWidth() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer width = stack.mallocInt(1);
			this.getSize(width, null);
			return width.get(0);
		}
	}

	@Override
	public int getHeight() {
		try (MemoryStack stack = stackPush()) {
			IntBuffer height = stack.mallocInt(1);
			this.getSize(null, height);
			return height.get(0);
		}
	}

	@Override
	public ISwapchain getSwapchain() {
		return this.swapchain;
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

	public void tick() {
		this.mouse.tick();
		this.keyboard.tick();

		glfwPollEvents();
	}

	@Override
	public void setIcon(NativeImage image) {
		try(MemoryStack stack = stackPush()) {
			glfwSetWindowIcon(this.handle, GLFWImage.calloc(1, stack)
				.width(image.getWidth())
				.height(image.getHeight())
				.pixels(image.getData()));		
		}
	}
	
	public PresentMode getPresentMode() {
		return this.presentMode;
	}
	
	@Override
	public void free() {
		this.swapchain.free();
		
		glfwDestroyWindow(this.handle);
	}
	
	static {
		Libraries.init();
	}
}
