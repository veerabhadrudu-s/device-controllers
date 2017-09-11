package com.hpe.iot.dc.watch.file.listener;

import java.nio.file.WatchEvent;

/**
 * @author sveera
 *
 */
public interface FileEventListener {

	void handleDirectoryChanges(WatchEvent.Kind<?> kind, String fullPath);
}
