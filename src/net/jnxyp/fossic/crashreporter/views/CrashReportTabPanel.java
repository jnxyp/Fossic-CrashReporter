package net.jnxyp.fossic.crashreporter.views;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.models.info.BaseInfo;
import net.jnxyp.fossic.crashreporter.models.info.LogInfo;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class CrashReportTabPanel extends TabPanel {
    protected List<BaseInfo> infos;
    protected LogInfo logInfo;

    protected GridBagLayout gbl;

    protected JScrollPane reportScrollPane;
    protected JTextArea reportTextArea;
    protected JButton reportCopyButton;
    protected JButton logCopyButton;

    // States
    protected String logReportText;
    protected StringBuilder reportText;
    protected StringBuilder reportMarkdown;
    protected boolean ready;


    public CrashReportTabPanel(List<BaseInfo> infos, LogInfo logInfo) {
        this.infos = infos;
        this.logInfo = logInfo;

        logReportText = "";
        reportText = new StringBuilder();
        reportMarkdown = new StringBuilder();
        ready = false;

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
        update();
        setReady(true);
    }

    void initComponents() {
        reportTextArea = new JTextArea();
        reportTextArea.setFont(Config.REPORT_FONT);
        reportScrollPane = new JScrollPane(reportTextArea);

        reportCopyButton = new JButton("复制报告内容");
        reportCopyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Util.copyToClipboard(reportMarkdown.toString());
            }
        });

        logCopyButton = new JButton("复制游戏日志");
        logCopyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Util.copyToClipboard(logReportText);
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
        ready = isReady;
        update();
    }

    public void update() {
        if (ready) {
            reportCopyButton.setEnabled(true);
            logCopyButton.setEnabled(true);
            reportTextArea.setEditable(true);
        } else {
            reportCopyButton.setEnabled(false);
            logCopyButton.setEnabled(false);
            reportTextArea.setEditable(false);
        }
        reportTextArea.setText(reportText.toString());
    }

    protected void generateReport() {
        reportText = new StringBuilder();
        reportMarkdown = new StringBuilder();

        reportMarkdown.append("[md]");

        for (BaseInfo info : infos) {
            if (info != logInfo) {
                reportMarkdown.append(info.toMarkdown());
                reportText.append(info);
            }
        }
        reportMarkdown.append(String.format("（以上内容由 %s 自动生成，生成工具版本 `%s`）.\n", Config.PROGRAM_NAME, Config.PROGRAM_VERSION));
        reportText.append(String.format("（以上内容由 %s 自动生成，生成工具版本 %s）.\n", Config.PROGRAM_NAME, Config.PROGRAM_VERSION));
        reportMarkdown.append("[/md]");

        logReportText = logInfo.toString();
    }
}
