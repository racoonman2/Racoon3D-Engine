package racoonman.r3d.window;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

//TODO rewrite this
public abstract class InputListener<T extends IAction> {
	protected Window window;
	private AtomicInteger nextPressHandle;
	private AtomicInteger nextHoldHandle;
	private Map<Integer, CallbackHolder> holdCallbacks;
	private Map<Integer, CallbackHolder> pressCallbacks;
	private Map<Integer, CallbackHolder> releaseCallbacks;
	
	public InputListener(Window window) {
		this.window = window;
		this.nextPressHandle = new AtomicInteger();
		this.nextHoldHandle = new AtomicInteger();
		this.holdCallbacks = new HashMap<>();
		this.pressCallbacks = new HashMap<>();
		this.releaseCallbacks = new HashMap<>();
		
		window.onKeyPress((key, scancode, action, mods) -> {
			for(CallbackHolder callbackHolder : this.pressCallbacks.values()) {
				if(callbackHolder.getAction().asInt() == key && callbackHolder.isPressed(callbackHolder.getModifiers())) {
					callbackHolder.getCallback().invoke();
				}
			}
		}).onMousePress((button, action, mods) -> {
			for(CallbackHolder callbackHolder : this.pressCallbacks.values()) {
				if(callbackHolder.getAction().asInt() == button && callbackHolder.isPressed(callbackHolder.getModifiers())) {
					callbackHolder.getCallback().invoke();
				}
			}
		}).onKeyRelease((key, scancode, action, mods) -> {
			for(CallbackHolder callbackHolder : this.releaseCallbacks.values()) {
				if(callbackHolder.getAction().asInt() == key && callbackHolder.isPressed(callbackHolder.getModifiers())) {
					callbackHolder.getCallback().invoke();
				}
			}
		}).onMouseRelease((button, action, mods) -> {
			for(CallbackHolder callbackHolder : this.releaseCallbacks.values()) {
				if(callbackHolder.getAction().asInt() == button && callbackHolder.isPressed(callbackHolder.getModifiers())) {
					callbackHolder.getCallback().invoke();
				}
			}
		});
	}
	
	public void tick() {
		for(CallbackHolder callback : this.holdCallbacks.values()) {
			if(callback.isPressed(callback.getAction()) && callback.isPressed(callback.modifiers)) {
				callback.getCallback().invoke();
			}
		}
	}
	
	public boolean isPressed(IAction action) {
		return this.window.isPressed(action);
	}
	
	public int onHold(Callback callback, T action, IAction...modifiers) {
		int id = this.nextHoldHandle.getAndIncrement();
		this.holdCallbacks.put(id, new CallbackHolder(action, modifiers, callback));
		return id;
	}

	public int onPress(Callback callback, T action, IAction...modifiers) {
		int id = this.nextPressHandle.getAndIncrement();
		this.pressCallbacks.put(id, new CallbackHolder(action, modifiers, callback));
		return id;
	}
	
	public int onRelease(Callback callback, T action, IAction...modifiers) {
		int id = this.nextPressHandle.getAndIncrement();
		this.releaseCallbacks.put(id, new CallbackHolder(action, modifiers, callback));
		return id;
	}
	
	public void removeHold(int id) {
		this.holdCallbacks.remove(id);
	}
	
	public void removePress(int id) {
		this.pressCallbacks.remove(id);
	}
	
	private class CallbackHolder {
		private T action;
		private IAction[] modifiers;
		private Callback callback;
		
		public CallbackHolder(T action, IAction[] modifiers, Callback callback) {
			this.action = action;
			this.modifiers = modifiers;
			this.callback = callback;
		}
	
		protected boolean isPressed(IAction...actions) {
			for(IAction action : actions) {
				if(!InputListener.this.isPressed(action)) {
					return false;
				}
			}
			
			return true;
		}
		
		public Callback getCallback() {
			return this.callback;
		}
		
		public T getAction() {
			return this.action;
		}
		
		public IAction[] getModifiers() {
			return this.modifiers;
		}
	}
	
	public static interface Callback {
		void invoke();
	}
}
