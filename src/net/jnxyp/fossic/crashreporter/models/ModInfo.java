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
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Mod名称\t\t\tMod版本\t\t已启用\n");
        for (Mod info : mods) {
            builder.append(String.format("%s\t\t\t%s\t\t%s\n", info.name, info.version, info.enabled ? "是" : "否"));
        }

        return builder + super.toString();
    }

    @Override
    public String toMarkdown() {
        StringBuilder builder = new StringBuilder();
        builder.append("|Mod名称|Mod版本|已启用|\n");
        builder.append("|-|-|-|\n");
        for (Mod info : mods) {
            builder.append(String.format("|%s|%s|%s|\n", info.name, info.version, info.enabled ? "是" : "否"));
        }

        return builder + super.toString();
    }
}
