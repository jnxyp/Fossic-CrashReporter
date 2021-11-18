package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;
import net.jnxyp.fossic.crashreporter.models.SystemInfo;
import net.jnxyp.fossic.crashreporter.models.VmParams;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SystemInfoCollector extends BaseInfoCollector {

    public static final Pattern GAME_JRE_CLI_VERSION_PATTERN = Pattern.compile("java version \"(.*?)\"");

    public SystemInfoCollector() {
        info = new SystemInfo();
    }

    @Override
    public void collectInfo() {
        collectSystemInfo();
        collectJreInfo();
        collectVmparams();
        super.collectInfo();
    }

    protected void collectSystemInfo() {
        getInfo().osName = System.getProperty("os.name");
        if (Util.getOSType().equals("WINDOWS")) {
            try {
                String output = Util.runCommand(new String[]{"cmd.exe", "/c", "ver"});
                getInfo().osName = output.replaceAll("\n", "");
            } catch (IOException e) {
                getInfo().addError(new InfoCollectionPartialFailureException(this, "在调用系统命令行获取Windows版本时发生错误。", e));
            }
        }

        com.sun.management.OperatingSystemMXBean mxbean = (com.sun.management.OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        getInfo().totalRam = mxbean.getTotalPhysicalMemorySize();
    }

    protected void collectJreInfo() {
        getInfo().javaPath = Config.getInstance().getGameJrePath();
        if (Util.getOSType().equals("WINDOWS")) {
            getInfo().foundGameJre = false;
            try {
                String output = Util.runCommand(new String[]{Config.getInstance().getGameJreExePath().toString(), "-version"});
                Matcher m = GAME_JRE_CLI_VERSION_PATTERN.matcher(output);
                if (m.find()) {
                    getInfo().javaVersion = m.group(1);
                    getInfo().foundGameJre = true;
                }
            } catch (IOException e) {
                getInfo().addError(new InfoCollectionPartialFailureException(this, "在调用系统命令行获取Jre版本时发生错误。", e));
            }
        } else {
            // todo: try get jre version the game uses in other os
            getInfo().foundGameJre = true;
            getInfo().javaVersion = System.getProperty("java.version");
            getInfo().javaPath = Paths.get(System.getProperty("java.home"));
        }

        getInfo().useGameDefaultJre = getInfo().foundGameJre && getInfo().javaPath.compareTo(Config.getInstance().getGameJrePath()) == 0;
    }

    protected void collectVmparams() {
        File vmParamsFile = Config.getInstance().getVmparamsPath().toFile();
        try {
            getInfo().vmParams = new VmParams(vmParamsFile);
        } catch (IOException | NumberFormatException e) {
            getInfo().addError(new InfoCollectionPartialFailureException(this, String.format("从 %s 读取Vmparams信息时发生错误", vmParamsFile.getAbsolutePath()), e));
        }
    }


    @Override
    public SystemInfo getInfo() {
        return (SystemInfo) info;
    }
}
