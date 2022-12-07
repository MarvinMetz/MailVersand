package de.marvinmetz.mailversand;

import java.io.File;
import java.util.Properties;

import de.marvinmetz.mailversand.PropertiesM.SMTPData;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class Mail {

	Message   message;
	Multipart multipart;

	public Mail(Session session) {
		this.message = new MimeMessage(session);
		this.multipart = new MimeMultipart();
	}

	public void fillHeader(InternetAddress from, InternetAddress internetAddress, String subj, InternetAddress bcc) throws AddressException, MessagingException {
		this.message.setFrom(from);
		this.message.setRecipient(Message.RecipientType.TO, internetAddress);
		this.message.setRecipient(Message.RecipientType.BCC, bcc);
		this.message.setSubject(subj);
	}

	public void addBodyText(String text) throws MessagingException {
		BodyPart part = new MimeBodyPart();
		part.setContent(text, "text/html");
		this.multipart.addBodyPart(part);
	}

	public void addAttachment(File file) throws MessagingException {
		BodyPart part = new MimeBodyPart();
		part.setDataHandler(new DataHandler(new FileDataSource(file)));
		part.setFileName(file.getName());
		this.multipart.addBodyPart(part);
	}

	public void send() throws MessagingException {
		this.message.setContent(this.multipart);
		Transport.send(this.message);
	}

	public static Session createSession(SMTPData data) {
		Properties props = new Properties();
		props.put("mail.smtp.from", data.getMail());
		props.put("mail.smtp.auth", "true");

		if (MailVersand.test) {
			props.put("mail.smtp.ssl.enable", "false");
			props.put("mail.smtp.host", data.getHost());
			props.put("mail.smtp.port", data.getPort());
			props.put("mail.smtp.host", "127.0.0.1");
			props.put("mail.smtp.port", "465");
		} else {
			props.put("mail.smtp.starttls.enable", "true");
			props.put("mail.smtp.socketFactory.port", data.getPort());
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.socketFactory.fallback", "false");
			props.put("mail.smtp.host", data.getHost());
			props.put("mail.smtp.port", data.getPort());
		}

		// Get the Session object.
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(data.getUser(), data.getPassword());
			}
		});
		return session;
	}

}
