package net.jnxyp.fossic.crashreporter;

import org.json.JSONObject;

import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
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
}
