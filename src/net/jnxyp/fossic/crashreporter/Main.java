package net.jnxyp.fossic.crashreporter;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0) {
            new CrashReporter(Paths.get(args[0]));
        } else {
            new CrashReporter();
        }
    }
}
