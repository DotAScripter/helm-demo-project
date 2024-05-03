package com.hdp.jpod;

import java.text.SimpleDateFormat;

public class LogHandler {

    private static LogHandler instance;
    private LogLevel logLevel;

    public enum LogLevel {
        DEBUG(1),
        INFO(2),
        WARNING(3),
        ERROR(4);

        private int level;
        private String levelTitle;

        private LogLevel(int level) {
            this.level = level;
            switch (level) {
                case 1:
                    levelTitle = "Debug";
                    break;
                case 2:
                    levelTitle = "Info";
                    break;
                case 3:
                    levelTitle = "Warning";
                    break;
                case 4:
                    levelTitle = "Error";
                    break;
                default:
                    levelTitle = "?";
                    break;
            }
        }

        public int getLevel() {
            return level;
        }
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    private LogHandler() {
        logLevel = LogLevel.DEBUG;
    }

    public static LogHandler getInstance() {
        if (instance == null) {
            instance = new LogHandler();
        }
        return instance;
    }

    private void log(LogLevel logLevel, String logContent) {
        if (this.logLevel.level > logLevel.level) {
            return;
        }
        String timeStamp = new SimpleDateFormat("dd/MM-HH:mm:ss").format(new java.util.Date());
        System.out.println("" + timeStamp + " - " + logLevel.levelTitle + ": " + logContent); // change this if stdout is not wanted
    }

    public void debug(String logContent) {
        log(LogLevel.DEBUG, logContent);
    }

    public void info(String logContent) {
        log(LogLevel.INFO, logContent);
    }

    public void warning(String logContent) {
        log(LogLevel.WARNING, logContent);
    }

    public void error(String logContent) {
        log(LogLevel.ERROR, logContent);
    }
}
