package racoonman.r3d.resource.format.binary;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import racoonman.r3d.resource.codec.token.IArray;
import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.codec.token.IObject;
import racoonman.r3d.resource.codec.token.PlainArray;
import racoonman.r3d.resource.codec.token.PlainElement;
import racoonman.r3d.resource.codec.token.PlainObject;
import racoonman.r3d.resource.errors.DecoderException;
import racoonman.r3d.resource.errors.EncoderException;
import racoonman.r3d.resource.format.IEncodingFormat;

public class BinaryEncodingFormat implements IEncodingFormat {
	public static final int VERSION = 0;

	private static final byte OBJECT = 0;
	private static final byte ARRAY = 1;
	private static final byte BYTE = 2;
	private static final byte SHORT = 3;
	private static final byte INT = 4;
	private static final byte LONG = 5;
	private static final byte FLOAT = 6;
	private static final byte DOUBLE = 7;
	private static final byte BOOL = 8;
	private static final byte STRING = 9;
	private static final byte ENUM = 10;
	private static final byte CHAR = 11;
	private static final byte NULL = 12;

	@Override
	public IElement read(InputStream input) throws IOException {
		try (
			GZIPInputStream compressedStream = new GZIPInputStream(input); 
			DataInputStream in = new DataInputStream(compressedStream)) {
			return readElement(in);
		}
	}

	@Override
	public void write(IElement element, OutputStream output) throws IOException {
		try (
			GZIPOutputStream compressedStream = new GZIPOutputStream(output); 
			DataOutputStream out = new DataOutputStream(compressedStream)) {
			writeElement(element, out);
		}
	}

	private static void writeElement(IElement element, DataOutput output) throws IOException {
		if (element.isObject()) {
			writeObject(element.asObject(), output);
		} else if (element.isArray()) {
			writeArray(element.asArray(), output);
		} else if (element.isBool()) {
			output.writeByte(BOOL);
			output.writeBoolean(element.asBool());
		} else if (element.isByte()) {
			output.writeByte(BYTE);
			output.writeByte(element.asByte());
		} else if (element.isChar()) {
			output.writeByte(CHAR);
			output.writeChar(element.asChar());
		} else if (element.isFloat()) {
			output.writeByte(FLOAT);
			output.writeFloat(element.asFloat());
		} else if (element.isDouble()) {
			output.writeByte(DOUBLE);
			output.writeDouble(element.asDouble());
		} else if (element.isInt() || element.isEnum()) {
			output.writeByte(INT);
			output.writeInt(element.asInt());
		} else if (element.isLong()) {
			output.writeByte(LONG);
			output.writeLong(element.asLong());
		} else if (element.isShort()) {
			output.writeByte(SHORT);
			output.writeShort(element.asShort());
		} else if (element.isString()) {
			output.writeByte(STRING);
			output.writeUTF(element.asString());
		} else if (element.isChar()) {
			output.writeByte(CHAR);
			output.writeChar(element.asChar());
		} else if (element != PlainElement.EMPTY) {
			throw new EncoderException("Unable to serialize element [" + element + "]");
		}
	}

	private static void writeObject(IObject object, DataOutput output) throws IOException {
		Map<String, IElement> elements = object.elements();
		output.writeByte(OBJECT);
		output.writeShort(elements.size());

		for (Entry<String, IElement> entry : elements.entrySet()) {
			String key = entry.getKey();
			IElement value = entry.getValue();

			output.writeUTF(key);
			writeElement(value, output);
		}
	}

	private static void writeArray(IArray array, DataOutput output) throws IOException {
		Collection<IElement> elements = array.elements().values();
		output.writeByte(ARRAY);
		output.writeShort(elements.size());

		for (IElement element : elements) {
			writeElement(element, output);
		}
	}

	private static IElement readElement(DataInput input) throws DecoderException, IOException {
		byte id = input.readByte();
		return switch (id) {
			case OBJECT -> readObject(input);
			case ARRAY -> readArray(input);
			case BYTE -> new PlainElement(input.readByte());
			case SHORT -> new PlainElement(input.readShort());
			case ENUM, INT -> new PlainElement(input.readInt());
			case LONG -> new PlainElement(input.readLong());
			case FLOAT -> new PlainElement(input.readFloat());
			case DOUBLE -> new PlainElement(input.readDouble());
			case BOOL -> new PlainElement(input.readBoolean());
			case STRING -> new PlainElement(input.readUTF());
			case CHAR -> new PlainElement(input.readChar());
			case NULL -> PlainElement.EMPTY;
			default -> throw new DecoderException("Invalid type id [" + id + "], input has most likely been corrupted");
		};
	}

	private static IObject readObject(DataInput input) throws DecoderException, IOException {
		IObject result = new PlainObject();
		int size = input.readUnsignedShort();

		for (int i = 0; i < size; i++) {
			String key = input.readUTF();
			result.set(key, readElement(input));
		}

		return result;
	}

	private static IArray readArray(DataInput input) throws DecoderException, IOException {
		IArray result = new PlainArray();
		int size = input.readUnsignedShort();

		for (int i = 0; i < size; i++) {
			result.append(readElement(input));
		}

		return result;
	}
}
