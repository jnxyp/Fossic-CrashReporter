package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Matcher;

public class SystemInfoCollector extends BaseInfoCollector {
    public String javaVersion;
    public Path javaPath;
    public String vmParams;

    public int xms;
    public int xmx;
    public boolean foundGameJre;
    public boolean useGameDefaultJre;


    @Override
    public String getName() {
        return "系统信息";
    }

    @Override
    public void collectInfo() throws InfoCollectionPartialFailureException {
        collectJreInfo();
        collectVmparams();
    }

    @Override
    public String asMarkdown() {
        StringBuilder builder = new StringBuilder();
        if (!foundGameJre) {
            builder.append("**警告：未找到游戏默认Java运行时**\n\n");
        } else {
            if (!useGameDefaultJre) {
                builder.append(String.format("**警告：游戏目录下Java运行时版本与默认值(%s)不符**\n\n", Config.GAME_JRE_DEFAULT_VERSION));
            }
            builder.append(String.format("Java版本：\t\t`%s`\n", javaVersion));
            builder.append(String.format("Java路径：\t\t%s\n\n", javaPath));
        }
        builder.append(String.format("虚拟机参数：\t%s\n", vmParams));
        builder.append(String.format("堆栈初始大小(`-Xms`)：\t`%sm`\n", xmx));
        builder.append(String.format("堆栈最大大小(`-Xmx`)：\t`%sm`", xms));

        return builder.toString();
    }

    protected void collectJreInfo() {
        javaVersion = "";
        javaPath = Config.getInstance().getGameJrePath();
        if (Util.getOSType().equals("WINDOWS")) {
            foundGameJre = false;
            try {
                String output = Util.runCommand(new String[]{Config.getInstance().getGameJreExePath().toString(), "-version"});
                Matcher m = Config.GAME_JRE_CLI_VERSION_PATTERN.matcher(output);
                if (m.find()) {
                    javaVersion = m.group(1);
                    foundGameJre = true;
                }
            } catch (IOException ignored) {
            }
        } else {
            // todo: try get jre version the game uses in other os
            foundGameJre = false;
        }

        useGameDefaultJre = javaPath.compareTo(Config.getInstance().getGameJrePath()) == 0;
    }

    protected void collectVmparams() throws InfoCollectionPartialFailureException {
        File vmParamsFile = Config.getInstance().getVmparamsPath().toFile();
        try {
            vmParams = Util.readFile(vmParamsFile, Config.VMPARAMS_CHARSET);
            Matcher m = Config.VMPARAMS_MEMORY_VALUE_PATTERN.matcher(vmParams);
            while (m.find()) {
                String s = m.group(0);
                int n = Integer.parseInt(m.group(2));
                if (s.contains("Xmx")) {
                    xmx = n;
                } else {
                    xms = n;
                }
            }
        } catch (IOException | NumberFormatException e) {
            throw new InfoCollectionPartialFailureException(this, String.format("从 %s 读取Vmparams信息时发生错误", vmParamsFile.getAbsolutePath()), e);
        }
    }
}
