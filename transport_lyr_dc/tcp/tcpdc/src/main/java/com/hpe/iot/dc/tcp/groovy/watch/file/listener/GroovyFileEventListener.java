package com.hpe.iot.dc.tcp.groovy.watch.file.listener;

import static com.handson.iot.dc.util.FileUtility.getFileNameFromFullPath;
import static com.handson.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static com.hpe.broker.utility.UtilityLogger.toStringExceptionStackTrace;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.iot.dc.watch.file.listener.FileEventListener;
import com.handson.logger.service.DeploymentLoggerService;
import com.hpe.iot.dc.tcp.initializer.groovy.GroovyScriptTCPServiceActivator;

/**
 * @author sveera
 *
 */
public class GroovyFileEventListener implements FileEventListener {

	private static final String FAILED_TO_EXECUTE_GROOVY_SCRIPT = "Failed to execute Groovy Script";

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final GroovyScriptTCPServiceActivator groovyScriptTCPServiceActivator;
	private final DeploymentLoggerService deploymentLoggerService;
	private final Map<Kind<Path>, GroovyFileEventHandler> fileEventHandlers;

	public GroovyFileEventListener(DeploymentLoggerService deploymentLoggerService,
			GroovyScriptTCPServiceActivator groovyScriptTCPServiceActivator) {
		super();
		this.deploymentLoggerService = deploymentLoggerService;
		this.groovyScriptTCPServiceActivator = groovyScriptTCPServiceActivator;
		this.fileEventHandlers = new ConcurrentHashMap<>();
		initializeFileEventHandlers();
	}

	@Override
	public void handleDirectoryChanges(Kind<?> kind, String fullPath) {
		if (!fullPath.endsWith(".groovy")) {
			logger.warn("File with name " + fullPath + " is not groovy extension");
		} else if (fileEventHandlers.get(kind) == null) {
			logger.warn("Handling directory Change event " + kind.name() + " is not supported by this listener");
		} else {
			executeServiceForEventType(kind, fullPath);
		}
	}

	private void initializeFileEventHandlers() {
		fileEventHandlers.put(ENTRY_CREATE, groovyScriptTCPServiceActivator::startTCPService);
		fileEventHandlers.put(ENTRY_MODIFY, groovyScriptTCPServiceActivator::restartTCPService);
		fileEventHandlers.put(ENTRY_DELETE, groovyScriptTCPServiceActivator::stopTCPService);
	}

	private void executeServiceForEventType(Kind<?> kind, String fullPath) {
		try {
			GroovyFileEventHandler groovyFileEventHandler = fileEventHandlers.get(kind);
			groovyFileEventHandler.execute(fullPath);
			logDeploymentSuccessMessage(fullPath);
		} catch (Exception e) {
			logDeployementFailureException(fullPath, e);
		}
	}

	private void logDeploymentSuccessMessage(String fullPath) {
		deploymentLoggerService.log(getFileNameFromFullPath(fullPath), "Success");
	}

	private void logDeployementFailureException(String fullPath, Exception e) {
		logger.error(FAILED_TO_EXECUTE_GROOVY_SCRIPT);
		logExceptionStackTrace(e, getClass());
		deploymentLoggerService.log(getFileNameFromFullPath(fullPath), toStringExceptionStackTrace(e, getClass()));
		deploymentLoggerService.log(getFileNameFromFullPath(fullPath), "Failed");
	}

	private interface GroovyFileEventHandler {
		void execute(String fullPath) throws Exception;
	}

}
