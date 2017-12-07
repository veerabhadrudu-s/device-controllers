package com.hpe.iot.dc.tcp.groovy.watch.file.listener;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.nio.file.WatchEvent.Kind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.tcp.initializer.groovy.GroovyScriptTCPServiceActivator;
import com.hpe.iot.dc.watch.file.listener.FileEventListener;

/**
 * @author sveera
 *
 */
public class GroovyFileEventListener implements FileEventListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final GroovyScriptTCPServiceActivator groovyScriptTCPServiceActivator;

	public GroovyFileEventListener(GroovyScriptTCPServiceActivator groovyScriptTCPServiceActivator) {
		super();
		this.groovyScriptTCPServiceActivator = groovyScriptTCPServiceActivator;
	}

	@Override
	public void handleDirectoryChanges(Kind<?> kind, String fullPath) {
		if (!fullPath.endsWith(".groovy")) {
			logger.warn("File with name " + fullPath + " is not groovy extension");
		} else if (kind.equals(ENTRY_CREATE)) {
			executeScriptForCreationEvent(fullPath);
		} else if (kind.equals(ENTRY_MODIFY)) {
			executeScriptForModifyEvent(fullPath);
		} else if (kind.equals(ENTRY_DELETE)) {
			executeScriptForDeleteEvent(fullPath);
		} else {
			logger.info("Handling directory Change event " + kind.name() + " is not supported by this listener");
		}
	}

	private void executeScriptForCreationEvent(String fullPath) {
		try {
			groovyScriptTCPServiceActivator.startTCPService(fullPath);
		} catch (Exception e) {
			logger.error("Failed to execute Groovy Script");
			logExceptionStackTrace(e, getClass());
		}
	}

	private void executeScriptForModifyEvent(String fullPath) {
		try {
			groovyScriptTCPServiceActivator.restartTCPService(fullPath);
		} catch (Exception e) {
			logger.error("Failed to execute Groovy Script");
			logExceptionStackTrace(e, getClass());
		}
	}

	private void executeScriptForDeleteEvent(String fullPath) {
		try {
			groovyScriptTCPServiceActivator.stopTCPService(fullPath);
		} catch (Exception e) {
			logger.error("Failed to execute Groovy Script");
			logExceptionStackTrace(e, getClass());
		}
	}

}
