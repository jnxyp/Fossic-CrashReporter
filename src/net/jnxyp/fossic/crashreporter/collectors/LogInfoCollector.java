package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class LogInfoCollector extends BaseInfoCollector {
    protected List<String> logs;
    protected List<String> errorInfo;

    @Override
    public String getName() {
        return "游戏日志";
    }

    @Override
    public void collectInfo() throws InfoCollectionPartialFailureException {
        // todo: Implement a LogInfo model and move log parsing logic into it
        logs = readLog();
        errorInfo = extractErrorInfo(logs);
        super.collectInfo();
    }

    @Override
    public String asMarkdown() {
        File logFile = Config.getInstance().getLogPath().toFile();
        StringBuilder builder = new StringBuilder();
        for (String line : errorInfo) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    protected List<String> readLog() throws InfoCollectionPartialFailureException {
        File logFile = Config.getInstance().getLogPath().toFile();
        List<String> lines;
        try {
            lines = Util.readLastNLines(logFile, Config.LOG_CHARSET, Config.MAX_LOG_CHECK_LINES);
        } catch (IOException e) {
            throw new InfoCollectionPartialFailureException(this, "读取log时发生错误", e);
        }
        return lines;
    }

    protected static List<String> extractErrorInfo(List<String> logs) {
        int endIndex = logs.size() - 1;
        for (int i = endIndex; i >= 0; i--) {
            String line = logs.get(i);
            if (Config.LOG_EXCEPTION_LINE_PATTERN.matcher(line).find()) {
                return logs.subList(Math.max(i - Config.LOG_LINES_ABOVE_EXCEPTION, 0), endIndex);
            }
        }
        return logs.subList(endIndex - Config.LOG_LINES_IF_NO_EXCEPTION, endIndex);
    }

}
