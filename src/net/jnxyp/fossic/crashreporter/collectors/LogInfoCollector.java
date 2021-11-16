package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;
import net.jnxyp.fossic.crashreporter.models.BaseInfo;
import net.jnxyp.fossic.crashreporter.models.LogInfo;
import net.jnxyp.fossic.crashreporter.models.ModInfo;

import java.io.*;
import java.util.List;


public class LogInfoCollector extends BaseInfoCollector {
    public LogInfoCollector() {
        info = new LogInfo();
    }

    @Override
    public void collectInfo() {
        // todo: Implement a LogInfo model and move log parsing logic into it
        getInfo().logs = readLog();
        getInfo().errorInfo = extractErrorInfo(getInfo().logs);
        super.collectInfo();
    }

    protected List<String> readLog() {
        File logFile = Config.getInstance().getLogPath().toFile();
        List<String> lines = null;
        try {
            lines = Util.readLastNLines(logFile, Config.LOG_CHARSET, Config.MAX_LOG_CHECK_LINES);
        } catch (IOException e) {
            getInfo().addError(new InfoCollectionPartialFailureException(this, "读取log时发生错误", e));
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


    public LogInfo getInfo() {
        return (LogInfo) info;
    }

}
