package com.hpe.iot.dc.watch.service;

import static com.hpe.iot.dc.util.UtilityLogger.logExceptionStackTrace;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hpe.iot.dc.watch.file.listener.FileEventListener;
import com.hpe.iot.dc.watch.listener.test.MockFileEventListener;

/**
 * @author sveera
 *
 */
public class DirectoryWatchServiceTest {

	private static final String WATCH_TXT_FILE = "watchservice_file.txt";
	private static final String TEST_WATCH_DIRECTORY = "testWatchService";

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private DirectoryWatchService directoryWatchService;

	@BeforeEach
	public void setUp() throws Exception {
		List<FileEventListener> directoryListeners = new ArrayList<>();
		directoryListeners.add(new MockFileEventListener());
		directoryWatchService = new DirectoryWatchService(ClassLoader.getSystemResource(TEST_WATCH_DIRECTORY).toURI(),
				directoryListeners, 2);
	}

	@Test
	public void testDirectoryWatchService() {
		assertNotNull(directoryWatchService,"DirectoryWatchService can not be null");
	}

	@Test
	@DisplayName("Test Modified File Change Events")
	public void testModifiedFileChangeTest()
			throws URISyntaxException, FileNotFoundException, IOException, InterruptedException {
		CountDownLatch countDownLatch = new CountDownLatch(2);
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		executeFileWatchingThread(executorService, countDownLatch);
		executeFileWritingThread(executorService, countDownLatch);
		countDownLatch.await();
		logger.info("Completed executing watch service");
	}

	private void executeFileWatchingThread(ExecutorService executorService, CountDownLatch countDownLatch) {
		executorService.execute(() -> {
			try {
				directoryWatchService.startWatching();
				directoryWatchService.processEvents();
			} catch (Exception e) {
				logExceptionStackTrace(e, getClass());
			} finally {
				countDownLatch.countDown();
			}

		});
	}

	private void executeFileWritingThread(ExecutorService executorService, CountDownLatch countDownLatch) {
		executorService.execute(() -> {
			try {
				Random random = new Random(100000000);
				for (int counter = 0; counter < 5; counter++) {
					File file = new File(ClassLoader
							.getSystemResource(TEST_WATCH_DIRECTORY + File.separator + WATCH_TXT_FILE).toURI());
					FileOutputStream fop = new FileOutputStream(file, true);
					byte[] contentInBytes = ("Random Text is " + random.nextLong() + "\n").getBytes();
					fop.write(contentInBytes);
					fop.flush();
					fop.close();
					Thread.sleep(100);
				}
			} catch (Exception e1) {
				logExceptionStackTrace(e1, getClass());
			} finally {
				try {
					Thread.sleep(200);
					countDownLatch.countDown();
					directoryWatchService.stopWatching();
				} catch (Exception e) {
					logExceptionStackTrace(e, getClass());
				}

			}

		});
	}

	@AfterEach
	public void clearTextFile() throws URISyntaxException, FileNotFoundException {
		File file = new File(
				ClassLoader.getSystemResource(TEST_WATCH_DIRECTORY + File.separator + WATCH_TXT_FILE).toURI());
		try (PrintWriter printWriter = new PrintWriter(file)) {
			printWriter.print("");
		}
	}

}
