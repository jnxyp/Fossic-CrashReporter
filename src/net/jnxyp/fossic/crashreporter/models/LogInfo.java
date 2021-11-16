package net.jnxyp.fossic.crashreporter.models;

import java.util.List;

public class LogInfo extends BaseInfo {
    public List<String> logs;
    public List<String> errorInfo;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String line : errorInfo) {
            builder.append(line).append("\n");
        }
        return super.toString(builder.toString());
    }

    @Override
    public String toMarkdown() {
        StringBuilder builder = new StringBuilder();
        for (String line : errorInfo) {
            builder.append(line).append("\n");
        }
        return super.toMarkdown(builder.toString());
    }

    @Override
    public String getName() {
        return "游戏日志";
    }
}
