package net.jnxyp.fossic.crashreporter.views;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.models.SystemInfo;
import net.jnxyp.fossic.crashreporter.models.VmParams;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;


public class MemorySettingTabPanel extends TabPanel {
    protected SystemInfo systemInfo;
    protected int memoryValue;

    protected GridBagLayout gbl;

    protected JLabel memoryValueLabel;
    protected JLabel descriptionLabel;
    protected JSlider memoryValueSlider;
    protected JButton saveButton;

    public MemorySettingTabPanel(SystemInfo systemInfo) {
        this.systemInfo = systemInfo;
        this.memoryValue = systemInfo.vmParams.getXmx();

        this.gbl = new GridBagLayout();
        this.setLayout(gbl);
    }

    @Override
    public String getTabName() {
        return "内存设置";
    }

    @Override
    public void init() {

        memoryValueLabel = new JLabel();
        memoryValueLabel.setFont(memoryValueLabel.getFont().deriveFont(64.0f));
        memoryValueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        descriptionLabel = new JLabel("拖动下方滑条来设置游戏可以使用的最大内存大小。");
        descriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        descriptionLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        memoryValueSlider = generateMemorySlider();
        saveButton = new JButton("保存");

        final MemorySettingTabPanel that = this;
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveMemoryConfig();
                    JOptionPane.showMessageDialog(that, "保存成功");
                } catch (IOException exception) {
                    JOptionPane.showMessageDialog(that, "内存设置保存失败：\n" + Util.getStackTrace(exception));
                }
            }
        });

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.weightx = 0.6;
        gbc.weighty = 0.3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbl.setConstraints(memoryValueLabel, gbc);
        this.add(memoryValueLabel);

        gbc.gridy = 1;
        gbc.weighty = 0.1;
        gbl.setConstraints(descriptionLabel, gbc);
        this.add(descriptionLabel);


        gbc.gridy = 2;
        gbc.weighty = 0.3;
        gbl.setConstraints(memoryValueSlider, gbc);
        this.add(memoryValueSlider);

        gbc.gridy = 3;
        gbc.weighty = 0;
        gbl.setConstraints(saveButton, gbc);
        gbc.fill = GridBagConstraints.NONE;
        this.add(saveButton);

        update();
    }

    protected void update() {
        memoryValueLabel.setText(memoryValue + " M");
    }

    public void setMemoryValue(int memoryValue) {
        this.memoryValue = memoryValue;
        update();
    }

    public void saveMemoryConfig() throws IOException {
        VmParams vmParams = systemInfo.vmParams;
        vmParams.setXmx(memoryValue);
        vmParams.setXms(memoryValue);
        vmParams.save();
    }

    public JSlider generateMemorySlider() {
        int ram = (int) (systemInfo.totalRam / 1024.0 / 1024.0);
        int currentValue = systemInfo.vmParams.getXmx();

        ArrayList<Integer> memoryLevels = new ArrayList<>();
        Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>();

        for (int i : Config.SETTING_MEMORY_LEVELS) {
            if (i < ram) {
                memoryLevels.add(i);
                JLabel label = new JLabel(String.format("%d M", i));
                label.setFont(Config.UI_FONT.deriveFont(12.0f));
                sliderLabels.put(i, label);
            }
        }

        currentValue = Math.min(currentValue, Collections.max(memoryLevels));

        JSlider slider = new JSlider(JSlider.HORIZONTAL, Collections.min(memoryLevels), Collections.max(memoryLevels), currentValue);

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setMemoryValue(((JSlider) e.getSource()).getValue());
            }
        });

        slider.setLabelTable(sliderLabels);
        slider.setMinorTickSpacing(128);
        slider.setMajorTickSpacing(256);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);

        return slider;
    }

}
