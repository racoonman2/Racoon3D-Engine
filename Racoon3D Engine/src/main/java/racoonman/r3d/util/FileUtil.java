package racoonman.r3d.util;

import java.nio.file.Path;

public class FileUtil {
	
	public static boolean hasExtension(Path path, String...extensions) {
		for(String extension : extensions) {
			if(path.getFileName().toString().endsWith(extension)) {
				return true;
			}
		}
		
		return false;
	}
	
	public static String removeExtension(Path path) {
		return removeExtension(path.getFileName().toString());
	}
	
	public static String removeExtension(String fileName) {
		int lastPeriod = fileName.lastIndexOf('.');

		if(lastPeriod <= -1) {
			return fileName;
		}
		
		return fileName.substring(0, lastPeriod);
	}
}
