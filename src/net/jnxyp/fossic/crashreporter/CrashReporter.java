package net.jnxyp.fossic.crashreporter;

import net.jnxyp.fossic.crashreporter.GUIs.GameLogPanel;
import net.jnxyp.fossic.crashreporter.collectors.BaseInfoCollector;
import net.jnxyp.fossic.crashreporter.collectors.LogInfoCollector;
import net.jnxyp.fossic.crashreporter.collectors.ModInfoCollector;
import net.jnxyp.fossic.crashreporter.collectors.SystemInfoCollector;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;

import javax.swing.*;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

public class CrashReporter {
    protected GameLogPanel gameLogPanel;
    protected Collection<BaseInfoCollector> collectors;
    protected boolean hasPartialFailure;

    public CrashReporter() {
        this(Config.getInstance().GAME_PATH);
    }

    public CrashReporter(Path gamePath) {
        Config.getInstance().GAME_PATH = gamePath;
        gameLogPanel = new GameLogPanel();
        gameLogPanel.setVisible(true);

        collectors = Arrays.asList(
                new SystemInfoCollector(),
                new ModInfoCollector()
        );

        generateReport();

        if (hasPartialFailure) {
            JOptionPane.showMessageDialog(gameLogPanel, "部分信息收集失败，请确认将本工具置于starsector.exe所在的游戏根目录。");
        } else {
            gameLogPanel.setReady(true);
        }
    }

    public void generateReport() {
        hasPartialFailure = false;
        gameLogPanel.setReport("");
        gameLogPanel.appendReport("[md]");

        for (BaseInfoCollector collector : collectors) {
            try {
                collector.collectInfo();
                gameLogPanel.appendReport(String.format("### %s\n\n%s\n\n", collector.getName(), collector.asMarkdown()));
            } catch (InfoCollectionPartialFailureException e) {
                hasPartialFailure = true;
                gameLogPanel.appendReport(String.format("### %s\n\n%s\n\n", collector.getName(), Util.getStackTrace(e)));
            }
        }
        gameLogPanel.appendReport(String.format("（以上内容由 %s 自动生成，生成工具版本 `%s`）.\n", Config.PROGRAM_NAME, Config.PROGRAM_VERSION));
        gameLogPanel.appendReport("[/md]");

        LogInfoCollector collector = new LogInfoCollector();
        try {
            collector.collectInfo();
            gameLogPanel.setGameErrorLog(String.format("### %s\n\n%s\n\n", collector.getName(), collector.asMarkdown()));
        } catch (InfoCollectionPartialFailureException e) {
            hasPartialFailure = true;
            gameLogPanel.setGameErrorLog(String.format("### %s\n\n%s\n\n", collector.getName(), Util.getStackTrace(e)));
        }
    }
}
