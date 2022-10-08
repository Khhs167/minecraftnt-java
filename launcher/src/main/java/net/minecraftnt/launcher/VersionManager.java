package net.minecraftnt.launcher;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import static net.minecraftnt.launcher.Main.GSON;

public final class VersionManager {

    private static final String baseURL = "https://openability.tech/jimmster/minecraftnt/download/";

    public static void TryDownloadVersion(String version) throws IOException {

        File versionFilePath = new File(GameInfo.getVersionLocation(version) + "/version.json");
        String version_json = "";
        if(!versionFilePath.exists()){

            URL url = new URL(baseURL + version + "/version.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String input;
            StringBuilder version_json_builder = new StringBuilder();

            while ((input = reader.readLine()) != null){
                version_json_builder.append(input);
            }

            version_json = version_json_builder.toString();

            FileUtils.writeStringToFile(versionFilePath, version_json, Charset.defaultCharset());

        } else {
            version_json = FileUtils.readFileToString(versionFilePath, Charset.defaultCharset());
        }

        VersionData data = GSON.fromJson(version_json, VersionData.class);


        File jarfile = new File(GameInfo.getVersionLocation(version) + "/client.jar");
        if(!jarfile.exists()) {
            System.out.println("Downloading JAR for " + data.getVersion());
            URL downloadURL = new URL(baseURL + data.getJar());

            if (!jarfile.exists())
                FileUtils.copyURLToFile(downloadURL, jarfile);
        }
    }

    public static String getLatest() throws IOException {
        URL url = new URL(baseURL + "version/");

        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        String version = null;
        version = reader.readLine();
        return version;
    }
}
