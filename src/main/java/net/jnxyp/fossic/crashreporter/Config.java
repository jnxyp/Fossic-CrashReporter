package net.jnxyp.fossic.crashreporter;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.regex.Pattern;

public final class Config {
    public static final String PROGRAM_NAME = "远行星号 报错信息收集工具";
    public static final String PROGRAM_VERSION = "1.1.3";
    public static final String AUTHOR_INFO = "远行星号中文论坛 jn_xyp (jinan.xyp@gmail.com)";

    public static final Font UI_FONT = UIManager.getFont("Label.font").deriveFont(16.0f);
    public static final Font REPORT_FONT = new FontUIResource(FontUIResource.MONOSPACED, Font.PLAIN, 14);

    private GamePaths gamePaths = GamePaths.fromWorkingDirectory();

    public static final Pattern JSON_COMMENTS_PATTERN = Pattern.compile("#.*$", Pattern.MULTILINE);

    public static final String GAME_JRE_DEFAULT_VERSION = "1.7.0_79";

    public static final int VMPARAMS_XMS_DEFAULT_VALUE = 1536;
    public static final int VMPARAMS_XMX_DEFAULT_VALUE = 1536;
    public static final Charset VMPARAMS_CHARSET = StandardCharsets.UTF_8;

    public static final int[] SETTING_MEMORY_LEVELS = {1024, 1536, 2048, 3072, 4096, 6144, 8192};

    public static final Charset ENABLED_MOD_LIST_CHARSET = StandardCharsets.UTF_8;
    public static final Charset MOD_INFO_CHARSET = StandardCharsets.UTF_8;
    public static final String[] PRIORITY_MOD_IDS = {"lw_lazylib", "shaderLib", "MagicLib", "dronelib"};

    public static final Charset LOG_CHARSET = Charset.forName("GB2312");
    public static final int MAX_LOG_CHECK_LINES = 500;
    public static final int LOG_LINES_IF_NO_EXCEPTION = 50;
    public static final int LOG_LINES_ABOVE_EXCEPTION = 3;

    public static final String URL_CLOUD_PASTEBOARD = "https://cp.api.jnxyp.net/";
    public static final String URL_FOSSIC_CRASH_REPORT = "https://www.fossic.org/forum.php?mod=post&action=newthread&fid=41&extra=page%3D1&sortid=6";

    private Config() {
    }

    private static Config instance;

    public static Config getInstance() {
        if (instance == null) {
            instance = new Config();
        }
        return instance;
    }

    public void initializeGamePaths(GamePaths gamePaths) {
        this.gamePaths = gamePaths;
    }

    public GamePaths getGamePaths() {
        return gamePaths;
    }

    public Path getGamePath() {
        return gamePaths.getGameRoot();
    }

    public Path getVmparamsPath() {
        return gamePaths.getVmparamsPath();
    }

    public Path getModPath() {
        return gamePaths.getModsPath();
    }

    public Path getLogPath() {
        return gamePaths.getLogPath();
    }

    public Path getGameJrePath() {
        return gamePaths.getGameJrePath();
    }

    public Path getGameJreExePath() {
        return gamePaths.getGameJreExePath();
    }

    public Path getEnabledModListPath() {
        return gamePaths.getEnabledModListPath();
    }

    public Path getStarsectorObfPath() {
        return gamePaths.getStarsectorObfPath();
    }
}
