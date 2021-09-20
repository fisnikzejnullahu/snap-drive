package com.fisnikz.snapdrive;

import io.quarkus.runtime.StartupEvent;

import javax.enterprise.event.Observes;
import java.io.File;

/**
 * @author Fisnik Zejnullahu
 */
public class OnApplicationStart {

    void onStart(@Observes StartupEvent ev) {
        System.out.println("ON START UP EVENT");
        File toUploadDir = new File(System.getProperty("java.io.tmpdir") + "/snap-files/toUpload/");
        if (!toUploadDir.exists()) {
            System.out.println("toUploadDir CREATED ");
            System.out.println(toUploadDir.getAbsolutePath());
            toUploadDir.mkdirs();
        }

        File decryptedFilesDir = new File(System.getProperty("java.io.tmpdir") + "/snap-files/local/");
        if (!decryptedFilesDir.exists()) {
            System.out.println("decryptedFilesDir CREATED ");
            System.out.println(decryptedFilesDir.getAbsolutePath());
            decryptedFilesDir.mkdirs();
        }
    }
}
