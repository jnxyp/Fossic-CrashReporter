package net.jnxyp.fossic.crashreporter;

import org.json.JSONObject;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public final class Util {
    public static void copyToClipboard(String s) {
        StringSelection stringSelection = new StringSelection(s);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static List<String> readLastNLines(File file, Charset charset, int n) throws IOException {
        List<String> lines = Arrays.asList(readFile(file, charset).split("\n"));
        int endIndex = lines.size();
        int startIndex = Math.max(endIndex - n, 0);
        return lines.subList(startIndex, endIndex);
    }

    public static String readFile(File file, Charset charset) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        return new String(bytes, charset);
    }

    public static void writeTextFile(File file, String text, Charset charset) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), charset);
        writer.write(text);
        writer.flush();
        writer.close();
    }

    public static String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static JSONObject parseJson(String json) {
        String jsonWithNoComment = Config.JSON_COMMENTS_PATTERN.matcher(json).replaceAll("");
        return new JSONObject(jsonWithNoComment);
    }

    public static String getOSType() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "WINDOWS";
        } else if (os.contains("mac")) {
            return "MACOS";
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return "UNIX";
        } else {
            return "WTF is this";
        }
    }

    public static String runCommand(String[] args, File workingDirectory) throws IOException {
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.directory(workingDirectory);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        StringBuilder output  = new StringBuilder();
        String line;
        while (true) {
            line = reader.readLine();
            if (line == null) {
                break;
            }
            output.append(line);
            output.append("\n");
        }
        return output.toString();
    }

    public static String runCommand(String[] args) throws IOException {
        return runCommand(args, Config.getInstance().getGamePath().toFile());
    }
}
