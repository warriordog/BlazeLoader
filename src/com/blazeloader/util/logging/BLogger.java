package com.blazeloader.util.logging;

import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.LogLevel;

public class BLogger extends CLogger {

    private boolean logToFile = false;

    BLogger(String name, boolean includeDate, boolean includeTime, LogLevel minimumLevel) {
        super(name, includeDate, includeTime, minimumLevel);
    }

    @Override
    public void logRaw(String message) {
        super.logRaw(message);
        if (logToFile) {
            LogManager.addLogEntry(this.getName(), message);
        }
    }

    public boolean canLogToFile() {
        return logToFile;
    }

    public void setLogToFile(boolean logToFile) {
        this.logToFile = logToFile;
    }
}
