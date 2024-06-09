package net.toaru.sidenplugin.utils;

import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class URLUtils
{
    public static String getAsString(String urlString)
    {
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (url.getHost().equals("file.io"))
                connection.setRequestProperty("Referer", "https://www.file.io/");
            connection.setRequestProperty("User-Agent", "Mozilla/8.10; Safari/Chrome/Opera/Edge/KungleBot-Peyang; Mobile-Desktop");
            connection.connect();

            if (connection.getResponseCode() == 204 || connection.getResponseCode() == 404)
                return "E: プレイヤーが見つかりませんでした。";

            if (connection.getResponseCode() != 200)
                return "E: " + connection.getResponseCode() + "エラー";

            return IOUtils.toString(connection.getInputStream(), StandardCharsets.UTF_8);

        }
        catch (Exception e)
        {
            return "E: 例外が発生しました：" + e.getClass();
        }
    }

    public static int fetch(String urlString, String method)
    {
        try
        {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);
            connection.setRequestProperty("User-Agent", "Mozilla/8.10; Safari/Chrome/Opera/Edge/KungleBot-Peyang; Mobile-Desktop");
            connection.connect();
            return connection.getResponseCode();

        }
        catch (Exception e)
        {
            return 500;
        }
    }
}
