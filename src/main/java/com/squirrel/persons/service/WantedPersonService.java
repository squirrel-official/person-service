package com.squirrel.persons.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Service
@Slf4j
public class WantedPersonService {
    public static final String CRIME_STOPPERS_VIC_WANTED_PERSONS_URL = "https://www.crimestoppersvic.com.au/help-solve-crime/wanted-persons/";
    public static final String WANTED_CRIMINALS_DIRECTORY = "/usr/local/squirrel-ai/data/wanted-criminals/";

    public void refreshWantedPersons() throws IOException {
        deleteAllFiles(WANTED_CRIMINALS_DIRECTORY);
        Set<String> picturesUrl = getAllPictures();
        Iterator<String> it = picturesUrl.iterator();
        int i = 0;
        while (it.hasNext()) {
            String url = it.next();
            i++;
            URLConnection connection = new URL(url).openConnection();
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            try (InputStream in = connection.getInputStream()) {
                Files.copy(in, Paths.get(WANTED_CRIMINALS_DIRECTORY + i + ".png"), StandardCopyOption.REPLACE_EXISTING);
            }
        }
    }

    private void deleteAllFiles(String wantedCriminalsDirectory) throws IOException {
        try {
            FileUtils.cleanDirectory(new File(wantedCriminalsDirectory));
        } catch (IOException e) {
            log.error("Unable to delete ", e);
            throw e;
        }
    }

    private Set<String> getAllPictures() throws IOException {
        Set<String> imagesUrl = new HashSet<>();
        for (int page = 1; page < 10; page++) {
            Document document = Jsoup.connect(CRIME_STOPPERS_VIC_WANTED_PERSONS_URL + page).get();
            Elements elements = document.getElementsByClass("type-wanted_persons");  //wanted_persons

            for (int i = 0; i < elements.size(); i++) {
                Element eachElement = elements.get(i);
                Elements eachElementSub = eachElement.getElementsByClass("elementor-section-wrap");
                for (int j = 0; j < eachElementSub.size(); j++) {
                    String data = eachElementSub.get(j).data();
                    int startIndex = data.indexOf("https://www.crimestoppersvic.com.au");
                    int endIndexPNG = data.indexOf(".png");
                    int endIndexJPG = data.indexOf(".jpg");
                    int endIndexJPEG = data.indexOf(".jpeg");
                    String extractedUrl;
                    if (endIndexPNG != -1) {
                        extractedUrl = data.substring(startIndex, endIndexPNG) + ".png";
                    } else if (endIndexJPG != -1) {
                        extractedUrl = data.substring(startIndex, endIndexJPG) + ".jpg";
                    } else {
                        extractedUrl = data.substring(startIndex, endIndexJPEG) + ".jpeg";
                    }

                    imagesUrl.add(extractedUrl);
                }
            }
        }
        return imagesUrl;
    }
}
