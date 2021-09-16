package com.fisnikz.snapdrive.api.drive.control;

import com.fisnikz.snapdrive.api.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.api.drive.entity.DownloadedFileMetadata;
import com.fisnikz.snapdrive.api.drive.entity.DriveFile;
import com.fisnikz.snapdrive.api.drive.entity.FilePermission;
import com.fisnikz.snapdrive.api.drive.entity.FileUploadForm;
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
    DriveSharesResourceClient driveSharesResourceClient;

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

    public static String humanReadableByteCountBin(long bytes) {
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
                responseData = Json.createObjectBuilder(googleDriveService.getStorageQuota())
                        .addAll(totalSnapDriveStorageSizeToJson(totalSize))
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
        var start = System.currentTimeMillis();
        File toUploadFile = new File(TO_UPLOAD_PATH + uploadForm.fileName);

        /*
            Because the application is accessed from localhost (frontend vue app), when we upload file from frontend
            we need to copy those uploaded form bytes in a local (temporary file) so we can do the encryption to that file.
            And in the end we will delete these local files and only upload encrypted bytes to server!
         */
        Files.copy(
                uploadForm.fileData,
                toUploadFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        LOG.log(Logger.Level.INFO, "Copying Process Took: " + (System.currentTimeMillis() - start));


//        File encryptedZipFile = fileManager.encryptFile(new File("C:\\Users\\Fisnik\\Downloads\\Boxcryptor_v2.47.1752_Setup.msi"), loggedInUserInfo.getUser());
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
        driveSharesResourceClient.deleteFileShares(fileId);
    }

    public MasterPasswordCryptoResults updateMasterPassword(String newMasterPassword, String oldMasterPassword) {
//        try {
//            MasterPasswordCryptoResults masterPasswordCryptoResults = cryptoService.doCryptoToMasterPassword(newMasterPassword);
//
//            for (DriveFile originalDriveFile : driveSharesResourceClient.getFiles(loggedInUserInfo.getUser().getId())) {
//                File folder = fileManager.downloadZipAndExtractToFolder(originalDriveFile.getGoogleDriveId());
//                fileManager.updateFileKey(folder, loggedInUserInfo.getUser(), masterPasswordCryptoResults.getPublicKeyBase64(), oldMasterPassword);
//                File zipFile = fileManager.toZipFile(folder);
//
//                googleDriveService.updateFile(originalDriveFile.getGoogleDriveId(), zipFile);
//            }
//
//            fileManager.resetToUploadFolder();
//            return masterPasswordCryptoResults;
//        } catch (Exception e) {
//            e.printStackTrace();
        return null;
//        }
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
//        return driveSharesResourceClient.shareFile(new FilePermission(permissionId, fileId, loggedInUserInfo.getUser().getId(), recipientEmail));
    }

    public List<DriveFile> getUserSharedFiles() {
//        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
//
//        driveSharesResourceClient.getSharedFilesToUser(loggedInUserInfo.getUser().getId())
//                .stream()
//                .map(JsonValue::asJsonObject)
//                .map(this::downloadAndDecryptSharedFile)
//                .forEach(arrayBuilder::add);
//
//        return arrayBuilder.build();
        try {
            return googleDriveService.getFiles(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public JsonObject calculateTotalStorageSize() {
        try {
            JsonObject driveStorageQuota = googleDriveService.getStorageQuota();
            Long totalSize = googleDriveService.getFiles(false).stream().map(DriveFile::getSize).reduce(0L, Long::sum);
            return Json
                    .createObjectBuilder(driveStorageQuota)
                    .addAll(totalSnapDriveStorageSizeToJson(totalSize))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInternalError());
        }
    }

    public JsonObjectBuilder totalSnapDriveStorageSizeToJson(long totalBytes) {
        return Json.createObjectBuilder()
                .add("storageSizeSnapDrive", humanReadableByteCountBin(totalBytes))
                .add("storageSizeSnapDrivePercentage", totalUsedPercentage(totalBytes));
    }

    private JsonObjectBuilder downloadAndDecryptFileName(DriveFile driveFile, boolean sharedFile) {
        try {
            File extractedFolder = fileManager.downloadDriveFileAndExtractToFolder(driveFile.getGoogleDriveId());

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

            fileManager.deleteFolder(extractedFolder);
            return Json.createObjectBuilder(Json.createReader(new StringReader(jsonb.toJson(driveFile))).readObject())
                    .add("fileName", decryptedFileName)
                    .add("readableSize", humanReadableByteCountBin(driveFile.getSize()));
        } catch (IOException ex) {
            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "Something went wrong!");
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            LOG.log(Logger.Level.ERROR, "Decryption error, because there is given invalid key!");

            return Json.createObjectBuilder()
                    .add("success", false)
                    .add("message", "Not decrypted because there is given invalid key!");
        }


    }

    private double totalUsedPercentage(long bytes) {
        //in GB
        int totalFreeSpace = 15;
        double usedSpace = (double) bytes / 1024 / 1024 / 1024;

        double percentage = (usedSpace / totalFreeSpace) * 100;
        System.out.println("percentage = " + percentage);
        return percentage;
//        return String.format(Locale.US, "%.2f", percentage);
    }
}
