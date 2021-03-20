package net.jnxyp.fossic.crashreporter.GUIs;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
    protected GridBagLayout gbl;

    protected JScrollPane reportScrollPane;
    protected JTextArea reportTextArea;
    protected JButton reportCopyButton;
    protected JButton logCopyButton;

    protected String gameErrorLog;

    public MainFrame() {
        super(Config.PROGRAM_NAME + " " + Config.PROGRAM_VERSION);
        this.setBounds(400, 400, 800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.gbl = new GridBagLayout();
        this.setLayout(gbl);

        this.initComponents();
        this.setReady(false);
        this.initLayout();
    }

    protected void initComponents() {
        reportTextArea = new JTextArea();
        reportTextArea.append("正在收集信息，请稍侯...");
        reportScrollPane = new JScrollPane(reportTextArea);

        reportCopyButton = new JButton("复制报告内容");
        reportCopyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Util.copyToClipboard(reportTextArea.getText());
                reportCopyButton.setText("已复制√");
                Timer timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        reportCopyButton.setText("复制报告内容");
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        logCopyButton = new JButton("复制游戏日志");
        logCopyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Util.copyToClipboard(gameErrorLog);
                logCopyButton.setText("已复制√");
                Timer timer = new Timer(1000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        logCopyButton.setText("复制游戏日志");
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }

    protected void initLayout() {
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

    public void updateReport(String s) {
        this.reportTextArea.setText(s);
    }

    public void appendReport(String s) {
        this.reportTextArea.append(s);
    }

    public void setGameErrorLog(String s) {
        this.gameErrorLog = s;
    }
}
