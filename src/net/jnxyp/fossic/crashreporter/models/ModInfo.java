package net.jnxyp.fossic.crashreporter.models;

import java.util.ArrayList;
import java.util.List;

public class ModInfo extends BaseInfo {
    public List<Mod> mods;

    public ModInfo() {
        mods = new ArrayList<>();
    }


    @Override
    public String getName() {
        return "Mod信息";
    }

    @Override
    public String asMarkdown() {
        StringBuilder builder = new StringBuilder();
        String rowTemplate = "|%-32s|%-8s|%-8s|\n";
        builder.append(String.format(rowTemplate, "Mod名称", "Mod版本", "已启用"));
        builder.append("|-|-|-|\n");
        for (Mod info : mods) {
            builder.append(String.format("|%-32s|%-8s|%-8s|\n", info.name, info.version, info.enabled ? "是" : "否"));
        }

        return builder.toString();
    }

    @Override
    public String asText() {
        StringBuilder builder = new StringBuilder();
        String rowTemplate = "|%-32s|%-8s|%-8s|\n";
        builder.append(String.format(rowTemplate, "Mod名称", "Mod版本", "已启用"));
        builder.append("|-|-|-|\n");
        for (Mod info : mods) {
            builder.append(String.format("|%-32s|%-8s|%-8s|\n", info.name, info.version, info.enabled ? "是" : "否"));
        }

        return builder.toString();
    }
}
