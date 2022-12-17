package net.minecraftnt;

import com.moandjiezana.toml.Toml;
import net.minecraftnt.api.ClientLoader;
import net.minecraftnt.api.ServerLoader;
import net.minecraftnt.api.SharedLoader;
import net.minecraftnt.server.data.GameData;
import net.minecraftnt.server.data.resources.Resources;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public class ModLoader {

    public static final Logger LOGGER = LogManager.getLogger("ModLoader");
    private final LinkedList<String> mods = new LinkedList<>();
    private final LinkedList<ClientLoader> resourceLoaders = new LinkedList<>();
    private static ModLoader latest;

    public static ModLoader getLatest() {
        return latest;
    }

    public ModLoader() {
        latest = this;
    }

    public void loadMods(boolean client) {
        LOGGER.info("Loading main game mod");
        ClassLoader localClassLoader = getClass().getClassLoader();

        loadMod(localClassLoader, Resources.readStream("META-INF/mod.toml"),"", client);

        LOGGER.info("Searching for mods");
        //Creating a File object for directory
        File modsDirectory = new File(GameData.getModsDirectory());
        LOGGER.info("Search path: " + modsDirectory.getAbsolutePath());
        //Creating filter for jpg files
        FilenameFilter jarFileFilter = (dir, name) -> {
            String lowercaseName = name.toLowerCase();
            return lowercaseName.endsWith(".jar");
        };

        File[] jarFiles = modsDirectory.listFiles(jarFileFilter);
        LOGGER.info("Iterating through mod jars");
        assert jarFiles != null;
        for(File mod : jarFiles) {

            try {
                URLClassLoader urlClassLoader = new URLClassLoader(
                  new URL[] { mod.toURI().toURL() }
                );

                JarFile jarZipFile = new JarFile(mod);

                loadMod(urlClassLoader, jarZipFile.getInputStream(jarZipFile.getEntry("META-INF/mod.toml")), mod.getName(), client);
                urlClassLoader.close();
            } catch (MalformedURLException e) {
                LOGGER.fatal("Invalid mod path: " + e.getMessage());
            } catch (IOException e) {
                LOGGER.fatal("URLClassLoader error: " + e.getMessage());
            }
        }

        reloadResources();


    }

    public void loadMod(ClassLoader classLoader, InputStream tomlResource, String file, boolean client) {
        LOGGER.info("Loading mod jar " + file);
        try {

            Toml toml = new Toml();
            toml.read(tomlResource);

            Toml informationTable = toml.getTable("Information");
            Toml loaderTable = toml.getTable("Loader");
            Toml compatabilityTable = toml.getTable("Compatability");

            assert informationTable != null;
            assert loaderTable != null;

            String name = informationTable.getString("Name");

            if(mods.contains(name)){
                LOGGER.fatal("Mod {} already defined", name);
                return;
            }

            assert name != null;

            if(compatabilityTable != null) {
                for(String mod : mods) {

                    if(Objects.equals(compatabilityTable.getString(mod), "Incompatible")){
                        LOGGER.fatal("{} is not compatible with {}", name, mod);
                        return;
                    }

                }
            }

            String sharedLoaderName = loaderTable.getString("SharedLoader");
            assert sharedLoaderName != null;

            LOGGER.info("Running shared loader: " + sharedLoaderName);

            Class<?> sharedLoader = classLoader.loadClass(sharedLoaderName);
            SharedLoader sharedLoaderInstance = (SharedLoader)sharedLoader.getDeclaredConstructor().newInstance();
            sharedLoaderInstance.loadShared();

            String clientLoaderName = loaderTable.getString("ClientLoader");

            if(clientLoaderName != null && client){
                LOGGER.info("Running client loader: " + clientLoaderName);
                Class<?> clientLoader = classLoader.loadClass(clientLoaderName);

                ClientLoader clientLoaderInstance = (ClientLoader)clientLoader.getDeclaredConstructor().newInstance();
                clientLoaderInstance.loadClient();

                resourceLoaders.add(clientLoaderInstance);
            }

            String serverLoaderName = loaderTable.getString("ServerLoader");

            if(serverLoaderName != null){
                LOGGER.info("Running server loader: " + serverLoaderName);
                Class<?> serverLoader = classLoader.loadClass(serverLoaderName);

                ServerLoader serverLoaderInstance = (ServerLoader)serverLoader.getDeclaredConstructor().newInstance();
                serverLoaderInstance.loadServer();
            }

            mods.add(name);


        } catch (ClassNotFoundException e) {
            LOGGER.fatal("Could not find mod class: {}", e.getMessage());
        } catch (NoSuchMethodException e) {
            LOGGER.fatal("Could not find mod method: {}", e.getMessage());
        } catch (InvocationTargetException | IllegalAccessException e) {
            LOGGER.fatal("Could not run mod method: {}", e.getMessage());
        } catch (InstantiationException e) {
            LOGGER.fatal("Could not create mod class: {}", e.getMessage());
        } catch (AssertionError e) {
            LOGGER.fatal("Mod loading assertion error: {}", e.getMessage());
        }
    }

    public void reloadResources() {
        for(ClientLoader loader : resourceLoaders)
            loader.loadResources();
    }
}
