package net.jnxyp.fossic.crashreporter;

import net.jnxyp.fossic.crashreporter.models.LogInfo;
import net.jnxyp.fossic.crashreporter.models.ModInfo;
import net.jnxyp.fossic.crashreporter.models.SystemInfo;
import net.jnxyp.fossic.crashreporter.views.CrashReportTabPanel;
import net.jnxyp.fossic.crashreporter.views.ErrorResolveTabPanel;
import net.jnxyp.fossic.crashreporter.views.MainFrame;
import net.jnxyp.fossic.crashreporter.views.MemorySettingTabPanel;
import net.jnxyp.fossic.crashreporter.collectors.BaseInfoCollector;
import net.jnxyp.fossic.crashreporter.collectors.LogInfoCollector;
import net.jnxyp.fossic.crashreporter.collectors.ModsInfoCollector;
import net.jnxyp.fossic.crashreporter.collectors.SystemInfoCollector;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Arrays;

public class CrashReporter {
    protected MainFrame mainFrame;
    protected boolean hasPartialFailure;

    public CrashReporter() {
        this(Config.getInstance().GAME_PATH);
    }

    public CrashReporter(Path gamePath) {
        Config.getInstance().GAME_PATH = gamePath;

        SystemInfoCollector systemInfoCollector = new SystemInfoCollector();
        ModsInfoCollector modInfoCollector = new ModsInfoCollector();
        LogInfoCollector logInfoCollector = new LogInfoCollector();

        for (BaseInfoCollector collector : new BaseInfoCollector[]{systemInfoCollector, modInfoCollector, logInfoCollector}) {
            collector.collectInfo();
            if (collector.getInfo().hasError()) {
                hasPartialFailure = true;
            }
        }

        SystemInfo systemInfo = systemInfoCollector.getInfo();
        ModInfo modInfo = modInfoCollector.getInfo();
        LogInfo logInfo = logInfoCollector.getInfo();

        CrashReportTabPanel gameLogPanel = new CrashReportTabPanel(Arrays.asList(systemInfo, modInfo), logInfo);
        MemorySettingTabPanel memoryPanel = new MemorySettingTabPanel(systemInfo);
        // ErrorResolveTabPanel errorPanel = new ErrorResolveTabPanel();

        // Set global font
        UIManager.put("Label.font", Config.UI_FONT);
        UIManager.put("Button.font", Config.UI_FONT);


        // Initialize user interface
        mainFrame = new MainFrame();

        mainFrame.addTabPanel(memoryPanel, 0);
        mainFrame.addTabPanel(gameLogPanel, 0);
        // mainFrame.addTabPanel(errorPanel, 0);
        mainFrame.switchToTab(gameLogPanel);

        mainFrame.setVisible(true);

        if (hasPartialFailure) {
            JOptionPane.showMessageDialog(mainFrame, "部分信息收集失败，请确认将本工具置于starsector.exe所在的游戏根目录。");
        }
    }
}
