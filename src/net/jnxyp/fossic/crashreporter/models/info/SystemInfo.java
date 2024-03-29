package net.jnxyp.fossic.crashreporter.models.info;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.models.VmParams;

import java.nio.file.Path;

public class SystemInfo extends BaseInfo {
    public String osName;
    public String osVersion;
    public long totalRam;

    public String javaVersion;
    public Path javaPath;

    public VmParams vmParams;

    public boolean foundGameJre;
    public boolean useGameDefaultJre;

    @Override
    public String getName() {
        return "系统信息";
    }

    @Override
    public String asMarkdown() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("操作系统：%s\n\n", osName));
        builder.append(String.format("版本：%s\n\n", osVersion));
        builder.append(String.format("总可用内存：`%.2fG`\n\n", totalRam / 1024.0 / 1024.0 / 1024.0));

        builder.append('\n');

        if (!foundGameJre) {
            builder.append("**警告：未找到游戏默认Java运行时**\n\n");
        } else {
            if (!useGameDefaultJre) {
                builder.append(String.format("**警告：游戏目录下Java运行时版本与默认值(%s)不符**\n\n", Config.GAME_JRE_DEFAULT_VERSION));
            }
            builder.append(String.format("Java版本：`%s`\n\n", javaVersion));
            builder.append(String.format("Java路径：%s\n\n", javaPath));
        }

        if (vmParams != null) {
            builder.append('\n');

            builder.append(String.format("虚拟机参数：%s\n\n", vmParams.getVmParams()));
            builder.append(String.format("堆栈初始大小(`-Xms`)：`%sm`\n\n", vmParams.getXms()));
            builder.append(String.format("堆栈最大大小(`-Xmx`)：`%sm`\n\n", vmParams.getXmx()));
        }
        return builder.toString();
    }

    @Override
    public String asText() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("操作系统：\t\t%s\n", osName));
        builder.append(String.format("系统版本：\t\t%s\n", osVersion));
        builder.append(String.format("总可用内存：\t\t%.2fG\n", totalRam / 1024.0 / 1024.0 / 1024.0));

        builder.append('\n');

        if (!foundGameJre) {
            builder.append("警告：未找到游戏默认Java运行时\n");
        } else {
            if (!useGameDefaultJre) {
                builder.append(String.format("警告：游戏目录下Java运行时版本与默认值(%s)不符\n", Config.GAME_JRE_DEFAULT_VERSION));
            }
            builder.append(String.format("Java版本：\t\t%s\n", javaVersion));
            builder.append(String.format("Java路径：\t\t%s\n", javaPath));
        }

        if (vmParams != null) {
            builder.append('\n');

            builder.append(String.format("虚拟机参数：\t\t%s\n", vmParams.getVmParams()));
            builder.append(String.format("堆栈初始大小(`-Xms`)：\t%sm\n", vmParams.getXms()));
            builder.append(String.format("堆栈最大大小(`-Xmx`)：\t%sm\n", vmParams.getXmx()));
        }

        return builder.toString();
    }
}
