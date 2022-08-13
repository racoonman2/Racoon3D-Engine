package racoonman.r3d.render.api.vulkan.types;

import static org.lwjgl.vulkan.EXTIndexTypeUint8.VK_INDEX_TYPE_UINT8_EXT;
import static org.lwjgl.vulkan.VK10.VK_INDEX_TYPE_UINT16;
import static org.lwjgl.vulkan.VK10.VK_INDEX_TYPE_UINT32;

import java.nio.ByteBuffer;

public enum IndexType implements IVkType {
	UINT32(VK_INDEX_TYPE_UINT32, Integer.BYTES) {
		@Override
		public void store(ByteBuffer buf, int index, int data) {
			buf.putInt(index, data);
		}
	},
	UINT16(VK_INDEX_TYPE_UINT16, Short.BYTES) {
		@Override
		public void store(ByteBuffer buf, int index, int data) {
			buf.putShort(index, (short) data);
		}
	},
	UINT8(VK_INDEX_TYPE_UINT8_EXT, Byte.BYTES) {
		@Override
		public void store(ByteBuffer buf, int index, int data) {
			buf.put(index, (byte) data);
		}
	};
	
	private int vkType;
	private int bytes;
	
	private IndexType(int vkType, int bytes) {
		this.vkType = vkType;
		this.bytes = bytes;
	}
	
	@Override
	public int getVkType() {
		return this.vkType;
	}
	
	public int getBytes() {
		return this.bytes;
	}
	
	public abstract void store(ByteBuffer buf, int index, int data);
}
