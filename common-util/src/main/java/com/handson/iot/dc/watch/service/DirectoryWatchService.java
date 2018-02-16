package com.handson.iot.dc.watch.service;

import static com.handson.iot.dc.util.FileUtility.findFullPath;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.handson.iot.dc.watch.file.listener.FileEventListener;

/**
 * @author sveera
 *
 */
public class DirectoryWatchService {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private final Path directoryPathToBeWatched;
	private final WatchService watchService;
	private final Map<WatchKey, Path> keys;
	private final List<FileEventListener> directoryListeners;
	private final int pollingInterval;
	private volatile boolean isWatching;

	public DirectoryWatchService(String pathToBeWatched, List<FileEventListener> directoryListeners,
			int pollingInterval) throws IOException {
		this.directoryPathToBeWatched = Paths.get(findFullPath(pathToBeWatched));
		this.watchService = FileSystems.getDefault().newWatchService();
		this.directoryListeners = directoryListeners;
		this.pollingInterval = pollingInterval;
		this.keys = new HashMap<>();
	}

	public DirectoryWatchService(URI pathToBeWatched, List<FileEventListener> directoryListeners, int pollingInterval)
			throws IOException {
		this.directoryPathToBeWatched = Paths.get(pathToBeWatched);
		this.watchService = FileSystems.getDefault().newWatchService();
		this.directoryListeners = directoryListeners;
		this.pollingInterval = pollingInterval;
		this.keys = new HashMap<>();
	}

	public void startWatching() throws IOException, InterruptedException {
		Files.walkFileTree(directoryPathToBeWatched, new FileVisitor());
		isWatching = true;
	}

	public void stopWatching() throws IOException {
		isWatching = false;
	}

	public void processEvents() throws InterruptedException, IOException {
		while (isWatching) {
			WatchKey key = watchService.poll(pollingInterval, SECONDS);
			Path dir = keys.get(key);
			if (dir == null) {
				// logger.trace("WatchKey not recognized!!");
				continue;
			}
			readDirectoryEvents(key, dir);
			resetAndRemoveInaccessableDirectory(key);
			if (keys.isEmpty())
				break;
		}
		watchService.close();
	}

	private void resetAndRemoveInaccessableDirectory(WatchKey key) {
		boolean valid = key.reset();
		if (!valid) {
			keys.remove(key);
		}
	}

	private void readDirectoryEvents(WatchKey key, Path dir) {
		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();
			Path pathOfFile = ((WatchEvent<Path>) event).context();
			Path absolutePathOfFile = dir.resolve(pathOfFile);
			logger.info("Identified Watch Event is " + kind.name() + " with file full path "
					+ absolutePathOfFile.toString());
			for (FileEventListener directoryListener : directoryListeners)
				directoryListener.handleDirectoryChanges(kind, absolutePathOfFile.toString());
		}
	}

	private final class FileVisitor extends SimpleFileVisitor<Path> {

		@Override
		public FileVisitResult preVisitDirectory(Path directoryPathToBeWatched, BasicFileAttributes attrs)
				throws IOException {
			registerDirectory(directoryPathToBeWatched);
			return FileVisitResult.CONTINUE;
		}

		private void registerDirectory(Path directoryPathToBeWatched) throws IOException {
			WatchKey key = directoryPathToBeWatched.register(watchService, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE,
					OVERFLOW);
			keys.put(key, directoryPathToBeWatched);
		}
	}

}
