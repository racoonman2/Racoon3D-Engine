package racoonman.r3d.resource.format;

import java.io.InputStream;
import java.io.OutputStream;

import racoonman.r3d.resource.codec.ICodec;
import racoonman.r3d.resource.codec.IDecoder;
import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.format.binary.BinaryEncodingFormat;
import racoonman.r3d.resource.format.json.JsonEncodingFormat;

public interface IEncodingFormat {
	public static final IEncodingFormat JSON = new JsonEncodingFormat();
	public static final IEncodingFormat BINARY = new BinaryEncodingFormat();
	
	default <T> T decode(IDecoder<T> decoder, InputStream in) throws Exception {
		return decoder.decode(this.read(in));
	}
	
	default <T> void encode(ICodec<T> codec, T value, OutputStream out) throws Exception {
		this.write(codec.encode(value), out);
	}
	
	IElement read(InputStream input) throws Exception;

	void write(IElement element, OutputStream output) throws Exception;
}
