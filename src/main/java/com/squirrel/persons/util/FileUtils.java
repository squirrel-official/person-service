package com.squirrel.persons.util;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.Arrays;

public class FileUtils {

   public static void copyAllFiles(String source, String destinationPath) throws IOException {
       File srcDir = new File(source);
       String destination = destinationPath + LocalDateTime.now().toLocalDate();
       File destDir = new File(destination);
       try {
           Files.move(srcDir.toPath(), destDir.toPath(), new StandardCopyOption[]{StandardCopyOption.REPLACE_EXISTING});
       }catch (DirectoryNotEmptyException de){
          File tempDirectory = new File(destination+"/temp");
           Files.move(srcDir.toPath(), tempDirectory.toPath(), new StandardCopyOption[]{StandardCopyOption.REPLACE_EXISTING});
           Files.createDirectory(srcDir.toPath());
           org.apache.commons.io.FileUtils.copyDirectory(tempDirectory , destDir);
           org.apache.commons.io.FileUtils.deleteDirectory(tempDirectory);
       }
       org.apache.commons.io.FileUtils.forceMkdir(srcDir);
    }

    public static boolean isNotImage(Path file) {
        return !imageCheck(file);
    }

    public static boolean imageCheck(Path file) {
        try {
            String mimetype = Files.probeContentType(file);
            return mimetype != null && mimetype.split("/")[0].equals("image");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean checkNotHidden(Path eachFilePath) {
        try {
            return !Files.isHidden(eachFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void deleteImages(String source) {
        File srcDir = new File(source);
        Arrays.stream(srcDir.listFiles()).filter(f->imageCheck(f.toPath())).forEach(File::delete);
    }

    static class GenericExtFilter implements FilenameFilter {
        public boolean accept(File file, String name) {
            return imageCheck(file.toPath());
        }
    }
}
