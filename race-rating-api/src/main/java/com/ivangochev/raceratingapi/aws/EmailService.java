package com.ivangochev.raceratingapi.aws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;


@Service
@Slf4j
public class EmailService {
    @Value("${aws.ses.senderEmail}")
    private String senderEmail;
    @Value("${aws.region}")
    private String region;

    public void sendEmail(String recipient, String subject, String body) {
        try (SesClient sesClient = SesClient.builder().region(Region.of(region)).build()) {
            log.info("Sending email to " + recipient + " with subject " + subject);

            Destination destination = Destination.builder()
                    .toAddresses(recipient)
                    .build();

            Content subjectContent = Content.builder()
                    .data(subject)
                    .charset("UTF-8")
                    .build();

            Content bodyContent = Content.builder()
                    .data(body)
                    .charset("UTF-8")
                    .build();

            Body messageBody = Body.builder()
                    .text(bodyContent)
                    .build();

            Message message = Message.builder()
                    .subject(subjectContent)
                    .body(messageBody)
                    .build();

            SendEmailRequest request = SendEmailRequest.builder()
                    .destination(destination)
                    .message(message)
                    .source("Race Rating <" + senderEmail + ">")
                    .build();

            sesClient.sendEmail(request);

            log.info("Email sent to {}", recipient);
        } catch (SesException e) {
            log.error("Failed to send email via SES: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
