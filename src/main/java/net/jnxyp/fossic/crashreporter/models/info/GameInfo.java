package net.jnxyp.fossic.crashreporter.models.info;

public class GameInfo extends BaseInfo{
    public String versionString;

    @Override
    public String getName() {
        return "游戏信息";
    }

    @Override
    protected String asMarkdown() {
        return String.format("游戏版本：\t\t%s\n", versionString);
    }

    @Override
    protected String asText() {
        return String.format("游戏版本：\t\t%s\n", versionString);
    }
}
