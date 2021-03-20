package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionFailureException;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class ModInfoCollector extends BaseInfoCollector {
    @Override
    public String getName() {
        return "Mod信息";
    }

    @Override
    public String getRawInfo() throws InfoCollectionPartialFailureException {
        File modsFolder = Config.getInstance().getModPath().toFile();

        ArrayList<ModInfo> mods = new ArrayList<>();
        File[] modFolders = {};
        try {
            modFolders = Objects.requireNonNull(modsFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            }));
        } catch (NullPointerException e) {
            throw new InfoCollectionPartialFailureException(this, "在遍历Mod文件夹时发生错误", e);
        }
        for (File modFolder : modFolders) {
            File modInfoFile = Paths.get(modFolder.toString(), Config.RELATIVE_MOD_INFO_PATH).toFile();
            try {
                mods.add(ModInfo.fromModInfoFile(modInfoFile));
            } catch (IOException | JSONException e) {
                throw new InfoCollectionPartialFailureException(this, String.format("在读取Mod信息文件 %s 时发生错误", modInfoFile.toPath().toString()), e);
            }
        }

        ArrayList<String> enabledModIds = new ArrayList<>();
        File enabledModListFile = Config.getInstance().getEnabledModListPath().toFile();
        try {
            String modListString = Util.readFile(enabledModListFile, Config.ENABLED_MOD_LIST_CHARSET);
            JSONArray array = Util.parseJson(modListString).getJSONArray("enabledMods");
            for (int i = 0; i < array.length(); i++) {
                enabledModIds.add(array.getString(i));
            }
        } catch (IOException e) {
            throw new InfoCollectionPartialFailureException(this, String.format("在读取已启用Mod列表文件 %s 时发生错误", enabledModListFile.toPath().toString()), e);
        }

        for (ModInfo info : mods) {
            if (enabledModIds.contains(info.id)) {
                info.enabled = true;
            }
        }

        Collections.sort(mods);

        StringBuilder builder = new StringBuilder();
        builder.append("|Mod名称|Mod版本|已启用|\n");
        builder.append("|-|-|-|\n");
        for (ModInfo info : mods) {
            builder.append(String.format("|%s|%s|%s|\n", info.name, info.version, info.enabled ? "是" : "否"));
        }

        return builder.toString();
    }

    static class ModInfo implements Comparable<ModInfo> {
        String id;
        String name;
        String version;
        String gameVersion;
        boolean enabled;

        public ModInfo(String id, String name, String version, String gameVersion, boolean enabled) {
            this.id = id;
            this.name = name;
            this.version = version;
            this.gameVersion = gameVersion;
            this.enabled = enabled;
        }

        public static ModInfo fromModInfoFile(File modInfoFile) throws IOException {
            String modInfoString = Util.readFile(modInfoFile, Config.MOD_INFO_CHARSET);
            JSONObject dict = Util.parseJson(modInfoString);
            return new ModInfo(
                    dict.getString("id"),
                    dict.getString("name"),
                    dict.getString("version"),
                    dict.getString("gameVersion"),
                    false);
        }

        @Override
        public int compareTo(ModInfo o) {
            List<String> priorityModIds = Arrays.asList(Config.PRIORITY_MOD_IDS);
            int thisPriority = priorityModIds.indexOf(this.id);
            int oPriority = priorityModIds.indexOf(o.id);
            if (thisPriority != oPriority) {
                if (thisPriority == -1) {
                    return 1;
                } else if (oPriority == -1) {
                    return -1;
                } else {
                    return thisPriority - oPriority;
                }
            }
            if (this.enabled != o.enabled) {
                return this.enabled ? -1 : 1;
            }
            return this.name.compareToIgnoreCase(o.name);
        }
    }
}
