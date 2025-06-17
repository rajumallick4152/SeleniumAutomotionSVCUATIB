package automationFramework.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.List;
import java.util.Properties;

public class EmailSender {

	private static final String fromEmail = "mallicksupriya4152@gmail.com"; // Sender's email
	private static final String password = "dwtsvhjlixhbrrbg"; // App-specific password

	public static void sendEmailWithAttachment(String[] toEmails, String subject, String message, File... attachments) {
		sendEmailWithMultipleAttachments(toEmails, subject, message, List.of(attachments));
	}

	public static void sendEmailWithMultipleAttachments(String[] toEmails, String subject, String message,
			List<File> attachments) {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(fromEmail, password);
			}
		});

		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress(fromEmail));

			String allRecipients = String.join(",", toEmails);
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(allRecipients));

			msg.setSubject(subject);

			// Body part
			MimeBodyPart messagePart = new MimeBodyPart();
			messagePart.setText(message);

			// Multipart for body + attachments
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messagePart);

			if (attachments != null) {
				for (File file : attachments) {
					if (file.exists()) {
						MimeBodyPart attachPart = new MimeBodyPart();
						attachPart.attachFile(file);
						multipart.addBodyPart(attachPart);
					}
				}
			}

			msg.setContent(multipart);
			Transport.send(msg);

			System.out.println("✅ Email with attachments sent successfully to multiple recipients.");

		} catch (Exception e) {
			System.out.println("❌ Failed to send email: " + e.getMessage());
		}
	}
}
