package com.blazeloader.util.logging;

import com.blazeloader.bl.main.Settings;
import net.acomputerdog.core.logger.CLogger;
import net.acomputerdog.core.logger.LogLevel;

public class BLogger extends CLogger {

    private boolean logToFile = true;

    BLogger(String name, boolean includeDate, boolean includeTime, LogLevel minimumLevel) {
        super(name, includeDate, includeTime, minimumLevel);
    }

    @Override
    public void logRaw(String message) {
        super.logRaw(message);
        if (logToFile && Settings.logToFile) {
            LogManager.addLogEntry(this, formatMessage(message));
        }
    }

    @Override
    public boolean log(LogLevel level, String message) {
        boolean result = super.log(level, message);
        if (result && logToFile && Settings.logToFile) {
            LogManager.addLogEntry(this, formatMessage(message) + "\n");
        }
        return result;
    }

    public boolean canLogToFile() {
        return logToFile;
    }

    public void setLogToFile(boolean logToFile) {
        this.logToFile = logToFile;
    }
}
