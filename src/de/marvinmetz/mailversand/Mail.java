package de.marvinmetz.mailversand;

import java.io.File;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import de.marvinmetz.mailversand.PropertiesM.SMTPData;

public class Mail {

	Message   message;
	Multipart multipart;

	public Mail(Session session) {
		this.message = new MimeMessage(session);
		this.multipart = new MimeMultipart();
	}

	public void fillHeader(InternetAddress from, InternetAddress internetAddress, String subj) throws AddressException, MessagingException {
		this.message.setFrom(from);
		this.message.setRecipient(Message.RecipientType.TO, internetAddress);
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
		Session session = Session.getInstance(props, new javax.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(data.getUser(), data.getPassword());
			}
		});
		return session;
	}

}
