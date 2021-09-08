package com.fisnikz.snapdrive.api.drive.control;

import com.fisnikz.snapdrive.api.ResponseWithJsonBodyBuilder;
import com.fisnikz.snapdrive.api.drive.entity.FileUploadForm;
import com.fisnikz.snapdrive.api.drive.entity.SharedFileMetadata;
import com.fisnikz.snapdrive.api.users.control.UsersResourceClient;
import com.fisnikz.snapdrive.api.users.entity.DriveFile;
import com.fisnikz.snapdrive.api.users.entity.LoggedInUserInfo;
import com.fisnikz.snapdrive.api.users.entity.MasterPasswordCryptoResults;
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
import javax.json.*;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.fisnikz.snapdrive.crypto.boundary.CryptoService.decodeFromBase64;
import static com.fisnikz.snapdrive.crypto.boundary.CryptoService.encodeToBase64;

/**
 * @author Fisnik Zejnullahu
 */
@ApplicationScoped
@Logged
public class DriveService {

    private final String DOWNLOADS_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snap-drive-fx-client\\snap-files\\";
    private final String TO_UPLOAD_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snap-drive-fx-client\\snap-files\\toUpload\\";
    private final String LOCAL_DECRYPTED_FILES_PATH = "C:\\Users\\Fisnik\\Desktop\\My\\java\\projects\\snap-drive\\snap-drive-fx-client\\snap-files\\local\\";

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

    public String downloadDecryptedFile(String fileLink, boolean shared) {
        File folder = fileManager.downloadZipAndExtractToFolder(fileLink);
        try {
            if (!shared) {
                return decrypt(folder, false);
            }
            FileEncryptionFinalResult result = this.readConfigFileToEncryptionResult(folder);

            String base64FileKey = result.getSharedInfoPerUser(loggedInUserInfo.getUser().getId()).getBase64FileKey();
            SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(base64FileKey), loggedInUserInfo.getUserPrivateKey());
            IvParameterSpec IV = new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector()));

            return doDecrypt(folder, secretKey, IV, false);
        } catch (NoSuchPaddingException | InvalidAlgorithmParameterException | NoSuchAlgorithmException | IOException | BadPaddingException | IllegalBlockSizeException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }
    }

    public JsonArray getAllFiles() {
        JsonArrayBuilder driveFiles = Json.createArrayBuilder();
        this.driveResourceClient.allFiles(this.loggedInUserInfo.getUser().getId())
                .stream()
                .map(this::downloadAndDecryptFileName)
                .forEach(driveFiles::add);

        return driveFiles.build();
    }

    public JsonObject upload(FileUploadForm driveFile) throws IOException {
        File targetFile = new File(TO_UPLOAD_PATH + driveFile.fileName);

        Files.copy(
                driveFile.fileData,
                targetFile.toPath(),
                StandardCopyOption.REPLACE_EXISTING);

        File encryptedZipFile = encryptFile(targetFile);

        JsonObject uploadedFilePath = doUpload(encryptedZipFile);
        resetToUploadFolder();

        return uploadedFilePath;
        //return more info, where is url for uploaded
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

            deleteFolder(outputFolder);

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
        //return more info, where is url for uploaded
    }

    private JsonObjectBuilder downloadAndDecryptFileName(DriveFile driveFile) {
        File downloadedFolder = fileManager.downloadZipAndExtractToFolder(driveFile.getLink());

        String decryptedFileName;
        try {
            decryptedFileName = decrypt(downloadedFolder, true);
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

    private String decrypt(File folder, boolean onlyFileName) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
        FileEncryptionFinalResult result = readConfigFileToEncryptionResult(folder);
        SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(result.getBase64FileKey()), loggedInUserInfo.getUserPrivateKey());
        return doDecrypt(folder, secretKey, new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector())), onlyFileName);
    }

    private String doDecrypt(File folder, SecretKey secretKey, IvParameterSpec IV, boolean onlyFileName) {
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


    private void deleteFolder(File outputFile) {
        for (File file : outputFile.listFiles()) {
            file.delete();
        }
        outputFile.delete();
    }

    private void resetToUploadFolder() {
        File file = new File(TO_UPLOAD_PATH);
        deleteFolder(file);
        file.mkdirs();
    }


    public JsonObject calculateTotalStorageSize() {
        Long totalBytes = loggedInUserInfo.getUser().getFiles().stream().map(DriveFile::getSize).reduce(0L, Long::sum);
        return Json.createObjectBuilder()
                .add("totalUsed", humanReadableByteCountBin(totalBytes))
                .add("percentage", totalUsedPercentage(totalBytes))
                .build();
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

    private double totalUsedPercentage(long bytes) {
        //in GB
        int totalFreeSpace = 15;
        double usedSpace = (double) bytes / 1024 / 1024 / 1024;

        return (usedSpace / totalFreeSpace) * 100;
    }

    public JsonObject deleteFile(String fileId) {
        JsonObject body = driveResourceClient.delete(fileId, loggedInUserInfo.getUser().getId());
        if (body.getBoolean("deleted")) {
            loggedInUserInfo.getUser().getFiles().removeIf(fileMetadata -> fileMetadata.getId().equals(fileId));
        }
        return body;
    }

    public MasterPasswordCryptoResults updateMasterPassword(String newMasterPassword, String oldMasterPassword) {
        try {
            MasterPasswordCryptoResults masterPasswordCryptoResults = cryptoService.doCryptoToMasterPassword(newMasterPassword);

            for (DriveFile originalDriveFile : loggedInUserInfo.getUser().getFiles()) {
                System.out.println("originalDriveFile = " + originalDriveFile.getId());
                File folder = fileManager.downloadZipAndExtractToFolder(originalDriveFile.getLink());
                updateFileKey(folder, loggedInUserInfo.getUser(), masterPasswordCryptoResults.getPublicKeyBase64(), oldMasterPassword);
                File zipFile = fileManager.toZipFile(folder);

                DriveFile updatedFile = updateFile(originalDriveFile.getId(), zipFile);
                originalDriveFile.setLink(updatedFile.getLink());
                originalDriveFile.setFileSharesRelationIds(updatedFile.getFileSharesRelationIds());
                originalDriveFile.setSize(updatedFile.getSize());
            }

            resetToUploadFolder();
            return masterPasswordCryptoResults;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void updateFileKey(File file, User user, String newPublicKeyBase64, String oldMasterPassword) {
        try {
            File configFile = new File(file.getAbsolutePath() + "/.config");

            FileEncryptionFinalResult result = JsonbBuilder.create()
                    .fromJson(new ByteArrayInputStream(Files.readAllBytes(Path.of(configFile.getAbsolutePath()))), FileEncryptionFinalResult.class);

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

    public JsonObject shareFile(DriveFile originalDriveFile, String recipientUsername) throws NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, IOException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException, InvalidKeySpecException {
        JsonObject userWithGivenFields = usersResourceClient.getUserWithGivenFields(recipientUsername, "userId,username,public_key");

        File downloadedFolder = fileManager.downloadZipAndExtractToFolder(originalDriveFile.getLink());
        addUserToConfigFile(downloadedFolder, userWithGivenFields.getString("userId"), userWithGivenFields.getString("publicKey"));

        File toZipFile = fileManager.toZipFile(downloadedFolder);
        DriveFile updatedFile = updateFile(originalDriveFile.getId(), toZipFile);

        originalDriveFile.setLink(updatedFile.getLink());
        originalDriveFile.setSize(updatedFile.getSize());
        originalDriveFile.setFileSharesRelationIds(updatedFile.getFileSharesRelationIds());

        SharedFileMetadata metadata = new SharedFileMetadata(
                originalDriveFile.getId(),
                loggedInUserInfo.getUser().getId(), loggedInUserInfo.getUser().getUsername(),
                userWithGivenFields.getString("userId"),
                userWithGivenFields.getString("username"));

        return driveResourceClient.shareFile(metadata);
    }

    private void addUserToConfigFile(File folder, String userId, String userPublicKeyBase64) throws IOException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeySpecException {
        FileEncryptionFinalResult result = readConfigFileToEncryptionResult(folder);

        if (result.getUsers().stream().anyMatch(sharedUsers -> sharedUsers.getUserId().equals(userId))) {
            throw new WebApplicationException(ResponseWithJsonBodyBuilder.withInformation(409, "File is already shared to given user!"));
        }

        String base64EncryptedFileKey = result.getBase64FileKey();
        SecretKey fileSecretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(base64EncryptedFileKey), loggedInUserInfo.getUserPrivateKey());

        PublicKey userPublicKey = (PublicKey) cryptoService.generateRSAKeyFromBase64(userPublicKeyBase64, false);

        byte[] encryptedFileKeyWithRecipientPublicKey = cryptoService.getRsaService().encrypt(fileSecretKey.getEncoded(), userPublicKey);

        String recipientFileKeyBase64 = encodeToBase64(encryptedFileKeyWithRecipientPublicKey);
        result.shareFile(userId, recipientFileKeyBase64);

        Files.writeString(Path.of(folder.getAbsolutePath() + "/.config"), JsonbBuilder.create().toJson(result), StandardOpenOption.WRITE);
    }

    public JsonArray getUserSharedFiles() {
        List<SharedFileMetadata> list = driveResourceClient.userSharedFiles(loggedInUserInfo.getUser().getId());

        JsonArrayBuilder jsonFiles = Json.createArrayBuilder();

        list.forEach(item -> {
            try {
                DriveFile driveFile = driveResourceClient.getFile(item.getFileId());
                File downloadedFolder = fileManager.downloadZipAndExtractToFolder(driveFile.getLink());

                FileEncryptionFinalResult result = this.readConfigFileToEncryptionResult(downloadedFolder);

                String base64FileKey = result.getSharedInfoPerUser(loggedInUserInfo.getUser().getId()).getBase64FileKey();
                SecretKey secretKey = cryptoService.decryptFileKeyWithPrivateKey(decodeFromBase64(base64FileKey), loggedInUserInfo.getUserPrivateKey());
                IvParameterSpec IV = new IvParameterSpec(decodeFromBase64(result.getBase64FileInitializationVector()));

                String decryptedFileName = doDecrypt(downloadedFolder, secretKey, IV, true);

                JsonObjectBuilder jsonMetadata = Json.createObjectBuilder(driveFile.toJsonObject())
                        .add("fileName", decryptedFileName)
                        .add("readableSize", humanReadableByteCountBin(driveFile.getSize()))
                        .add("readableCreatedAt", Instant.ofEpochMilli(driveFile.getCreatedAt()).toString())
                        .add("sharedBy", item.getOwnerUsername());

                jsonMetadata.remove("fileSharesRelationIds");

                jsonFiles.add(jsonMetadata);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        return jsonFiles.build();
    }

    private FileEncryptionFinalResult readConfigFileToEncryptionResult(File folder) throws IOException {
        File configFile = new File(folder.getAbsolutePath() + "/.config");

        return JsonbBuilder.create()
                .fromJson(new ByteArrayInputStream(Files.readAllBytes(Path.of(configFile.getAbsolutePath()))), FileEncryptionFinalResult.class);
    }


}
