package net.jnxyp.fossic.crashreporter.models.info;

public class GameInfo extends BaseInfo{
    public enum LocalizationStatus {
        NOT_DETECTED,
        DETECTED,
        READ_FAILED
    }

    public String versionString;
    public LocalizationStatus localizationStatus = LocalizationStatus.NOT_DETECTED;
    public String localizationVersionString;
    public String localizationBranchString;

    @Override
    public String getName() {
        return "游戏信息";
    }

    @Override
    protected String asMarkdown() {
        return formatReport();
    }

    @Override
    protected String asText() {
        return formatReport();
    }

    private String formatReport() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("游戏版本：\t\t%s\n", versionString));

        switch (localizationStatus) {
            case DETECTED:
                builder.append(String.format("汉化版本：\t\t%s\n", localizationVersionString));
                builder.append(String.format("汉化分支：\t\t%s\n", localizationBranchString));
                break;
            case READ_FAILED:
                builder.append("汉化信息：\t\t读取 localization_version.json 失败\n");
                break;
            case NOT_DETECTED:
            default:
                builder.append("汉化信息：\t\t未检测到远星汉化组汉化包\n");
                break;
        }

        return builder.toString();
    }
}
