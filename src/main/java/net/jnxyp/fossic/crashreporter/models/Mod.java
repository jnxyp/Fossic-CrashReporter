package net.jnxyp.fossic.crashreporter.models;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mod implements Comparable<Mod> {
    public String id;
    public String name;
    public String version;
    public String gameVersion;
    public boolean enabled;

    public Mod(String id, String name, String version, String gameVersion, boolean enabled) {
        this.id = id;
        this.name = name;
        this.version = version;
        this.gameVersion = gameVersion;
        this.enabled = enabled;
    }

    public static Mod fromModInfoFile(File modInfoFile) throws IOException {
        String modInfoString = Util.readFile(modInfoFile, Config.MOD_INFO_CHARSET);
        JSONObject dict = Util.parseJson(modInfoString);
        return new Mod(
                dict.getString("id"),
                dict.getString("name"),
                parseVersion(dict),
                dict.getString("gameVersion"),
                false);
    }

    private static String parseVersion(JSONObject dict) {
        if (!dict.has("version")) {
            return "";
        }

        Object versionObj = dict.get("version");
        if (versionObj instanceof JSONObject) {
            return parseVersionObject((JSONObject) versionObj);
        }
        return versionObj.toString();
    }

    private static String parseVersionObject(JSONObject versionObj) {
        ArrayList<String> parts = new ArrayList<>();
        appendVersionPart(parts, versionObj, "major");
        appendVersionPart(parts, versionObj, "minor");
        appendVersionPart(parts, versionObj, "patch");
        return joinVersionParts(parts);
    }

    private static void appendVersionPart(List<String> parts, JSONObject versionObj, String key) {
        if (versionObj.has(key)) {
            parts.add(versionObj.get(key).toString());
        }
    }

    private static String joinVersionParts(List<String> parts) {
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (builder.length() > 0) {
                builder.append('.');
            }
            builder.append(part);
        }
        return builder.toString();
    }

    @Override
    public int compareTo(Mod o) {
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
