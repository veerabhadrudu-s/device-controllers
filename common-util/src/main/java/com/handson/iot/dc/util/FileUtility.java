/**
 * 
 */
package com.handson.iot.dc.util;

import java.nio.file.Paths;

/**
 * @author sveera
 *
 */
public class FileUtility {

	public static String findFullPath(String path) {
		String[] pathParts = path.split("}");
		return pathParts.length > 1
				? System.getProperty(pathParts[0].substring(1, pathParts[0].length())) + pathParts[1]
				: pathParts[0];
	}

	public static String getFileNameFromFullPath(String fullPath) {
		return Paths.get(fullPath).getFileName().toString();
	}

}
