package net.jnxyp.fossic.crashreporter;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class GamePaths {
    private final Path gameRoot;

    public GamePaths(Path gameRoot) {
        this.gameRoot = gameRoot.toAbsolutePath().normalize();
    }

    public static GamePaths fromWorkingDirectory() {
        return new GamePaths(Paths.get(""));
    }

    public Path getGameRoot() {
        return gameRoot;
    }

    public Path getVmparamsPath() {
        return gameRoot.resolve("vmparams");
    }

    public Path getModsPath() {
        return gameRoot.resolve("mods");
    }

    public Path getModInfoPath(Path modFolder) {
        return modFolder.resolve("mod_info.json");
    }

    public Path getEnabledModListPath() {
        return getModsPath().resolve("enabled_mods.json");
    }

    public Path getLogPath() {
        if (Util.getOSType().equals("WINDOWS")) {
            return gameRoot.resolve("starsector-core").resolve("starsector.log");
        }
        return gameRoot.resolve("starsector.log");
    }

    public Path getGameJrePath() {
        if (Util.getOSType().equals("UNIX")) {
            return gameRoot.resolve("jre_linux");
        }
        return gameRoot.resolve("jre");
    }

    public Path getGameJreExePath() {
        return getGameJrePath().resolve("bin").resolve("java.exe");
    }

    public Path getStarsectorObfPath() {
        if (Util.getOSType().equals("WINDOWS")) {
            return gameRoot.resolve("starsector-core").resolve("starfarer_obf.jar");
        }
        return gameRoot.resolve("starfarer_obf.jar");
    }
}
