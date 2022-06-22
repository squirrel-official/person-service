package com.squirrel.persons.service;


import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.squirrel.persons.aspect.TrackExecutionTime;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private FileService fileService;

    private static final Logger LOGGER = LogManager.getLogger(EmailService.class);

    @Value("${spring.mail.username}")
    private String fromUser;

    @Autowired
    protected EmailService(JavaMailSender sender, FileService fileService) {
        this.sender = sender;
        this.fileService = fileService;
    }

    @TrackExecutionTime
    public void triggerNotification(String toEmail, String subjectMessage, String detailMessage) throws MessagingException{
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
        helper.setTo(toEmail);
        helper.setSubject(subjectMessage);
        helper.setText(detailMessage, true);
        message.setFrom(fromUser);
        sender.send(message);
    }

    @TrackExecutionTime
    public boolean attachImagesAndSendEmail(String toEmail, String path, String emailMessage, String detailMessage) throws MessagingException, IOException, DocumentException {
        try {
            Set<Image> imageSet = fileService.getListOfFiles(path);
            LOGGER.debug(String.format("total files to be attached : %s for %s", imageSet.size(), emailMessage));
            if (!imageSet.isEmpty()) {
                File file = createDocument(imageSet);
                MimeMessage message = sender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                        StandardCharsets.UTF_8.name());
                helper.setTo(toEmail);
                helper.setSubject(emailMessage);
                helper.setText(detailMessage, true);
                helper.addAttachment(file.getName(), file);
                message.setFrom(fromUser);
                sender.send(message);
                long size = file.length() / (1024 * 1024);
                LOGGER.info(String.format("Sent mail with attachment size %s", size));
                if (!file.delete()) {
                    file.deleteOnExit();
                }
            }
        } catch (Exception exception) {
            LOGGER.error("An error happened", exception);
            return false;
        }
        return true;
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