package automationFramework.utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.io.File;
import java.util.Properties;

public class EmailSender {

	public static void sendEmailWithAttachment(String[] toEmails, String subject, String message, File... attachments) {
		final String fromEmail = "mallicksupriya4152@gmail.com"; // Sender's email
		final String password = "dwtsvhjlixhbrrbg"; // App-specific password

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

			// Convert array to comma-separated string and parse
			String allRecipients = String.join(",", toEmails);
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(allRecipients));

			msg.setSubject(subject);

			// Email body
			MimeBodyPart messagePart = new MimeBodyPart();
			messagePart.setText(message);

			// Attachments
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(messagePart);

			for (File file : attachments) {
				if (file.exists()) {
					MimeBodyPart attachPart = new MimeBodyPart();
					attachPart.attachFile(file);
					multipart.addBodyPart(attachPart);
				}
			}

			msg.setContent(multipart);
			Transport.send(msg);

			System.out.println("✅ Email with attachment sent to multiple recipients successfully.");

		} catch (Exception e) {
			System.out.println("❌ Failed to send email: " + e.getMessage());
		}
	}
}
