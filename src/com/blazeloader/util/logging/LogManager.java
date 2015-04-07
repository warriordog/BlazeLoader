package com.blazeloader.util.logging;

import com.blazeloader.bl.main.Settings;
import net.acomputerdog.core.java.Sleep;
import net.acomputerdog.core.logger.CLogger;
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
    private static CLogger loggerLogger = new CLogger("LogManager", false, true);

    private static boolean logsInitialized = false;
    private static boolean loggerActive = true;

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

    static void addLogEntry(BLogger logger, String message) {
        if (logsInitialized) {
            logQueue.add(new LogItem(logger, message));
        }
    }

    static {
        if (!(logDir.isDirectory() || logDir.mkdirs())) {
            loggerLogger.logError("Unable to create logging directory!  Logs will not be recorded!");
        } else {
            logsInitialized = true;
        }

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                loggerActive = false;
                logInfo("Writing queued logs.");
                if (logQueue != null && !logQueue.isEmpty()) {
                    for (LogItem item : logQueue) {
                        try {
                            writeLogItem(item);
                        } catch (Exception ignored) {}
                    }
                    logQueue.clear();
                }
                if (writerMap != null && !writerMap.isEmpty()) {
                    for (Writer writer : writerMap.values()) {
                        try {
                            writer.flush();
                            writer.close();
                        } catch (Exception ignored) {}
                    }
                    writerMap.clear();
                }
                logInfo("Done.");
            }

            private void logInfo(String message) {
                if (loggerLogger != null) {
                    loggerLogger.logInfo(message);
                } else {
                    fallbackLog("INFO", message);
                }
            }

            private void logWarning(String message) {
                if (loggerLogger != null) {
                    loggerLogger.logWarning(message);
                } else {
                    fallbackLog("WARNING", message);
                }
            }

            private void fallbackLog(String level, String message) {
                System.out.println("[LogManager/Fallback][" + level + "] " + message);
            }

        });

        new Thread() {
            @Override
            public synchronized void start() {
                this.setName("LogWriterThread");
                this.setDaemon(true);
                super.start();
            }

            @Override
            public void run() {
                loggerLogger.logInfo("Log writer thread started.");
                while (loggerActive) {
                    while (logQueue.size() > 0) {
                        LogItem log = logQueue.poll();
                        if (logsInitialized && Settings.logToFile) {
                            try {
                                writeLogItem(log);
                            } catch (IOException ignored) {}
                        }
                    }
                    Sleep.sleep(100);
                }
                loggerLogger.logInfo("Log writer terminated.");
            }
        }.start();
        loggerLogger.logInfo("BLogger system initialized.");
    }

    private static void writeLogItem(LogItem log) throws IOException {
        getWriter(log.logger.getName()).write(log.message);
    }

    private static Writer getWriter(String name) {
        Writer writer = writerMap.get(name);
        if (writer == null) {
            try {
                writer = new FileWriter(new File(logDir, name + ".bl_log.txt"));
            } catch (IOException e) {
                loggerLogger.logWarning("Exception creating logger for \"" + name + "\"!", e);
                writer = new NullWriter();
            }
            writerMap.put(name, writer);
        }
        return writer;
    }

    private static class LogItem {
        private final BLogger logger;
        private final String message;

        private LogItem(BLogger logger, String message) {
            this.logger = logger;
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
