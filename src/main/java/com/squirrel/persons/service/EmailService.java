package com.squirrel.persons.service;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EmailService {

    private JavaMailSender sender;
    private static final Log logger = LogFactory.getLog(EmailService.class);

    @Autowired
    protected EmailService(JavaMailSender sender) {
        this.sender = sender;
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

    public void attachImagesAndSendEmail(String toEmail, String path) throws MessagingException, IOException, DocumentException {
        Set<Image> imageSet = getListOfFiles(path);
        if (!imageSet.isEmpty()) {
            File file = createDocument(imageSet);
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());
            helper.setTo(toEmail);
            helper.setSubject("Person of interest found");
            helper.setText("You had  following visitors today", true);
            helper.addAttachment(file.getName(), file);
            sender.send(message);
        }
    }

    private Set<Image> getListOfFiles(String path) throws IOException {
        Set<Image> images = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .filter(EmailService::imageCheck)
                .map(file -> {
                    Image image;
                    try {
                        image = Image.getInstance(file.toString());
                        image.scaleAbsolute(300, 300);
                        return image;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }).filter(Objects::nonNull).collect(Collectors.toSet());
        return images;
    }

    ;


    private File createDocument(Set<Image> images) throws IOException, DocumentException {
        final File outputFile = File.createTempFile("Squirrel-" + DateTime.now().toLocalTime(), ".pdf");
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        document.open();
        for (Image eachImage : images) {
            document.add(eachImage);
        }
        document.close();
        return outputFile;
    }

    ;
}