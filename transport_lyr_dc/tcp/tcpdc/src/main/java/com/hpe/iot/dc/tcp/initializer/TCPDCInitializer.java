package com.hpe.iot.dc.tcp.initializer;

import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.initializer.DCInitializer;
import com.hpe.iot.dc.tcp.initializer.groovy.GroovyScriptTCPServiceActivator;
import com.handson.iot.dc.util.DirectoryFileScanner;

/**
 * @author sveera
 *
 */
public class TCPDCInitializer implements DCInitializer {

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final DirectoryFileScanner directoryFileScanner;
	private final GroovyScriptTCPServiceActivator groovyScriptTCPServiceActivator;
	private String directoryPathForGroovyScript;

	public TCPDCInitializer(String directoryPathForGroovyScript,
			GroovyScriptTCPServiceActivator groovyScriptTCPServiceActivator) {
		super();
		this.directoryPathForGroovyScript = directoryPathForGroovyScript;
		this.groovyScriptTCPServiceActivator = groovyScriptTCPServiceActivator;
		this.directoryFileScanner = new DirectoryFileScanner();

	}

	@Override
	public void startDC() {
		try {
			loadAllTcpDCPlugins();
		} catch (Exception e) {
			logger.error("Failed to initialize DC  ");
			logExceptionStackTrace(e, getClass());
		}
	}

	private void loadAllTcpDCPlugins() throws IOException {
		configureDirectoryPath();
		List<String> fileNames = directoryFileScanner.getDirectoryFileNames(directoryPathForGroovyScript);
		List<String> groovyFileNames = filterInvalidFileNames(fileNames);
		for (String groovyScriptName : groovyFileNames)
			loadTcpPluginScript(groovyScriptName);
	}

	private void configureDirectoryPath() {
		String[] pathParts = directoryPathForGroovyScript.split("}");
		if (pathParts.length > 1)
			directoryPathForGroovyScript = System.getProperty(pathParts[0].substring(1, pathParts[0].length()))
					+ pathParts[1];
		else
			directoryPathForGroovyScript = pathParts[0];
		logger.trace("Path for script files to be scanned is " + directoryPathForGroovyScript);
	}

	private void loadTcpPluginScript(String groovyScriptName) throws IOException {
		try {
			groovyScriptTCPServiceActivator.startTCPService(groovyScriptName);
		} catch (Throwable e) {
			logger.error("Failed to initialze TCP DC Plugin Script " + groovyScriptName);
			logExceptionStackTrace(e, getClass());
		}

	}

	@Override
	public void stopDC() {
		try {
			groovyScriptTCPServiceActivator.stopAllTCPServices();
		} catch (Exception e) {
			logger.error("Failed to stop DC  ");
			logExceptionStackTrace(e, getClass());
		}
	}

	private List<String> filterInvalidFileNames(List<String> fileNames) {
		List<String> groovyFileNames = new ArrayList<>();
		for (String fileName : fileNames) {
			if (fileName.endsWith(".groovy"))
				groovyFileNames.add(fileName);
			else {
				logger.warn("Invalid file identified in the directory with name " + fileName);
			}
		}
		return groovyFileNames;
	}

}
