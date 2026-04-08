package net.jnxyp.fossic.crashreporter;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        try {
            GamePaths gamePaths = parseGamePaths(args);
            Config.getInstance().initializeGamePaths(gamePaths);
            new CrashReporter(gamePaths);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.err.println("Usage: --game-path <path>");
        }
    }

    static GamePaths parseGamePaths(String[] args) {
        Path gameRoot = Paths.get("");

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--game-path".equals(arg)) {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException("Missing value for --game-path");
                }
                gameRoot = Paths.get(args[++i]);
            } else {
                throw new IllegalArgumentException("Unknown argument: " + arg);
            }
        }

        return new GamePaths(gameRoot);
    }
}
