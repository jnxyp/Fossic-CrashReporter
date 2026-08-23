package net.jnxyp.fossic.crashreporter.collectors;

import net.jnxyp.fossic.crashreporter.Config;
import net.jnxyp.fossic.crashreporter.GamePaths;
import net.jnxyp.fossic.crashreporter.models.info.GameInfo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameInfoCollectorTest {
    @TempDir
    Path tempDirectory;

    @Test
    void reportsCurrentLocalizationVersionAndBranch() throws IOException {
        GameInfoCollector collector = collectorWithVersionFile("""
                {
                  "author": "远星汉化组",
                  "version": "2026.08.22",
                  "branch": "startup-optimization"
                }
                """);

        collector.collectLocalizationInfo();

        GameInfo info = collector.getInfo();
        assertEquals(GameInfo.LocalizationStatus.DETECTED, info.localizationStatus);
        assertEquals("2026.08.22", info.localizationVersionString);
        assertEquals("startup-optimization", info.localizationBranchString);
        assertTrue(info.toString().contains("汉化版本：\t\t2026.08.22"));
        assertTrue(info.toString().contains("汉化分支：\t\tstartup-optimization"));
        assertFalse(info.hasError());
    }

    @Test
    void supportsLegacyLocalizationWithoutBranch() throws IOException {
        GameInfoCollector collector = collectorWithVersionFile("""
                {
                  "author": "远星汉化组",
                  "version": "1.0.0"
                }
                """);

        collector.collectLocalizationInfo();

        GameInfo info = collector.getInfo();
        assertEquals(GameInfo.LocalizationStatus.DETECTED, info.localizationStatus);
        assertEquals(GameInfoCollector.LEGACY_BRANCH_DESCRIPTION, info.localizationBranchString);
        assertFalse(info.hasError());
    }

    @Test
    void reportsNotDetectedWhenVersionFileIsMissing() {
        Config.getInstance().initializeGamePaths(new GamePaths(tempDirectory));
        GameInfoCollector collector = new GameInfoCollector();

        collector.collectLocalizationInfo();

        GameInfo info = collector.getInfo();
        assertEquals(GameInfo.LocalizationStatus.NOT_DETECTED, info.localizationStatus);
        assertTrue(info.toString().contains("未检测到远星汉化组汉化包"));
        assertFalse(info.hasError());
    }

    @Test
    void ignoresVersionFilesFromOtherLocalizationTeams() throws IOException {
        GameInfoCollector collector = collectorWithVersionFile("""
                {
                  "author": "其他汉化组",
                  "version": "9.9.9",
                  "branch": "custom"
                }
                """);

        collector.collectLocalizationInfo();

        GameInfo info = collector.getInfo();
        assertEquals(GameInfo.LocalizationStatus.NOT_DETECTED, info.localizationStatus);
        assertTrue(info.toString().contains("未检测到远星汉化组汉化包"));
        assertFalse(info.hasError());
    }

    @Test
    void reportsPartialFailureForMalformedVersionFile() throws IOException {
        GameInfoCollector collector = collectorWithVersionFile("{not valid json");

        collector.collectLocalizationInfo();

        GameInfo info = collector.getInfo();
        assertEquals(GameInfo.LocalizationStatus.READ_FAILED, info.localizationStatus);
        assertTrue(info.toString().contains("读取 localization_version.json 失败"));
        assertTrue(info.hasError());
    }

    @Test
    void reportsPartialFailureWhenOwnVersionIsMissing() throws IOException {
        GameInfoCollector collector = collectorWithVersionFile("""
                {
                  "author": "远星汉化组",
                  "branch": "master"
                }
                """);

        collector.collectLocalizationInfo();

        GameInfo info = collector.getInfo();
        assertEquals(GameInfo.LocalizationStatus.READ_FAILED, info.localizationStatus);
        assertTrue(info.hasError());
    }

    private GameInfoCollector collectorWithVersionFile(String content) throws IOException {
        GamePaths gamePaths = new GamePaths(tempDirectory);
        Path versionPath = gamePaths.getLocalizationVersionPath();
        Files.createDirectories(versionPath.getParent());
        Files.writeString(versionPath, content, StandardCharsets.UTF_8);
        Config.getInstance().initializeGamePaths(gamePaths);
        return new GameInfoCollector();
    }
}
