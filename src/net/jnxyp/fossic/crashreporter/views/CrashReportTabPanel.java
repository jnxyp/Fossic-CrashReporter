package net.jnxyp.fossic.crashreporter.views;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.models.BaseInfo;
import net.jnxyp.fossic.crashreporter.models.LogInfo;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CrashReportTabPanel extends TabPanel {
    protected List<BaseInfo> infos;
    protected LogInfo logInfo;
    protected String gameErrorLog;

    protected GridBagLayout gbl;

    protected JScrollPane reportScrollPane;
    protected JTextArea reportTextArea;
    protected JButton reportCopyButton;
    protected JButton logCopyButton;


    public CrashReportTabPanel(List<BaseInfo> infos, LogInfo logInfo) {
        this.infos = infos;
        this.logInfo = logInfo;

        this.gbl = new GridBagLayout();
        this.setLayout(gbl);
        init();
    }

    public String getTabName() {
        return "错误报告";
    }

    public void init() {
        initComponents();
        setReady(false);
        initLayout();

        generateReport();
        setReady(true);
    }

    void initComponents() {
        reportTextArea = new JTextArea();
        reportScrollPane = new JScrollPane(reportTextArea);

        reportCopyButton = new JButton("复制报告内容");
        reportCopyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Util.copyToClipboard(reportTextArea.getText());
            }
        });

        logCopyButton = new JButton("复制游戏日志");
        logCopyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Util.copyToClipboard(gameErrorLog);
            }
        });
    }

    void initLayout() {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = gbc.gridy = 0;
        gbc.weightx = gbc.weighty = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(reportScrollPane, gbc);
        this.add(reportScrollPane);

        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbl.setConstraints(reportCopyButton, gbc);
        this.add(reportCopyButton);

        gbc.weightx = 0.5;
        gbc.weighty = 0;
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbl.setConstraints(logCopyButton, gbc);
        this.add(logCopyButton);
    }

    public void setReady(boolean isReady) {
        if (isReady) {
            reportCopyButton.setEnabled(true);
            logCopyButton.setEnabled(true);
            reportTextArea.setEditable(true);
        } else {
            reportCopyButton.setEnabled(false);
            logCopyButton.setEnabled(false);
            reportTextArea.setEditable(false);
        }
    }

    public void setReport(String s) {
        this.reportTextArea.setText(s);
    }

    public void appendReport(String s) {
        this.reportTextArea.append(s);
    }

    public void setGameErrorLog(String s) {
        this.gameErrorLog = s;
    }

    public void generateReport() {
        setReport("");
        appendReport("[md]");

        for (BaseInfo info : infos) {
            if (info != logInfo) {
                appendReport(String.format("### %s\n\n%s\n\n", info.getName(), info.toMarkdown()));
            }
        }
        appendReport(String.format("（以上内容由 %s 自动生成，生成工具版本 `%s`）.\n", Config.PROGRAM_NAME, Config.PROGRAM_VERSION));
        appendReport("[/md]");

        setGameErrorLog(String.format("### %s\n\n%s\n\n", logInfo.getName(), logInfo.toMarkdown()));
    }
}
