package net.jnxyp.fossic.crashreporter;

import net.jnxyp.fossic.crashreporter.GUIs.ErrorReportTabPanel;
import net.jnxyp.fossic.crashreporter.GUIs.MainFrame;
import net.jnxyp.fossic.crashreporter.GUIs.MemorySettingTabPanel;
import net.jnxyp.fossic.crashreporter.collectors.BaseInfoCollector;
import net.jnxyp.fossic.crashreporter.collectors.LogInfoCollector;
import net.jnxyp.fossic.crashreporter.collectors.ModInfoCollector;
import net.jnxyp.fossic.crashreporter.collectors.SystemInfoCollector;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;

import javax.swing.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CrashReporter {
    protected MainFrame mainFrame;
    protected boolean hasPartialFailure;

    public CrashReporter() {
        this(Config.getInstance().GAME_PATH);
    }

    public CrashReporter(Path gamePath) {
        Config.getInstance().GAME_PATH = gamePath;


        SystemInfoCollector systemInfoCollector = new SystemInfoCollector();
        ModInfoCollector modInfoCollector = new ModInfoCollector();
        LogInfoCollector logInfoCollector = new LogInfoCollector();

        List<BaseInfoCollector> collectors = new ArrayList<>(
                Arrays.asList(systemInfoCollector, modInfoCollector, logInfoCollector));

        for (BaseInfoCollector collector : collectors) {
            try {
                collector.collectInfo();
            } catch (InfoCollectionPartialFailureException e) {
                hasPartialFailure = true;
            }
        }

        ErrorReportTabPanel gameLogPanel = new ErrorReportTabPanel(collectors, logInfoCollector);
        MemorySettingTabPanel memoryPanel = new MemorySettingTabPanel(systemInfoCollector);

        mainFrame = new MainFrame();

        mainFrame.addTabPanel(memoryPanel, 0);
        mainFrame.addTabPanel(gameLogPanel, 0);
        mainFrame.switchToTab(gameLogPanel);


        mainFrame.setVisible(true);

        if (hasPartialFailure) {
            JOptionPane.showMessageDialog(mainFrame, "部分信息收集失败，请确认将本工具置于starsector.exe所在的游戏根目录。");
        }
    }
}
