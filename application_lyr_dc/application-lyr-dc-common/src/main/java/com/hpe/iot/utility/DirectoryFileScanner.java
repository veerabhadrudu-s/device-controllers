package com.hpe.iot.utility;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sveera
 *
 */
public class DirectoryFileScanner {

	public List<String> getDirectoryFileNames(String directoryPath) throws IOException {
		List<String> fileNames = new ArrayList<>();
		DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(directoryPath));
		for (Path path : directoryStream) {
			fileNames.add(path.toString());
		}
		directoryStream.close();
		return fileNames;
	}
}
