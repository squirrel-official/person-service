package com.squirrel.persons.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

public class FilesUtils {

   public static void copyAllFiles(String source, String destinationPath) throws IOException {
       File srcDir = new File(source);
       String destination = destinationPath + LocalDateTime.now().toLocalDate();
       File destDir = new File(destination);
       try {
           Files.move(srcDir.toPath(), destDir.toPath(), StandardCopyOption.REPLACE_EXISTING);
       }catch (DirectoryNotEmptyException de){
          File tempDirectory = new File(destination+"/temp");
           Files.move(srcDir.toPath(), tempDirectory.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
           Files.createDirectory(srcDir.toPath());
           FileUtils.copyDirectory(tempDirectory , destDir);
           FileUtils.deleteDirectory(tempDirectory);
       }
       FileUtils.forceMkdir(srcDir);
    }
}
