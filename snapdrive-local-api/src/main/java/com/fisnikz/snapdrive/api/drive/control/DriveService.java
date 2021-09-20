package com.fisnikz.snapdrive.api.drive.control;

import com.fisnikz.snapdrive.api.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.api.drive.entity.*;
import com.fisnikz.snapdrive.api.users.control.LoggedInUserInfo;
import com.fisnikz.snapdrive.api.users.control.UsersResourceClient;
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
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.bind.Jsonb;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.System.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.List;
import java.util.Locale;

import static com.fisnikz.snapdrive.crypto.boundary.CryptoService.decodeFromBase64;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Logged
public class DriveService {

    private final String TO_UPLOAD_PATH = System.getProperty("java.io.tmpdir") + "/snap-files/toUpload/";

    @Inject
    CryptoService cryptoService;

    @Inject
    LoggedInUserInfo loggedInUserInfo;

    @Inject
    @RestClient
    UsersResourceClient usersResourceClient;

    @Inject
    FileManager fileManager;

    @Inject
    Jsonb jsonb;

    @Inject
    GoogleDriveService googleDriveService;

    @Inject
    Logger LOG;

    // @return all the files (with decrypted fileName) of the logged in user
    public JsonObject getAllFiles(boolean sharedWithMeOnly) {
        try {
            JsonArrayBuilder driveFiles = Json.createArrayBuilder();
            List<DriveFile> userFiles = googleDriveService.getFiles(sharedWithMeOnly);
            userFiles.stream()
                    .map((DriveFile driveFile) -> downloadAndDecryptFileName(driveFile, sharedWithMeOnly))
                    .forEach(driveFiles::add);

            Long totalSize = userFiles.stream().map(DriveFile::getSize).reduce(0L, Long::sum);

            JsonObjectBuilder responseData;
            if (!sharedWithMeOnly) {
                GoogleStorageQuota storageQuota = googleDriveService.getStorageQuota();
                responseData = Json.createObjectBuilder()
                        .add("storageLimit", humanReadableByteCountBin(storageQuota.getStorageLimit()))
                        .add("storageInDrive", humanReadableByteCountBin(storageQuota.getStorageInGoogleDrive()))
                        .add("storageInGCM", humanReadableByteCountBin(storageQuota.getStorageInGCM()))
                        .addAll(totalSnapDriveStorageSizeToJson(totalSize, storageQuota))
                        .add("files", driveFiles);

            } else {
                responseData = Json.createObjectBuilder()
                        .add("files", driveFiles);
            }

            return responseData.build();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JsonObject getFile(String fileId) {
        try {
            return this.downloadAndDecryptFileName(googleDriveService.getFile(fileId), false)
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            LOG.log(Logger.Level.ERROR, "IOException happened!");
            return null;
        }
    }

    public JsonObject unlockFiles(String masterPassword) {
        boolean gotPrivateKey = loggedInUserInfo.unlock(masterPassword);

        if (gotPrivateKey) {
            return getAllFiles(false);
        }

        throw new WebApplicationException(Response.status(403).build());

    }

    public DownloadedFileMetadata downloadDecryptedFile(String fileId, boolean sharedFile) {
        try {
            File extractedFolder = fileManager.downloadDriveFileAndExtractToFolder(fileId);
            FileEncryptionFinalResult result = fileManager.readConfigFileToEncryptionResult(extractedFolder);

            String decryptedPath;
            if (sharedFile) {
                String base64FileKey = result.getSharedInfoPerUser(loggedInUserInfo.getUser().getId()).getBase64FileKey();
                SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(base64FileKey), loggedInUserInfo.getUserPrivateKey());
                IvParameterSpec IV = new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector()));
                decryptedPath = fileManager.decrypt(extractedFolder, secretKey, IV, false);
            } else {
                SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(result.getBase64FileKey()), loggedInUserInfo.getUserPrivateKey());
                IvParameterSpec IV = new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector()));
                decryptedPath = fileManager.decrypt(extractedFolder, secretKey, IV, false);
            }

            var mimeType = Files.probeContentType(Paths.get(decryptedPath));
            byte[] bytes = Files.readAllBytes(Paths.get(decryptedPath));
            var fileName = new File(decryptedPath).getName();

            fileManager.deleteFolder(extractedFolder);
            Files.deleteIfExists(Path.of(decryptedPath));

            return new DownloadedFileMetadata(fileName, mimeType, bytes);

        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            LOG.log(Logger.Level.ERROR, "Decryption error, because there is given invalid key!");
            return null;
        }
    }

    public String uploadToGoogleDrive(FileUploadForm uploadForm) throws IOException {
        File toUploadFile = new File(TO_UPLOAD_PATH + uploadForm.fileName);

        /*
            The application is accessed from localhost (frontend vue app). When we upload file from frontend
            we need to copy those uploaded form bytes in a local (temporary file) so we can do the encryption to that file.
            In the end we will upload encrypted file to server and delete all those local files!
         */
        Files.copy(
                uploadForm.fileData,
                toUploadFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);


        File encryptedZipFile = fileManager.encryptFile(toUploadFile, loggedInUserInfo.getUser());

        String fileId = googleDriveService.uploadFile(encryptedZipFile);
        fileManager.resetToUploadFolder();

        return fileId;
    }

    public void deleteFile(String fileId) {
        try {
            googleDriveService.deleteFile(fileId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FilePermission shareFile(String fileId, String recipientEmail) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        JsonObject userWithGivenFields = usersResourceClient.getUserWithGivenFields(recipientEmail, "userId,public_key");
        System.out.println(userWithGivenFields);

        File extractedFolder = fileManager.downloadDriveFileAndExtractToFolder(fileId);
        fileManager.addUserToConfigFile(extractedFolder, userWithGivenFields.getString("userId"), loggedInUserInfo.getUserPrivateKey(), userWithGivenFields.getString("publicKey"));

        File toZipFile = fileManager.toZipFile(extractedFolder);
        googleDriveService.updateFile(fileId, toZipFile);

        FilePermission filePermission = googleDriveService.addReaderPermissionToFile(userWithGivenFields.getString("email"), fileId);

        Files.deleteIfExists(Path.of(toZipFile.getAbsolutePath()));
        fileManager.deleteFolder(extractedFolder);
        return filePermission;
    }

    public List<DriveFile> getUserSharedFiles() {
        try {
            return googleDriveService.getFiles(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public JsonObject calculateTotalStorageSize() {
        try {
            GoogleStorageQuota storageQuota = googleDriveService.getStorageQuota();
            Long totalSize = googleDriveService.getFiles(false).stream().map(DriveFile::getSize).reduce(0L, Long::sum);

            return Json.createObjectBuilder()
                    .add("storageLimit", humanReadableByteCountBin(storageQuota.getStorageLimit()))
                    .add("storageInDrive", humanReadableByteCountBin(storageQuota.getStorageInGoogleDrive()))
                    .add("storageInGCM", humanReadableByteCountBin(storageQuota.getStorageInGCM()))
                    .addAll(totalSnapDriveStorageSizeToJson(totalSize, storageQuota))
                    .build();

        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInternalError());
        }
    }

    public JsonObjectBuilder totalSnapDriveStorageSizeToJson(long totalBytes, GoogleStorageQuota googleStorageQuota) {
        return Json.createObjectBuilder()
                .add("storageSizeSnapDrive", humanReadableByteCountBin(totalBytes))
                .add("storageSizeSnapDrivePercentage", totalUsedPercentage(totalBytes, googleStorageQuota.getStorageLimit()));
    }

    private JsonObjectBuilder downloadAndDecryptFileName(DriveFile driveFile, boolean sharedFile) {
        File extractedFolder = null;
        try {
            extractedFolder = fileManager.downloadDriveFileAndExtractToFolder(driveFile.getGoogleDriveId());
            String decryptedFileName;
            FileEncryptionFinalResult result = fileManager.readConfigFileToEncryptionResult(extractedFolder);
            if (sharedFile) {
                String base64FileKey = result.getSharedInfoPerUser(loggedInUserInfo.getUser().getId()).getBase64FileKey();
                SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(base64FileKey), loggedInUserInfo.getUserPrivateKey());
                IvParameterSpec IV = new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector()));

                decryptedFileName = fileManager.decrypt(extractedFolder, secretKey, IV, true);
            } else {
                SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(result.getBase64FileKey()), loggedInUserInfo.getUserPrivateKey());
                IvParameterSpec IV = new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector()));
                decryptedFileName = fileManager.decrypt(extractedFolder, secretKey, IV, true);
            }


            return Json.createObjectBuilder(Json.createReader(new StringReader(jsonb.toJson(driveFile))).readObject())
                    .add("fileName", decryptedFileName)
                    .add("readableSize", humanReadableByteCountBin(driveFile.getSize()));
        } catch (IOException ex) {
            ex.printStackTrace();
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "Something went wrong!");
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            LOG.log(Logger.Level.ERROR, "Decryption error, because there is given invalid key!");

            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "Not decrypted because there is given invalid key!");
        } finally {
            if (extractedFolder != null) {
                fileManager.deleteFolder(extractedFolder);
            }
        }


    }

    private String totalUsedPercentage(long snapDriveSize, long storageLimit) {
        double percentage = ((double) snapDriveSize / storageLimit) * 100;
        System.out.println("percentage = " + percentage);
        return String.format(Locale.US, "%.2f", percentage);
//        return percentage;
    }

    public String humanReadableByteCountBin(long bytes) {
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
}
