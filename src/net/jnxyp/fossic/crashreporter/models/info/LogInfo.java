package net.jnxyp.fossic.crashreporter.models.info;

import net.jnxyp.fossic.crashreporter.models.StackTrace;

import java.util.List;

public class LogInfo extends BaseInfo {
    public List<String> logs;
    public List<String> errorInfo;

    // TODO: log报错分析
    public List<StackTrace> stackTraces;

    @Override
    public String asText() {
        StringBuilder builder = new StringBuilder();
        for (String line : errorInfo) {
            builder.append(line).append("\n");
        }
        return builder.toString();
    }

    @Override
    public String getName() {
        return "游戏日志";
    }
}
