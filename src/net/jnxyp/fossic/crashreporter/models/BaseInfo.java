package net.jnxyp.fossic.crashreporter.models;

import net.jnxyp.fossic.crashreporter.Util;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseInfo {
    protected List<Throwable> errors;

    public abstract String getName();

    public BaseInfo() {
        errors = new ArrayList<>();
    }

    public boolean hasError() {
        return !errors.isEmpty();
    }

    public String toString(String content) {
        StringBuilder builder = new StringBuilder();

        builder.append(String.format("[%s]\n\n", getName()));

        builder.append(content);

        if (hasError()) {
            builder.append(String.format("\n在收集以上信息时，发生了 %d 个错误，可能导致信息内容不完整。\n以下列出错误细节：\n", errors.size()));

            int index = 0;
            for (Throwable e : errors) {
                index++;
                builder.append(String.format("第 %d 个错误：\n", index));
                builder.append(Util.getStackTrace(e));
                builder.append("\n");
            }
        }

        builder.append("\n");
        return builder.toString();
    }

    public String toString() {
        return toString("（此部分无内容）");
    }

    public String toMarkdown(String content) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("### %s\n\n", getName()));

        builder.append(content);

        if (hasError()) {
            builder.append(String.format("\n在收集以上信息时，发生了 %d 个错误，可能导致信息内容不完整。\n以下列出错误细节：\n", errors.size()));

            int index = 0;
            for (Throwable e : errors) {
                index++;
                builder.append(String.format("第 %d 个错误：\n", index));
                builder.append("```");
                builder.append(Util.getStackTrace(e));
                builder.append("```");
                builder.append("\n");
            }
        }

        builder.append("\n");
        return builder.toString();
    }

    public String toMarkdown() {
        return toMarkdown("（此部分无内容）");
    }

    public void addError(Throwable e) {
        errors.add(e);
    }
}
