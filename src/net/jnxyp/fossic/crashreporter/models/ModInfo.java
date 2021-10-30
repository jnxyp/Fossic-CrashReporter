package net.jnxyp.fossic.crashreporter.models;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ModInfo implements Comparable<ModInfo> {
    public String id;
    public String name;
    public String version;
    public String gameVersion;
    public boolean enabled;

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
        Object versionObj = dict.get("version");
        String version = "";
        if (versionObj instanceof String) {
            version = (String) versionObj;
        } else if (versionObj instanceof JSONObject) {
            JSONObject v = (JSONObject) versionObj;
            version = String.format("%s.%s.%s", v.get("major").toString(), v.get("minor").toString(), v.get("patch").toString());
        }
        return new ModInfo(
                dict.getString("id"),
                dict.getString("name"),
                version,
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