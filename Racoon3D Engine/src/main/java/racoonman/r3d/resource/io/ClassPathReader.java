package racoonman.r3d.resource.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import racoonman.r3d.core.R3DRuntime;
import racoonman.r3d.resource.codec.IDecoder;
import racoonman.r3d.resource.format.IEncodingFormat;

public class ClassPathReader {
	
	public static String readContents(String dir) {
		InputStream stream = stream(dir);
			
		if(stream != null) {
			try {
				return new String(stream.readAllBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
		
	public static <T> T decode(IDecoder<T> decoder, IEncodingFormat format, String location) {
		return read(location, (stream) -> {
			try {
				return format.decode(decoder, stream);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		});
	}
	
	public static <T> T read(String location, Function<InputStream, T> read) {
		return read.apply(stream(location));
	}
	
	public static InputStream stream(String location) {
		try {
			Path path = find(location);
			FileSystem fileSystem = path.getFileSystem();
			
			if (Files.isRegularFile(path)) {
				FileSystemProvider provider = fileSystem.provider();
				
				InputStream in = provider.newInputStream(path);
				if(in != null) {
					return in;
				}
			}
		} catch(IOException | URISyntaxException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static Path find(String location) throws URISyntaxException, IOException {
		FileSystem filesystem = null;
		location = "/" + getRoots()[0] + "/" + getDomain() + "/" + location;
		URL url = ClassPathReader.class.getResource(location);

		if (url != null) {
			URI uri = url.toURI();
			String scheme = uri.getScheme();
			Path path;

			if ("file".equals(scheme)) {
				path = Paths.get(url.toURI());
				filesystem = path.getFileSystem();
			} else {
				if (!"jar".equals(scheme)) {
					throw new IllegalArgumentException("Invalid scheme [" + scheme + "]");
				}

				filesystem = getOrCreateFileSystem(uri);
				path = filesystem.getPath(location);
			}

			return path;
		} else {
			throw new FileNotFoundException("Unable to locate path for " + location);
		}
	}

	private static FileSystem getOrCreateFileSystem(URI uri) throws IOException {
		String scheme = uri.getScheme();

		List<FileSystemProvider> providers = FileSystemProvider.installedProviders();

		for (FileSystemProvider provider : providers) {
			if (scheme.equalsIgnoreCase(provider.getScheme())) {
				try {
					return provider.getFileSystem(uri);
				} catch (FileSystemNotFoundException e) {
				}
			}
		}

		return FileSystems.newFileSystem(uri, Collections.emptyMap());
	}
	
	private static String getDomain() {
		return R3DRuntime.getClient().name();
	}
	
	private static String[] getRoots() {
		return R3DRuntime.getClient().roots();
	}
}

