package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;
import net.jnxyp.fossic.crashreporter.models.Mod;
import net.jnxyp.fossic.crashreporter.models.info.ModInfo;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

public class ModsInfoCollector extends BaseInfoCollector {

    public ModsInfoCollector() {
        info = new ModInfo();
    }


    @Override
    public void tryCollectInfo() {
        collectModInfo();
        collectEnabledModInfo();
        infoCollected = true;
    }

    protected void collectModInfo() {
        File modsFolder = Config.getInstance().getModPath().toFile();

        File[] modFolders = new File[]{};
        try {
            modFolders = Objects.requireNonNull(modsFolder.listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.isDirectory();
                }
            }));
        } catch (NullPointerException e) {
            getInfo().addError(new InfoCollectionPartialFailureException(this, "在遍历Mod文件夹时发生错误", e));
        }
        for (File modFolder : modFolders) {
            File modInfoFile = Paths.get(modFolder.toString(), "mod_info.json").toFile();
            if (modInfoFile.exists()) {
                try {
                    getInfo().mods.add(Mod.fromModInfoFile(modInfoFile));
                } catch (IOException | JSONException e) {
                    getInfo().addError(new InfoCollectionPartialFailureException(this, String.format("在读取Mod信息文件 %s 时发生错误", modInfoFile.toPath()), e));
                }
            }
        }
    }

    protected void collectEnabledModInfo()  {
        ArrayList<String> enabledModIds = new ArrayList<>();
        File enabledModListFile = Config.getInstance().getEnabledModListPath().toFile();
        try {
            String modListString = Util.readFile(enabledModListFile, Config.ENABLED_MOD_LIST_CHARSET);
            JSONArray array = Util.parseJson(modListString).getJSONArray("enabledMods");
            for (int i = 0; i < array.length(); i++) {
                enabledModIds.add(array.getString(i));
            }
        } catch (IOException e) {
            getInfo().addError(new InfoCollectionPartialFailureException(this, String.format("在读取已启用Mod列表文件 %s 时发生错误", enabledModListFile.toPath()), e));
        }

        for (Mod info : getInfo().mods) {
            if (enabledModIds.contains(info.id)) {
                info.enabled = true;
            }
        }

        Collections.sort(getInfo().mods);
    }

    @Override
    public ModInfo getInfo() {
        return (ModInfo) info;
    }


}
