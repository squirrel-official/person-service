package com.squirrel.persons.service;

import com.itextpdf.text.Image;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class FileService {
    public Set<Image> getListOfFiles(String path) throws IOException {
        Set<Image> images = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(FileService::imageCheck)
                .filter(FileService::checkNotHidden)
                .map(FileService::formatImages).filter(Objects::nonNull).limit(50).collect(Collectors.toSet());
        return images;
    }


    public void purgeFilesOlderThanNDays(String path, int days) throws IOException {
        Set<Path> paths = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(FileService::isNotImage)
                .filter(FileService::checkNotHidden).filter(Objects::nonNull).collect(Collectors.toSet());

        for (Path eachPath : paths) {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(eachPath, BasicFileAttributes.class);
            long fileTimeInMillis = basicFileAttributes.creationTime().toMillis();
            long currentTimeInMillis = DateTime.now().minusDays(days).getMillis();
            if (fileTimeInMillis < currentTimeInMillis) {
                Files.delete(eachPath);
            }
        }
    }

    private static boolean isNotImage(Path file) {
       return !imageCheck(file);
    }

    private static boolean imageCheck(Path file) {
        try {
            String mimetype = Files.probeContentType(file);
            return mimetype != null && mimetype.split("/")[0].equals("image");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean checkNotHidden(Path eachFilePath) {
        try {
            return !Files.isHidden(eachFilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static Image formatImages(Path file) {
        Image image;
        try {
            image = Image.getInstance(file.toString());
            image.scaleAbsolute(300, 300);
            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
