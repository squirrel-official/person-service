package com.squirrel.persons.service;

import com.itextpdf.text.Image;
import com.squirrel.persons.util.FileUtils;
import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

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
                .filter(FileUtils::imageCheck)
                .filter(FileUtils::checkNotHidden)
                .map(FileService::formatImages).filter(Objects::nonNull).limit(50).collect(Collectors.toSet());
        return images;
    }


    public void purgeFilesOlderThanNDays(String path, int days) throws IOException {
        Set<Path> paths = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(FileUtils::isNotImage)
                .filter(FileUtils::checkNotHidden).filter(Objects::nonNull).collect(Collectors.toSet());

        for (Path eachPath : paths) {
            BasicFileAttributes basicFileAttributes = Files.readAttributes(eachPath, BasicFileAttributes.class);
            long fileTimeInMillis = basicFileAttributes.creationTime().toMillis();
            long currentTimeInMillis = DateTime.now().minusDays(days).getMillis();
            if (fileTimeInMillis < currentTimeInMillis) {
                Files.delete(eachPath);
            }
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
