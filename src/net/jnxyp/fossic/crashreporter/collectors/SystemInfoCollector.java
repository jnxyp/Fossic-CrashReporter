package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemInfoCollector extends BaseInfoCollector {
    @Override
    public String getName() {
        return "系统信息";
    }

    @Override
    public String getRawInfo() throws InfoCollectionPartialFailureException {
        String javaVersion = System.getProperty("java.version");
        String javaPath = System.getProperty("java.home");

        return getWarning(javaVersion, javaPath) +
                String.format("Java版本：\t`%s`\n\n", javaVersion) +
                String.format("Java路径：\t%s\n\n", javaPath) +
                String.format("虚拟机参数：\t%s", getVmparams());
    }

    protected String getVmparams() throws InfoCollectionPartialFailureException {
        File vmparams = Config.getInstance().getVmparamsPath().toFile();
        try {
            String params = Util.readFile(vmparams, Config.VMPARAMS_CHARSET);
            return Config.VMPARAMS_MEMORY_VALUE_PATTERN.matcher(params).replaceAll("**`$1`** ");
        } catch (IOException e) {
            throw new InfoCollectionPartialFailureException(this, String.format("从 %s 读取Vmparams信息时发生错误", vmparams.getAbsolutePath()), e);
        }
    }

    protected String getWarning(String javaVersion, String javaPath) {
        StringBuilder builder = new StringBuilder();
        Path jrePath = Paths.get(javaPath).toAbsolutePath();
        if (jrePath.compareTo(Config.getInstance().getGameDefaultJrePath().toAbsolutePath()) != 0) {
            builder.append("**警告：未找到游戏默认Java运行时**\n\n");
        }
        if (!javaVersion.equals(Config.GAME_DEFAULT_JRE_VERSION)) {
            builder.append(String.format("**警告：游戏目录下Java运行时版本与默认值(%s)不符**\n\n", Config.GAME_DEFAULT_JRE_VERSION));
        }
        return builder.toString();
    }
}
