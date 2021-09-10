package com.fisnikz.snapdrive.api.drive.control;

import com.fisnikz.snapdrive.api.drive.entity.DriveFile;
import com.fisnikz.snapdrive.api.drive.entity.FileUploadForm;
import com.fisnikz.snapdrive.api.drive.entity.ShareFileRequest;
import com.fisnikz.snapdrive.api.users.control.UsersResourceClient;
import com.fisnikz.snapdrive.api.users.entity.LoggedInUserInfo;
import com.fisnikz.snapdrive.crypto.boundary.CryptoService;
import com.fisnikz.snapdrive.crypto.entity.FileEncryptionFinalResult;
import com.fisnikz.snapdrive.crypto.entity.MasterPasswordCryptoResults;
import com.fisnikz.snapdrive.logging.Logged;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.Instant;
import java.util.UUID;

import static com.fisnikz.snapdrive.crypto.boundary.CryptoService.decodeFromBase64;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Logged
public class DriveService {

    private final String DOWNLOADS_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snapdrive-local-api\\snap-files\\";
    private final String TO_UPLOAD_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snapdrive-local-api\\snap-files\\toUpload\\";
    private final String LOCAL_DECRYPTED_FILES_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snapdrive-local-api\\snap-files\\local\\";

    @Inject
    CryptoService cryptoService;

    @Inject
    LoggedInUserInfo loggedInUserInfo;

    @Inject
    @RestClient
    DriveResourceClient driveResourceClient;

    @Inject
    @RestClient
    UsersResourceClient usersResourceClient;

    @Inject
    FileManager fileManager;

    /*
        From a frontend app we send the request to download the file from this given @fileLink param
        The file is stored in server as encrypted so after we download the files from the @param fileLink
        we need to decrypt it's bytes and then return the decrypted file to (frontend app)
     */
    public String downloadDecryptedFile(String fileLink, boolean shared) {
        File folder = fileManager.downloadZipAndExtractToFolder(fileLink);
        try {
            if (!shared) {
                return fileManager.decrypt(folder, loggedInUserInfo.getUserPrivateKey(), false);
            }
            FileEncryptionFinalResult result = fileManager.readConfigFileToEncryptionResult(folder);

            String base64FileKey = result.getSharedInfoPerUser(loggedInUserInfo.getUser().getId()).getBase64FileKey();
            SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(base64FileKey), loggedInUserInfo.getUserPrivateKey());
            IvParameterSpec IV = new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector()));

            return fileManager.doDecrypt(folder, secretKey, IV, false);
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    // @return all the files (with decrypted fileName) of the logged in user
    public JsonArray getAllFiles() {
        JsonArrayBuilder driveFiles = Json.createArrayBuilder();
        this.driveResourceClient.getFiles(this.loggedInUserInfo.getUser().getId())
                .stream()
                .map(this::downloadAndDecryptFileName)
                .forEach(driveFiles::add);

        return driveFiles.build();
    }

    public JsonObject upload(FileUploadForm uploadForm) throws IOException {
        File targetFile = new File(TO_UPLOAD_PATH + uploadForm.fileName);

        /*
            Because the application is accessed from localhost (frontend vue app), when we upload file from frontend
            we need to copy those uploaded form bytes in a local (temporary file) so we can do the encryption to that file.
            And in the end we will delete these local files and only upload encrypted bytes to server!
         */
        Files.copy(
                uploadForm.fileData,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        File encryptedZipFile = encryptFile(targetFile);

        JsonObject uploadedFilePath = doUpload(encryptedZipFile);
        fileManager.resetToUploadFolder();

        return uploadedFilePath;
    }

    private File encryptFile(File fileToEncrypt) throws IOException {
        //master key save it in memory, to save time
        File outputFolder = new File("sdrive-" + UUID.randomUUID().toString());
        outputFolder.mkdir();

        System.out.println(outputFolder.getAbsolutePath());

        try {
            cryptoService.encrypt(fileToEncrypt, outputFolder, loggedInUserInfo.getUser());
            File encryptedZipFile = fileManager.toZipFile(outputFolder);
            System.out.println("Local before: " + encryptedZipFile.getName());

            fileManager.deleteFolder(outputFolder);

            return encryptedZipFile;

        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException
                | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException
                | InvalidKeyException | InvalidKeySpecException e) {
            e.printStackTrace();
            throw new RuntimeException("something happened");
        }
    }

    private JsonObject doUpload(File file) {
        FileUploadForm driveFileUploadForm = new FileUploadForm();
        try {
            driveFileUploadForm.fileData = new ByteArrayInputStream(Files.readAllBytes(Path.of(file.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        DriveFile driveFile = this.driveResourceClient.upload(loggedInUserInfo.getUser().getId(), driveFileUploadForm);
        loggedInUserInfo.getUser().getFiles().add(driveFile);
        return downloadAndDecryptFileName(driveFile).build();
    }

    private DriveFile updateFile(String fileId, File file) {
        FileUploadForm driveFile = new FileUploadForm();
        try {
            driveFile.fileData = new ByteArrayInputStream(Files.readAllBytes(Path.of(file.getAbsolutePath())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return this.driveResourceClient.update(fileId, loggedInUserInfo.getUser().getId(), driveFile);
    }

    private JsonObjectBuilder downloadAndDecryptFileName(DriveFile driveFile) {
        File downloadedFolder = fileManager.downloadZipAndExtractToFolder(driveFile.getLink());

        String decryptedFileName;
        try {
            decryptedFileName = fileManager.decrypt(downloadedFolder, loggedInUserInfo.getUserPrivateKey(), true);
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "Not decrypted because there is given invalid key!");
        } finally {
            downloadedFolder.delete();
        }

        return Json.createObjectBuilder(driveFile.toJsonObject())
                .add("fileName", decryptedFileName)
                .add("readableSize", humanReadableByteCountBin(driveFile.getSize()))
                .add("readableCreatedAt", Instant.ofEpochMilli(driveFile.getCreatedAt()).toString());
    }


    public JsonObject deleteFile(String fileId) {
        JsonObject body = driveResourceClient.delete(fileId);
        if (body.getBoolean("deleted")) {
            loggedInUserInfo.getUser().getFiles().removeIf(file -> file.getId().equals(fileId));
        }
        return body;
    }

    public MasterPasswordCryptoResults updateMasterPassword(String newMasterPassword, String oldMasterPassword) {
        try {
            MasterPasswordCryptoResults masterPasswordCryptoResults = cryptoService.doCryptoToMasterPassword(newMasterPassword);

            for (DriveFile originalDriveFile : driveResourceClient.getFiles(loggedInUserInfo.getUser().getId())) {
                File folder = fileManager.downloadZipAndExtractToFolder(originalDriveFile.getLink());
                fileManager.updateFileKey(folder, loggedInUserInfo.getUser(), masterPasswordCryptoResults.getPublicKeyBase64(), oldMasterPassword);
                File zipFile = fileManager.toZipFile(folder);

                DriveFile updatedFile = updateFile(originalDriveFile.getId(), zipFile);
                originalDriveFile.setLink(updatedFile.getLink());
                originalDriveFile.setSize(updatedFile.getSize());
            }

            fileManager.resetToUploadFolder();
            return masterPasswordCryptoResults;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonObject shareFile(String fileId, String recipientUsername) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        DriveFile originalDriveFile = driveResourceClient.getFile(fileId);
        JsonObject userWithGivenFields = usersResourceClient.getUserWithGivenFields(null, recipientUsername, "userId,username,public_key");

        File downloadedFolder = fileManager.downloadZipAndExtractToFolder(originalDriveFile.getLink());
        fileManager.addUserToConfigFile(downloadedFolder, userWithGivenFields.getString("userId"), loggedInUserInfo.getUserPrivateKey(), userWithGivenFields.getString("publicKey"));

        File toZipFile = fileManager.toZipFile(downloadedFolder);
        DriveFile updatedFile = updateFile(originalDriveFile.getId(), toZipFile);

        originalDriveFile.setLink(updatedFile.getLink());
        originalDriveFile.setSize(updatedFile.getSize());

        ShareFileRequest metadata = new ShareFileRequest(originalDriveFile.getId(), userWithGivenFields.getString("username"));

        return driveResourceClient.shareFile(metadata);
    }

    public JsonArray getUserSharedFiles() {
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

        driveResourceClient.getSharedFilesToUser(loggedInUserInfo.getUser().getId())
                .stream()
                .map(JsonValue::asJsonObject)
                .map(this::asResponseJsonFile)
                .forEach(arrayBuilder::add);

        return arrayBuilder.build();
    }

    // modifies original jsonObject that does not have included (decrypted fileName), and also size and createdAt in beautified forms
    private JsonObject asResponseJsonFile(JsonObject jsonObject) {
        System.out.println(jsonObject);
        try {
            File downloadedFolder = fileManager.downloadZipAndExtractToFolder(jsonObject.getJsonObject("file").getString("link"));
            FileEncryptionFinalResult result = fileManager.readConfigFileToEncryptionResult(downloadedFolder);

            String base64FileKey = result.getSharedInfoPerUser(loggedInUserInfo.getUser().getId()).getBase64FileKey();
            SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(base64FileKey), loggedInUserInfo.getUserPrivateKey());
            IvParameterSpec IV = new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector()));

            String decryptedFileName = fileManager.doDecrypt(downloadedFolder, secretKey, IV, true);

            JsonObject fileJson = Json.createObjectBuilder(jsonObject.getJsonObject("file"))
                    .add("fileName", decryptedFileName)
                    .add("readableSize", humanReadableByteCountBin(jsonObject.getJsonObject("file").getInt("size")))
                    .add("readableCreatedAt", Instant.ofEpochMilli(jsonObject.getJsonObject("file").getInt("createdAt")).toString())
                    .build();

            return Json.createObjectBuilder(jsonObject)
                    .add("file", fileJson)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JsonObject calculateTotalStorageSize() {
        Long totalBytes = loggedInUserInfo.getUser().getFiles().stream().map(DriveFile::getSize).reduce(0L, Long::sum);
        return Json.createObjectBuilder()
                .add("totalUsed", humanReadableByteCountBin(totalBytes))
                .add("percentage", totalUsedPercentage(totalBytes))
                .build();
    }

    private String humanReadableByteCountBin(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

    private double totalUsedPercentage(long bytes) {
        //in GB
        int totalFreeSpace = 15;
        double usedSpace = (double) bytes / 1024 / 1024 / 1024;

        return (usedSpace / totalFreeSpace) * 100;
    }


}
