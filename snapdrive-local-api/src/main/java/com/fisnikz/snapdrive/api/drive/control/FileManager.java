package com.fisnikz.snapdrive.api.drive.control;

import javax.enterprise.context.ApplicationScoped;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
public class FileManager {

    private final String DOWNLOADS_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snap-drive-fx-client\\snap-files\\";
    private final String TO_UPLOAD_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snap-drive-fx-client\\snap-files\\toUpload\\";
    private final String LOCAL_DECRYPTED_FILES_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snap-drive-fx-client\\snap-files\\local\\";

    public File toZipFile(File folder) throws IOException {
        File zipFile = new File(TO_UPLOAD_PATH + folder.getName() + ".zip");
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile));

        File[] files = folder.listFiles();
        for (File file : files) {
            out.putNextEntry(new ZipEntry(file.getName()));

            out.write(Files.readAllBytes(Path.of(file.getAbsolutePath())));
            out.closeEntry();
        }

        out.close();


        return zipFile;
    }

    public void extractZipFile(String fileZip, File destDir) throws IOException {
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = new File(destDir + "/" + zipEntry.getName());
            System.out.println(newFile.isDirectory());
            newFile.createNewFile();
            // fix for Windows-created archives

            // write file content
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public File downloadZipAndExtractToFolder(String link) {
        System.out.println("DOWNOADING: " + link);
        var fileName = link.substring(link.lastIndexOf("/") + 1, link.lastIndexOf(".zip"));
        System.out.println("fileName = " + fileName);
//        String downloadedZipFilePath = downloadFromInternet(fileName, link);
//        File downloadedZipFile = new File(downloadedZipFilePath);
        File downloadedZipFile = downloadFromInternet(fileName, link);
        System.out.println("downloadedZipFile = " + downloadedZipFile.getAbsolutePath());
//        String filePath = doDecrypt(downloadedZipFile, loggedInUserInfo.getUserPrivateKey(), false);
//        downloadedZipFile.delete();

        File extractedZip = new File(DOWNLOADS_PATH + downloadedZipFile.getName().substring(0, downloadedZipFile.getName().lastIndexOf(".zip")));
        extractedZip.mkdirs();

        try {
            extractZipFile(downloadedZipFile.getAbsolutePath(), extractedZip);
        } catch (IOException e) {
            e.printStackTrace();
        }
        downloadedZipFile.delete();

        return extractedZip;
    }

    // returns filePath
    public File downloadFromInternet(String fileName, String fileUrl) {
        try {
            URL website = new URL(fileUrl);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            File downloadedFile = new File(DOWNLOADS_PATH + fileName + ".zip");
            downloadedFile.createNewFile();
            System.out.println("downloadedFile11111111111111 = " + downloadedFile.getAbsolutePath());
//            final String FILE_PATH = DOWNLOADS_PATH + fileName + ".zip";
            FileOutputStream fos = new FileOutputStream(downloadedFile);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            System.out.println("Done: " + fileName + ", " + LocalDateTime.now() + ", from " + Thread.currentThread().getName());
            fos.close();
            return downloadedFile;
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new IllegalArgumentException("Something went wrong:");
        }
    }
}
