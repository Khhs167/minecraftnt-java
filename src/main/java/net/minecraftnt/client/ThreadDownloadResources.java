package net.minecraftnt.client;

import net.minecraftnt.util.GameInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class ThreadDownloadResources extends Thread {

    private static final Logger LOGGER = LogManager.getLogger(ThreadDownloadResources.class);

    private static final String PING_URL = "https://www.google.com";

    private static final String ASSET_URL = "https://openability.tech/jimmster/minecraftnt/assets/";

    private boolean needsReload;

    public ThreadDownloadResources() {
        setName("ResourceDownloadThread");
    }

    public void tryReload() {
        if(needsReload)
            ClientMainHandler.getInstance().loadResources();
        needsReload = false;
    }

    @Override
    public void run() {
        needsReload = false;

        LOGGER.info("Pinging {} to check for internet connection", PING_URL);
        if(!netIsAvailable()){
            LOGGER.warn("No internet connection is available! No resources will be downloaded!\n");
        }

        try {
            Thread.sleep(5000, 0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        LOGGER.info("Starting download...");

        readAndDownloadListing(ASSET_URL);

        needsReload = true;

    }

    public void readAndDownloadListing(String indexURL){
        LOGGER.info("Reading listing at {}", indexURL);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {

            documentBuilderFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            LOGGER.info("Opening download stream to listing...");
            BufferedInputStream in = new BufferedInputStream(new URL(indexURL).openStream());


            LOGGER.info("Reading listing...");
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document fileListing = documentBuilder.parse(in);

            Element listingElement = fileListing.getDocumentElement();
            listingElement.normalize();

            if(!listingElement.getTagName().equals("DirectoryListing")){
                LOGGER.fatal("Invalid directory listing!");
                throw new RuntimeException("Invalid Directory Listing: expected 'DirectoryListing', got '" + listingElement.getTagName() + "'");
            }

            if(listingElement.getElementsByTagName("DirectoryName").getLength() <= 0){
                LOGGER.fatal("Invalid directory listing!");
                throw new RuntimeException("Invalid Directory Listing: expected 'DirectoryName' attribute! Got none!");
            }

            if(listingElement.getElementsByTagName("Files").getLength() <= 0){
                LOGGER.fatal("Invalid directory listing!");
                throw new RuntimeException("Invalid Directory Listing: expected 'Files' attribute! Got none!");
            }

            Node fileElement = listingElement.getElementsByTagName("Files").item(0);

            NodeList subNodes = fileElement.getChildNodes();

            for(int i = 0; i < subNodes.getLength(); i++){
                Node node = subNodes.item(i);

                if(!node.getNodeName().equals("FileListing")){
                    LOGGER.fatal("Invalid file listing!");
                    throw new RuntimeException("Invalid File Listing: expected 'FileListing' node, got '" + node.getNodeName() + "'!");
                }

                String type = null;
                long size = -1;
                String name = null;

                NodeList fileInfoNodes = node.getChildNodes();

                for(int j = 0; j < fileInfoNodes.getLength(); j++){

                    switch (fileInfoNodes.item(j).getNodeName()) {
                        case "Type" -> type = fileInfoNodes.item(j).getTextContent();
                        case "Size" -> size = Long.parseLong(fileInfoNodes.item(j).getTextContent());
                        case "Name" -> name = fileInfoNodes.item(j).getTextContent();
                        default -> {
                            LOGGER.fatal("Invalid file listing");
                            throw new RuntimeException("Invalid File Listing: expected 'Type', 'Size' or 'Name' child node, got '" + node.getNodeName() + "'!");
                        }
                    }

                }

                if(type == null){
                    LOGGER.fatal("Invalid file listing");
                    throw new RuntimeException("Invalid File Listing: expected 'Type' child node, got none!");
                }

                if(name == null){
                    LOGGER.fatal("Invalid file listing");
                    throw new RuntimeException("Invalid File Listing: expected 'Name' child node, got none!");
                }

                if(!type.equals("file") && !type.equals("directory")){
                    LOGGER.fatal("Invalid file listing");
                    throw new RuntimeException("Invalid File Listing: expected 'file' or 'directory' type, got '" + type + "'!");
                }

                if(type.equals("file") && size < 0){
                    LOGGER.fatal("Invalid file listing");
                    throw new RuntimeException("Invalid File Listing: expected 0> size, got " + size + "!(-1 is default)");
                }

                File resourceFile = new File(GameInfo.getResourceLocation(name));

                if(resourceFile.isDirectory() && type.equals("directory") && resourceFile.exists())
                    continue;

                if(resourceFile.isFile() && type.equals("file") && resourceFile.exists())
                    continue;

                if(type.equals("directory")){
                    if(!resourceFile.mkdirs()){
                        LOGGER.fatal("Could not create directory " + name);
                        throw new RuntimeException("Directory creation failed!");
                    }
                } else {

                    if(!resourceFile.exists() || Files.size(resourceFile.toPath()) != size) {
                        URL downloadURL = new URL(new URL(indexURL), name);
                        downloadAndSaveResource(downloadURL, size, resourceFile);
                    }
                }

            }


        } catch (Exception e){

            StringWriter stringWriter = new StringWriter();
            PrintWriter stackTraceStream = new PrintWriter(stringWriter);

            e.printStackTrace(stackTraceStream);

            LOGGER.fatal("An exception occurred when downloading resources: {}", stringWriter.toString());
        }
    }

    private static void downloadAndSaveResource(URL url, long size, File output){

        try {

            Path outPath = Paths.get(output.toURI());

            LOGGER.info("Downloading {} ({}) [{} byte]", output, url, size);

            InputStream in = url.openStream();
            Files.copy(in, outPath, StandardCopyOption.REPLACE_EXISTING);

            if(Files.size(outPath) != size){
                Files.delete(outPath);
                LOGGER.fatal("File size " + size + " != " + Files.size(outPath));
                throw new RuntimeException("Invalid file size!");
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private static boolean netIsAvailable() {
        try {
            final URL url = new URL(PING_URL);
            final URLConnection conn = url.openConnection();
            conn.connect();
            conn.getInputStream().close();
            return true;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            return false;
        }
    }
}
