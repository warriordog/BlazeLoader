package com.blazeloader.util.logging;

import com.blazeloader.bl.main.BLMain;
import com.blazeloader.bl.main.Settings;
import net.acomputerdog.core.java.Sleep;
import net.acomputerdog.core.logger.LogLevel;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogManager {
    private static final Map<String, BLogger> loggerCache = new HashMap<String, BLogger>();
    private static final Map<String, Writer> writerMap = new HashMap<String, Writer>();
    private static final Queue<LogItem> logQueue = new ConcurrentLinkedQueue<LogItem>();
    private static final File logDir = new File("./logs/");

    private static boolean logsEnabled = false;

    public static BLogger createLogger(String name) {
        return createLogger(name, false, true);
    }

    public static BLogger createLogger(String name, boolean date, boolean time) {
        return createLogger(name, date, time, LogLevel.getByName(Settings.minimumLogLevel));
    }

    public static BLogger createLogger(String name, boolean date, boolean time, LogLevel logLevel) {
        BLogger logger = loggerCache.get(name);
        if (logger == null) {
            logger = new BLogger(name, date, time, logLevel);
            loggerCache.put(name, logger);
        }
        return logger;
    }

    static void addLogEntry(String name, String message) {
        if (logsEnabled) {
            logQueue.add(new LogItem(name, message));
        }
    }

    static {
        if (!(logDir.isDirectory() || logDir.mkdirs())) {
            BLMain.LOGGER_MAIN.logError("Unable to create logging directory!  Logs will not be recorded!");
        }
        logsEnabled = true;
        new Thread() {
            @Override
            public synchronized void start() {
                this.setName("LogWriterThread");
                this.setDaemon(true);
                super.start();
            }

            @Override
            public void run() {
                while (true) {
                    while (logQueue.size() > 0) {
                        LogItem log = logQueue.poll();
                        try {
                            getWriter(log.name).write("[" + log.name + "] " + log.message);
                        } catch (IOException ignored) {}
                    }
                    Sleep.sleep(100);
                }
            }
        }.start();
    }

    private static Writer getWriter(String name) {
        Writer writer = writerMap.get(name);
        if (writer == null) {
            try {
                writer = new FileWriter(new File(logDir, "name.log"));
            } catch (IOException e) {
                BLMain.LOGGER_MAIN.logWarning("Exception creating logger for \"" + name + "\"!", e);
                writer = new NullWriter();
            }
            writerMap.put(name, writer);
        }
        return writer;
    }

    private static class LogItem {
        private final String name;
        private final String message;

        private LogItem(String name, String message) {
            this.name = name;
            this.message = message;
        }
    }

    private static class NullWriter extends Writer {

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {

        }

        @Override
        public void flush() throws IOException {

        }

        @Override
        public void close() throws IOException {

        }
    }
}
