package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;
import net.jnxyp.fossic.crashreporter.models.info.GameInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameInfoCollector extends BaseInfoCollector{
    static final String LOCALIZATION_AUTHOR = "远星汉化组";
    static final String LEGACY_BRANCH_DESCRIPTION = "未记录（旧版汉化包）";

    public GameInfoCollector() {
        info = new GameInfo();
    }

    @Override
    public void tryCollectInfo() {
        try {
            URL url = new URL("jar:file:" + Config.getInstance().getStarsectorObfPath() + "!/com/fs/starfarer/Version.class");
            String content = Util.readInputStream(url.openStream());
            Pattern gameVersionPattern = Pattern.compile("Starsector ([- .0-9aRC]+)");
            Matcher m = gameVersionPattern.matcher(content);
            m.find();
            getInfo().versionString = m.group(1);
        } catch (IOException e) {
            getInfo().addError(new InfoCollectionPartialFailureException(this, "在读取游戏版本文件时发生错误", e));
        } catch (IllegalStateException | IndexOutOfBoundsException e) {
            getInfo().addError(new InfoCollectionPartialFailureException(this, "在游戏版本文件中未找到版本号", e));
        }

        collectLocalizationInfo();

        infoCollected = true;
    }

    void collectLocalizationInfo() {
        Path versionPath = Config.getInstance().getLocalizationVersionPath();
        if (!Files.isRegularFile(versionPath)) {
            return;
        }

        try {
            String content = Util.readFile(versionPath.toFile(), Config.LOCALIZATION_VERSION_CHARSET);
            JSONObject versionInfo = Util.parseJson(content);
            String author = optionalString(versionInfo, "author");
            if (!LOCALIZATION_AUTHOR.equals(author)) {
                return;
            }

            String version = requiredString(versionInfo, "version");
            String branch = optionalString(versionInfo, "branch");

            getInfo().localizationVersionString = version;
            getInfo().localizationBranchString = branch == null ? LEGACY_BRANCH_DESCRIPTION : branch;
            getInfo().localizationStatus = GameInfo.LocalizationStatus.DETECTED;
        } catch (IOException | JSONException | IllegalArgumentException e) {
            getInfo().localizationStatus = GameInfo.LocalizationStatus.READ_FAILED;
            getInfo().addError(new InfoCollectionPartialFailureException(
                    this,
                    String.format("在读取汉化版本文件 %s 时发生错误", versionPath),
                    e
            ));
        }
    }

    private static String requiredString(JSONObject object, String key) {
        String value = optionalString(object, key);
        if (value == null) {
            throw new IllegalArgumentException(String.format("字段 %s 缺失或为空", key));
        }
        return value;
    }

    private static String optionalString(JSONObject object, String key) {
        if (!object.has(key) || object.isNull(key)) {
            return null;
        }
        Object value = object.get(key);
        if (!(value instanceof String)) {
            throw new IllegalArgumentException(String.format("字段 %s 必须是字符串", key));
        }
        String text = ((String) value).trim();
        return text.isEmpty() ? null : text;
    }

    @Override
    public GameInfo getInfo() {
        return (GameInfo) info;
    }
}
