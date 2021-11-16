package net.jnxyp.fossic.crashreporter;

import org.json.JSONObject;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Clipboard;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
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
        StringBuilder output = new StringBuilder();
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

    public static String runCommand(String command) throws IOException {
        return runCommand(command.split(" "));
    }


    public static void openInBrowser(URL url) throws IOException {
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (Exception e) {
            // From: https://stackoverflow.com/questions/5226212/how-to-open-the-default-webbrowser-using-java
            switch (getOSType()) {
                case "WINDOWS":
                    runCommand("rundll32 url.dll,FileProtocolHandler " + url);
                case "MACOS":
                    runCommand("open " + url);
                case "UNIX":
                    String[] browsers = {"google-chrome", "firefox", "mozilla", "epiphany", "konqueror",
                            "netscape", "opera", "links", "lynx"};
                    StringBuilder cmd = new StringBuilder();
                    for (int i = 0; i < browsers.length; i++)
                        if (i == 0)
                            cmd.append(String.format("%s \"%s\"", browsers[i], url));
                        else
                            cmd.append(String.format(" || %s \"%s\"", browsers[i], url));
                    runCommand("sh -c " + cmd);
            }
        }
    }

    // From: https://stackoverflow.com/questions/1359689/how-to-send-http-request-in-java
    public static String sendPost(URL url, String urlParameters) throws IOException {
        HttpURLConnection connection = null;

        try {
            // Create connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "zh-CN");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            // Send request
            DataOutputStream wr = new DataOutputStream(
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            // Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
