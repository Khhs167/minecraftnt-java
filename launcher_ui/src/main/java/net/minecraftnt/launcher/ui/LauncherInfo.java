package net.minecraftnt.launcher.ui;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static net.minecraftnt.launcher.ui.LauncherApplication.GSON;

public class LauncherInfo {

    private static LinkedList<Profile> profiles = new LinkedList<>();
    private static String[] versions;
    private static String changelog = null;

    public static final Profile PROFILE_LATEST = new Profile().setName("Latest").setVersion("latest");

    public static String getChangelog() {
        if(changelog == null)
            getVersions();
        return changelog;
    }

    public static String[] getVersions() {

        if(versions != null)
            return versions;

        changelog = "";
        LinkedList<String> versionList = new LinkedList<>();

        String versionsURL = "https://openability.tech/jimmster/minecraftnt/download/versions/";

        try {
            URL url = new URL(versionsURL);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

            String s = null;
            while ((s = reader.readLine()) != null) {
                versionList.add(s);
                changelog += s + "\n-------\n";

                String baseURL = "https://openability.tech/jimmster/minecraftnt/download/notes/?version=";

                URL logURL = new URL(baseURL + s);
                BufferedReader changelogReader = new BufferedReader(new InputStreamReader(logURL.openStream()));

                String logS = null;

                while ((logS = changelogReader.readLine()) != null)
                    changelog += logS + "\n";

                changelog += "\n\n";
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        versionList.add("latest");

        versions = versionList.toArray(new String[versionList.size()]);

        return versions;
    }

    public static Profile[] getProfiles() {
        return profiles.toArray(new Profile[profiles.size()]);
    }

    public static void addProfile(Profile profile){
        profiles.add(profile);
    }

    public static void removeProfile(Profile profile){
        profiles.remove(profile);
    }

    public static void loadProfiles() {
        profiles.clear();
        try {
            File profileJSONFile = new File(getLauncherFileLocation("profiles.json"));

            if (profileJSONFile.exists()) {


                String profilesJSON = FileUtils.readFileToString(profileJSONFile, Charset.defaultCharset());


                Profile[] loadedProfiles = GSON.fromJson(profilesJSON, Profile[].class);

                profiles.clear();
                profiles.addAll(List.of(loadedProfiles));

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        addLatest();

    }

    private static void addLatest() {
        for(Profile p : profiles){
            if(Objects.equals(p.getName(), "Latest"))
                return;
        }
        profiles.add(PROFILE_LATEST);
    }

    public static void saveProfiles() {
        try {

            File profileJSONFile = new File(getLauncherFileLocation("profiles.json"));
            String profilesJSON = GSON.toJson(profiles);
            FileUtils.writeStringToFile(profileJSONFile, profilesJSON, Charset.defaultCharset());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String getGameDirectory() {
        String home_dir = System.getProperty("user.home");

        Path path = Path.of(home_dir, ".khhs", "minecraft");

        File gameDir = path.toFile();

        if(!gameDir.exists())
            if(!gameDir.mkdirs())
                throw new RuntimeException("Could not create game directory!");

        return path.toAbsolutePath().toString();
    }

    public static String getLauncherDirectory() {
        Path path = Path.of(getGameDirectory(), "launcher");

        File resourceDir = path.toFile();

        if(!resourceDir.exists())
            if(!resourceDir.mkdirs())
                throw new RuntimeException("Could not create version directory!");

        return path.toAbsolutePath().toString();
    }

    public static String getLauncherFileLocation(String file){
        return Path.of(getLauncherDirectory(), file).toAbsolutePath().toString();
    }
}
