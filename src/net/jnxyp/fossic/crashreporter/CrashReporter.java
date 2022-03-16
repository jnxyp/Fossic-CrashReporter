package net.jnxyp.fossic.crashreporter;

import net.jnxyp.fossic.crashreporter.collectors.*;
import net.jnxyp.fossic.crashreporter.models.info.BaseInfo;
import net.jnxyp.fossic.crashreporter.models.info.LogInfo;
import net.jnxyp.fossic.crashreporter.models.info.SystemInfo;
import net.jnxyp.fossic.crashreporter.views.CrashReportTabPanel;
import net.jnxyp.fossic.crashreporter.views.MainFrame;
import net.jnxyp.fossic.crashreporter.views.MemorySettingTabPanel;

import javax.swing.*;
import java.nio.file.Path;
import java.util.ArrayList;
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
        GameInfoCollector gameInfoCollector = new GameInfoCollector();

        ArrayList<BaseInfoCollector> collectors = new ArrayList<>(Arrays.asList(gameInfoCollector, systemInfoCollector, modInfoCollector, logInfoCollector));
        ArrayList<BaseInfo> infos = new ArrayList<>();

        for (BaseInfoCollector collector : collectors) {
            collector.collectInfo();
            infos.add(collector.getInfo());
            if (collector.getInfo().hasError()) {
                hasPartialFailure = true;
            }
        }

        SystemInfo systemInfo = systemInfoCollector.getInfo();
        LogInfo logInfo = logInfoCollector.getInfo();



        // Set global font
        UIManager.put("Label.font", Config.UI_FONT);
        UIManager.put("Button.font", Config.UI_FONT);


        // Initialize user interface
        mainFrame = new MainFrame();

        try {
            CrashReportTabPanel gameLogPanel = new CrashReportTabPanel(infos, logInfo);
            mainFrame.addTabPanel(gameLogPanel, 0);
            mainFrame.switchToTab(gameLogPanel);
        } catch (Exception ignored){}

        try{
            MemorySettingTabPanel memoryPanel = new MemorySettingTabPanel(systemInfo);
            mainFrame.addTabPanel(memoryPanel, 0);
        } catch (Exception ignored){}

        // ErrorResolveTabPanel errorPanel = new ErrorResolveTabPanel();

        // mainFrame.addTabPanel(errorPanel, 0);

        mainFrame.setVisible(true);

        if (hasPartialFailure) {
            JOptionPane.showMessageDialog(mainFrame, "部分信息收集失败，请确认将本工具置于starsector.exe所在的游戏根目录。如果已经置于根目录，请前往工具发布贴进行反馈，谢谢");
        }
    }
}
