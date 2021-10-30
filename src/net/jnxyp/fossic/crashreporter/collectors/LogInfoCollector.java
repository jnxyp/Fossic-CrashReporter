package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;

import java.io.*;
import java.util.List;


public class LogInfoCollector extends BaseInfoCollector {



    @Override
    public String getName() {
        return "游戏日志";
    }

    @Override
    public void collectInfo() {
    }

    @Override
    public String asMarkdown() throws InfoCollectionPartialFailureException {
        File logFile = Config.getInstance().getLogPath().toFile();
        StringBuilder builder = new StringBuilder();
        try {
            List<String> lines = Util.readLastNLines(logFile, Config.LOG_CHARSET, Config.MAX_LOG_CHECK_LINES);
            List<String> errorLines = extractErrorInfo(lines);
            for (String line : errorLines) {
                builder.append(line).append("\n");
            }
        } catch (IOException e) {
            throw new InfoCollectionPartialFailureException(this, "读取log时发生错误", e);
        }
        return builder.toString();
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
