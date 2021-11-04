package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;
import net.jnxyp.fossic.crashreporter.models.VmParams;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;

public class SystemInfoCollector extends BaseInfoCollector {
    public String osName;
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
    public void collectInfo() throws InfoCollectionPartialFailureException {
        collectSystemInfo();
        collectJreInfo();
        collectVmparams();
        super.collectInfo();
    }

    @Override
    public String asMarkdown() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("操作系统：\t\t%s\n", osName));
        builder.append(String.format("总可用内存：\t\t%.2fG\n", totalRam / 1024.0 / 1024.0 / 1024.0));

        builder.append('\n');

        if (!foundGameJre) {
            builder.append("**警告：未找到游戏默认Java运行时**\n\n");
        } else {
            if (!useGameDefaultJre) {
                builder.append(String.format("**警告：游戏目录下Java运行时版本与默认值(%s)不符**\n\n", Config.GAME_JRE_DEFAULT_VERSION));
            }
            builder.append(String.format("Java版本：\t\t`%s`\n", javaVersion));
            builder.append(String.format("Java路径：\t\t%s\n", javaPath));
        }

        builder.append('\n');

        builder.append(String.format("虚拟机参数：\t\t%s\n", vmParams.getVmParams()));
        builder.append(String.format("堆栈初始大小(`-Xms`)：\t`%sm`\n", vmParams.getXms()));
        builder.append(String.format("堆栈最大大小(`-Xmx`)：\t`%sm`", vmParams.getXmx()));

        return builder.toString();
    }

    protected void collectSystemInfo() {
        osName = System.getProperty("os.name");
        if (Util.getOSType().equals("WINDOWS")) {
            try {
                String output = Util.runCommand(new String[]{"cmd.exe","/c","ver"});
                osName = output.replaceAll("\n", "");
            } catch (IOException ignored){}
        }

        com.sun.management.OperatingSystemMXBean mxbean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        totalRam = mxbean.getTotalPhysicalMemorySize();
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
            foundGameJre = true;
            javaVersion = System.getProperty("java.version");
            javaPath = Paths.get(System.getProperty("java.home"));
        }

        useGameDefaultJre = foundGameJre && javaPath.compareTo(Config.getInstance().getGameJrePath()) == 0;
    }

    protected void collectVmparams() throws InfoCollectionPartialFailureException {
        File vmParamsFile = Config.getInstance().getVmparamsPath().toFile();
        try {
            vmParams = new VmParams(vmParamsFile);
        } catch (IOException | NumberFormatException e) {
            throw new InfoCollectionPartialFailureException(this, String.format("从 %s 读取Vmparams信息时发生错误", vmParamsFile.getAbsolutePath()), e);
        }
    }
}
