package net.jnxyp.fossic.crashreporter.models.info;

import net.jnxyp.fossic.crashreporter.models.Mod;

import java.util.ArrayList;
import java.util.List;

public class ModInfo extends BaseInfo {
    private static final String HEADER_NAME = "Mod名称";
    private static final String HEADER_VERSION = "Mod版本";
    private static final String HEADER_ENABLED = "已启用";

    public List<Mod> mods;
    public boolean foundEnabledModList;

    public ModInfo() {
        mods = new ArrayList<>();
        foundEnabledModList = true;
    }


    @Override
    public String getName() {
        return "Mod信息";
    }

    @Override
    public String asMarkdown() {
        StringBuilder builder = new StringBuilder();
        int[] columnWidths = getColumnWidths();
        String rowTemplate = getMarkdownRowTemplate(columnWidths);
        if (!foundEnabledModList) {
            builder.append("未找到 `enabled_mods.json`，所有 Mod 均按未启用显示。\n\n");
        }
        builder.append(String.format(rowTemplate, HEADER_NAME, HEADER_VERSION, HEADER_ENABLED));
        builder.append("|-|-|-|\n");
        for (Mod info : mods) {
            builder.append(String.format(rowTemplate, info.name, info.version, getEnabledText(info)));
        }

        return builder.toString();
    }

    @Override
    public String asText() {
        StringBuilder builder = new StringBuilder();
        int[] columnWidths = getColumnWidths();
        String rowTemplate = getTextRowTemplate(columnWidths);
        builder.append(String.format(rowTemplate, HEADER_NAME, HEADER_VERSION, HEADER_ENABLED));
        builder.append(getTextSeparator(columnWidths));
        if (!foundEnabledModList) {
            builder.append("未找到 enabled_mods.json，所有 Mod 均按未启用显示。\n\n");
        }
        for (Mod info : mods) {
            builder.append(String.format(rowTemplate, info.name, info.version, getEnabledText(info)));
        }

        return builder.toString();
    }

    private int[] getColumnWidths() {
        int nameWidth = HEADER_NAME.length();
        int versionWidth = HEADER_VERSION.length();
        int enabledWidth = HEADER_ENABLED.length();

        for (Mod info : mods) {
            nameWidth = Math.max(nameWidth, getLength(info.name));
            versionWidth = Math.max(versionWidth, getLength(info.version));
            enabledWidth = Math.max(enabledWidth, getLength(getEnabledText(info)));
        }

        return new int[]{nameWidth, versionWidth, enabledWidth};
    }

    private String getMarkdownRowTemplate(int[] columnWidths) {
        return String.format("|%%-%ds|%%-%ds|%%-%ds|\n", columnWidths[0], columnWidths[1], columnWidths[2]);
    }

    private String getTextRowTemplate(int[] columnWidths) {
        return String.format("%%-%ds|%%-%ds|%%-%ds\n", columnWidths[0], columnWidths[1], columnWidths[2]);
    }

    private String getTextSeparator(int[] columnWidths) {
        StringBuilder builder = new StringBuilder();
        int separatorLength = columnWidths[0] + columnWidths[1] + columnWidths[2] + 2;
        for (int i = 0; i < separatorLength; i++) {
            builder.append('=');
        }
        builder.append('\n');
        return builder.toString();
    }

    private String getEnabledText(Mod info) {
        return info.enabled ? "是" : "否";
    }

    private int getLength(String value) {
        return value == null ? 0 : value.length();
    }
}
