package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.Util;
import net.jnxyp.fossic.crashreporter.exceptions.InfoCollectionPartialFailureException;
import net.jnxyp.fossic.crashreporter.models.info.GameInfo;
import net.jnxyp.fossic.crashreporter.models.info.SystemInfo;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameInfoCollector extends BaseInfoCollector{

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

        infoCollected = true;
    }

    @Override
    public GameInfo getInfo() {
        return (GameInfo) info;
    }
}
