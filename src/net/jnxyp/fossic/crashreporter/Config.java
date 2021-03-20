package net.jnxyp.fossic.crashreporter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

public final class Config {
    public static final String PROGRAM_NAME = "远行星号 报错信息收集工具";
    public static final String PROGRAM_VERSION = "1.0.3";

    public static final Pattern JSON_COMMENTS_PATTERN = Pattern.compile("#.*$", Pattern.MULTILINE);

    public static final String GAME_DEFAULT_JRE_VERSION = "1.7.0_79";
    public static final String[] RELATIVE_GAME_DEFAULT_JRE_PATH = {"jre"};
    public static final Pattern VMPARAMS_MEMORY_VALUE_PATTERN = Pattern.compile("(-Xms.*?|-Xmx.*?) ");

    public Path GAME_PATH = Paths.get("");
    public static final String[] RELATIVE_VMPARAMS_PATH = {"vmparams"};
    public static final String[] RELATIVE_MOD_PATH = {"mods"};
    public static final String[] RELATIVE_ENABLED_MOD_LIST_PATH = {"mods", "enabled_mods.json"};
    public static final String[] RELATIVE_MOD_INFO_PATH = {"mod_info.json"};
    public static final String[] RELATIVE_LOG_PATH = {"starsector-core", "starsector.log"};
    public static final String[] PRIORITY_MOD_IDS = {"lw_lazylib", "shaderLib", "MagicLib"};

    public static final Charset LOG_CHARSET = Charset.forName("GB2312");
    public static final int MAX_LOG_CHECK_LINES = 500;
    public static final int LOG_LINES_IF_NO_EXCEPTION = 50;
    public static final int LOG_LINES_ABOVE_EXCEPTION = 3;
    public static final Pattern LOG_EXCEPTION_LINE_PATTERN = Pattern.compile("ERROR");


    public static final Charset VMPARAMS_CHARSET = StandardCharsets.UTF_8;
    public static final Charset MOD_INFO_CHARSET = StandardCharsets.UTF_8;
    public static final Charset ENABLED_MOD_LIST_CHARSET = StandardCharsets.UTF_8;

    private Config() {
    }

    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public Path getGamePath() {
        return GAME_PATH;
    }

    public Path getVmparamsPath() {
        return Paths.get(getGamePath().toString(), RELATIVE_VMPARAMS_PATH);
    }

    public Path getModPath() {
        return Paths.get(getGamePath().toString(), RELATIVE_MOD_PATH);
    }

    public Path getLogPath() {
        return Paths.get(getGamePath().toString(), RELATIVE_LOG_PATH);
    }

    public Path getGameDefaultJrePath() {
        return Paths.get(getGamePath().toString(), RELATIVE_GAME_DEFAULT_JRE_PATH);
    }

    public Path getEnabledModListPath() {
        return Paths.get(getGamePath().toString(), RELATIVE_ENABLED_MOD_LIST_PATH);
    }
}
