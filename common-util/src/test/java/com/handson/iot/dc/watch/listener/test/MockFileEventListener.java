package com.handson.iot.dc.watch.listener.test;

import java.nio.file.WatchEvent.Kind;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.iot.dc.watch.file.listener.FileEventListener;

/**
 * @author sveera
 *
 */
public class MockFileEventListener implements FileEventListener {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleDirectoryChanges(Kind<?> kind, String fullPath) {
		logger.info("Identified Watch Event is " + kind.name() + " with file full path " + fullPath);
	}

}
