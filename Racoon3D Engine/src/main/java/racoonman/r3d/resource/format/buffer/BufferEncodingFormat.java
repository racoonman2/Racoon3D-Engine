package racoonman.r3d.resource.format.buffer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import racoonman.r3d.resource.codec.token.IElement;
import racoonman.r3d.resource.format.IEncodingFormat;

// TODO 
// for reading and writing to network buffers

// IDEAS
// maybe buffer it when reading to allow arbitrary lookups
public class BufferEncodingFormat implements IEncodingFormat {

	@Override
	public IElement read(InputStream input) throws IOException {
		return null;
	}

	@Override
	public void write(IElement element, OutputStream output) throws IOException {

	}
}
