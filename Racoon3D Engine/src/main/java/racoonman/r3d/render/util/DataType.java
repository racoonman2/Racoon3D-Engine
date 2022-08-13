package racoonman.r3d.render.util;

import racoonman.r3d.resource.codec.EnumCodec;
import racoonman.r3d.resource.codec.ICodec;

public enum DataType {
	BYTE(Byte.BYTES),
	CHAR(Character.BYTES),
	INT16(Short.BYTES),
	INT32(Integer.BYTES),
	INT64(Long.BYTES),
	FLOAT32(Float.BYTES),
	FLOAT64(Double.BYTES);
	
	public static final ICodec<DataType> ORDINAL_CODEC = EnumCodec.byOrdinal(values());
	public static final ICodec<DataType> NAME_CODEC = EnumCodec.byName(DataType::valueOf);
	
	private int size;
	
	private DataType(int size) {
		this.size = size;
	}
	
	public int getSize() {
		return this.size;
	}
}
