package com.fisnikz.snapdrive.api.drive.control;

import com.fisnikz.snapdrive.api.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.api.users.control.UsersResourceClient;
import com.fisnikz.snapdrive.api.users.entity.User;
import com.fisnikz.snapdrive.crypto.boundary.CryptoService;
import com.fisnikz.snapdrive.crypto.entity.FileEncryptionFinalResult;
import com.fisnikz.snapdrive.logging.Logged;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static com.fisnikz.snapdrive.crypto.boundary.CryptoService.decodeFromBase64;
import static com.fisnikz.snapdrive.crypto.boundary.CryptoService.encodeToBase64;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Logged
public class FileManager {

    private final String DOWNLOADS_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snapdrive-local-api\\snap-files\\";
    private final String TO_UPLOAD_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snapdrive-local-api\\snap-files\\toUpload\\";
    private final String LOCAL_DECRYPTED_FILES_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snapdrive-local-api\\snap-files\\local\\";

    @Inject
    CryptoService cryptoService;
    @Inject
    @RestClient
    UsersResourceClient usersResourceClient;

    File toZipFile(File folder) throws IOException {
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

    void extractZipFile(String fileZip, File destDir) throws IOException {
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

    File downloadZipAndExtractToFolder(String link) {
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
    File downloadFromInternet(String fileName, String fileUrl) {
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

    String decrypt(File folder, PrivateKey privateKey, boolean onlyFileName) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        FileEncryptionFinalResult result = this.readConfigFileToEncryptionResult(folder);
        SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(result.getBase64FileKey()), privateKey);
        return doDecrypt(folder, secretKey, new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector())), onlyFileName);
    }

    String doDecrypt(File folder, SecretKey secretKey, IvParameterSpec IV, boolean onlyFileName) {
        try {
            File encryptedFile = folder.listFiles(pathname -> !pathname.getName().contains(".config"))[0];

            byte[] decryptedFileContent = cryptoService.decryptFile(encryptedFile, secretKey, IV);
            byte[] decryptedFileName = cryptoService.decryptBytes(decodeFromBase64(encryptedFile.getName()), secretKey, IV);

            if (onlyFileName) {
                return new String(decryptedFileName);
            }

            File decryptedFile = new File(LOCAL_DECRYPTED_FILES_PATH + new String(decryptedFileName));
            decryptedFile.createNewFile();
            Files.write(Path.of(decryptedFile.getAbsolutePath()), decryptedFileContent, StandardOpenOption.WRITE);

            return decryptedFile.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("something happened");
        }
    }

    void addUserToConfigFile(File folder, String userId, PrivateKey privateKey, String userPublicKeyBase64) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        FileEncryptionFinalResult result = readConfigFileToEncryptionResult(folder);

        if (result.getSharedUsers().stream().anyMatch(sharedUsers -> sharedUsers.getUserId().equals(userId))) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(409, "File is already shared to given user!"));
        }

        String base64EncryptedFileKey = result.getBase64FileKey();
        SecretKey fileSecretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(base64EncryptedFileKey), privateKey);

        PublicKey userPublicKey = (PublicKey) cryptoService.generateRSAKeyFromBase64(userPublicKeyBase64, false);

        byte[] encryptedFileKeyWithRecipientPublicKey = cryptoService.getRsaService().encrypt(fileSecretKey.getEncoded(), userPublicKey);

        String recipientFileKeyBase64 = encodeToBase64(encryptedFileKeyWithRecipientPublicKey);
        result.shareFile(userId, recipientFileKeyBase64);

        Files.writeString(Path.of(folder.getAbsolutePath() + "/.config"), JsonbBuilder.create().toJson(result), StandardOpenOption.WRITE);
    }

    void updateFileKey(File file, User user, String newPublicKeyBase64, String oldMasterPassword) {
        try {
            File configFile = new File(file.getAbsolutePath() + "/.config");

            FileEncryptionFinalResult result = JsonbBuilder.create()
                    .fromJson(new ByteArrayInputStream(Files.readAllBytes(Path.of(configFile.getAbsolutePath()))), FileEncryptionFinalResult.class);

            System.out.println("########################");
            System.out.println(result.getSharedUsers().size());
            System.out.println("########################");
            System.out.println(JsonbBuilder.create().toJson(result));

            String newKey = this.cryptoService.updateFileEncryptionKey(oldMasterPassword, user.getDerivativeSalt(),
                    user.getPrivateKey(), user.getDerivativeIterations(), user.getNonce(),
                    result.getBase64FileKey(), newPublicKeyBase64);

            result.setBase64FileKey(newKey);


            String newJsonConfig = JsonbBuilder.create().toJson(result);

            Files.writeString(Path.of(configFile.getAbsolutePath()), newJsonConfig, StandardOpenOption.WRITE);
        } catch (IOException | InvalidKeySpecException | NoSuchAlgorithmException | IllegalBlockSizeException | InvalidKeyException | BadPaddingException | NoSuchPaddingException | InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    FileEncryptionFinalResult readConfigFileToEncryptionResult(File folder) throws IOException {
        File configFile = new File(folder.getAbsolutePath() + "/.config");

        return JsonbBuilder.create()
                .fromJson(new ByteArrayInputStream(Files.readAllBytes(Path.of(configFile.getAbsolutePath()))), FileEncryptionFinalResult.class);
    }

    void deleteFolder(File outputFile) {
        for (File file : outputFile.listFiles()) {
            file.delete();
        }
        outputFile.delete();
    }

    void resetToUploadFolder() {
        File file = new File(TO_UPLOAD_PATH);
        deleteFolder(file);
        file.mkdirs();
    }
}
