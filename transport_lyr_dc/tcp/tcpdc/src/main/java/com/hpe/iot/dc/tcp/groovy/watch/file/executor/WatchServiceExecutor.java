package com.hpe.iot.dc.tcp.groovy.watch.file.executor;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;

import java.io.IOException;

import javax.enterprise.concurrent.ManagedExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.watch.service.DirectoryWatchService;

/**
 * @author sveera
 *
 */
public class WatchServiceExecutor {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final DirectoryWatchService directoryWatchService;
	private final ManagedExecutorService managedExecutorService;

	public WatchServiceExecutor(DirectoryWatchService directoryWatchService,
			ManagedExecutorService managedExecutorService) {
		super();
		this.directoryWatchService = directoryWatchService;
		this.managedExecutorService = managedExecutorService;
	}

	public DirectoryWatchService getDirectoryWatchService() {
		return directoryWatchService;
	}

	public ManagedExecutorService getManagedExecutorService() {
		return managedExecutorService;
	}

	public void startExecutor() throws IOException, InterruptedException {
		logger.debug("Starting Directory WatchServiceExecutor ");
		directoryWatchService.startWatching();
		processDirectoryEventsInThread();
	}

	private void processDirectoryEventsInThread() {
		managedExecutorService.execute(() -> {
			try {
				directoryWatchService.processEvents();
			} catch (Exception e) {
				logger.error("Failed to run WatchServiceExecutor ");
				logExceptionStackTrace(e, getClass());
			}
			logger.debug("Stopped WatchServiceExecutor ");
		});
	}

	public void stopExecutor() throws IOException {
		logger.debug("Stopping Directory WatchServiceExecutor ");
		directoryWatchService.stopWatching();
	}

	@Override
	public String toString() {
		return "WatchServiceExecutor [directoryWatchService=" + directoryWatchService + ", managedExecutorService="
				+ managedExecutorService + "]";
	}

}
