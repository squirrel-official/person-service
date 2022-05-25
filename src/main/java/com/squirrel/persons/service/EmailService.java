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
import java.util.Set;

@Service
public class EmailService {

    private JavaMailSender sender;
    private static final Log LOGGER = LogFactory.getLog(EmailService.class);
    private FileService fileService;
    @Autowired
    protected EmailService(JavaMailSender sender,FileService fileService) {
        this.sender = sender;
        this.fileService = fileService;
    }

    public void attachImagesAndSendEmail(String toEmail, String path) throws MessagingException, IOException, DocumentException {
        Set<Image> imageSet = fileService.getListOfFiles(path);
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
            if(!file.delete()) {
                file.deleteOnExit();
            }
        }
    }


    private File createDocument(Set<Image> images) throws IOException, DocumentException {
        final File outputFile = File.createTempFile("Squirrel-" + DateTime.now().toLocalTime(), ".pdf");
        Document document = new Document();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(outputFile));
        pdfWriter.setFullCompression();
        document.open();
        for (Image eachImage : images) {
            document.add(eachImage);
        }
        document.close();
        return outputFile;
    }

    ;
}