package com.squirrel.persons.service;

import com.google.common.collect.Iterables;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.squirrel.persons.aspect.TrackExecutionTime;
import com.squirrel.persons.util.FileUtils;
import dev.failsafe.Failsafe;
import dev.failsafe.RetryPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.squirrel.persons.Constant.*;

@Service
public class NotificationService {

    @Value("${spring.mail.username}")
    private String fromUser;
    @Value("${mail.images.count:50}")
    private int eachEmailImageCount;
    @Value("${mail.recipient}")
    private String toEmail;
    private JavaMailSender sender;
    private FileService fileService;

    private RetryPolicy<Object> retryPolicy;

    private Map<String, String> archivePathMap;

    private static final Logger LOGGER = LogManager.getLogger(NotificationService.class);


    @Autowired
    protected NotificationService(@Value("${retry.duration:10}") int retryDuration, @Value("${retry.count:3}") int retryCount,
                                  JavaMailSender sender, FileService fileService) {
        this.sender = sender;
        this.fileService = fileService;
        this.retryPolicy = RetryPolicy.builder()
                .handle(Exception.class)
                .withDelay(Duration.ofSeconds(retryDuration))
                .withMaxRetries(retryCount)
                .build();

        this.archivePathMap = Map.of(
                CRIMINALS_PATH, CRIMINALS_ARCHIVE_PATH,
                VISITOR_PATH, VISITOR_ARCHIVE_PATH,
                FRIENDS_PATH,FRIENDS_ARCHIVE_PATH
        );
    }

    public void notification( String subjectMessage, String detailMessage) {
        SimpleMailMessage message=new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subjectMessage);
        message.setText(detailMessage);
        message.setFrom(fromUser);
        sender.send(message);
    }

    public void notificationWithAttachments(String path, String subject, String message, boolean suspendedNotifications) {
        if (suspendedNotifications) {
            archiveImages(path);
        }else{
            if (Failsafe.with(retryPolicy).get(() -> attachImagesAndSendEmail(path, subject, message))) {
                archiveImages(path);
            }
        }
    }

    @TrackExecutionTime
    private boolean attachImagesAndSendEmail(String path, String emailMessage, String detailMessage) throws IOException, DocumentException, MessagingException {
        Set<Image> allFiles = fileService.getListOfAllFiles(path);
        LOGGER.debug(String.format("total files to be attached : %s and files are %s ", allFiles.size(), allFiles));
            for (List<Image> eachImageSet : Iterables.partition(allFiles, eachEmailImageCount)) {
                if (!eachImageSet.isEmpty()) {
                    File file = createDocument(eachImageSet);
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
            }

        return true;
    }

    private File createDocument(List<Image> images) throws IOException, DocumentException {
        final File outputFile = File.createTempFile("Netra-" + DateTime.now().toLocalTime(), ".pdf");
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

    private void archiveImages(String path) {
        try {
            FileUtils.copyAllFiles(path, archivePathMap.get(path));
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }



}