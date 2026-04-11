package net.jnxyp.fossic.crashreporter.views;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.models.info.SystemInfo;
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
    private static final int MEMORY_TICK_UNIT = 512;

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
        memoryValueLabel.setText(formatMemorySize(memoryValue));
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
        int maxValue = Math.min(Config.SETTING_MEMORY_MAX_VALUE, ram - Config.SETTING_MEMORY_RESERVED_FOR_SYSTEM);
        maxValue = Math.max(1024, maxValue);

        ArrayList<Integer> memoryLevels = new ArrayList<>();

        for (int i : Config.SETTING_MEMORY_LEVELS) {
            if (i <= maxValue) {
                memoryLevels.add(i);
            }
        }
        if (!memoryLevels.contains(maxValue)) {
            memoryLevels.add(maxValue);
        }
        Collections.sort(memoryLevels);

        currentValue = Math.min(currentValue, Collections.max(memoryLevels));
        memoryValue = currentValue;

        int minValue = Collections.min(memoryLevels);
        maxValue = Collections.max(memoryLevels);
        JSlider slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue, currentValue);

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                setMemoryValue(((JSlider) e.getSource()).getValue());
            }
        });

        int majorTickSpacing = calculateMajorTickSpacing(minValue, maxValue);
        slider.setLabelTable(generateSliderLabels(minValue, maxValue, majorTickSpacing));
        slider.setMinorTickSpacing(MEMORY_TICK_UNIT);
        slider.setMajorTickSpacing(majorTickSpacing);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.setSnapToTicks(true);

        return slider;
    }

    private Hashtable<Integer, JLabel> generateSliderLabels(int minValue, int maxValue, int majorTickSpacing) {
        Hashtable<Integer, JLabel> sliderLabels = new Hashtable<>();
        sliderLabels.put(minValue, createSliderLabel(minValue));

        int firstMajorTick = roundUp(minValue, majorTickSpacing);
        for (int value = firstMajorTick; value <= maxValue; value += majorTickSpacing) {
            sliderLabels.put(value, createSliderLabel(value));
        }

        if (!sliderLabels.containsKey(maxValue)) {
            sliderLabels.put(maxValue, createSliderLabel(maxValue));
        }
        return sliderLabels;
    }

    private JLabel createSliderLabel(int value) {
        JLabel label = new JLabel(formatMemorySize(value));
        label.setFont(Config.UI_FONT.deriveFont(12.0f));
        return label;
    }

    private int calculateMajorTickSpacing(int minValue, int maxValue) {
        int range = Math.max(1, maxValue - minValue);
        int targetIntervalCount = 6;
        int rawSpacing = (int) Math.ceil(range / (double) targetIntervalCount);
        return roundUp(rawSpacing, MEMORY_TICK_UNIT);
    }

    private int roundUp(int value, int unit) {
        return ((value + unit - 1) / unit) * unit;
    }

    private String formatMemorySize(int memoryValue) {
        return String.format("%d M", memoryValue);
    }

}
