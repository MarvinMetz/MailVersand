package de.marvinmetz.mailversand;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;

import javax.mail.internet.InternetAddress;

public class PropertiesM {

	Properties config;

	String     importPath;
	String     attachmentPath;

	String     subject;
	String     mailTemplate;

	SMTPData   smtpData;

	@SuppressWarnings("resource")
	public PropertiesM() {
		InputStream inputStream;

		File folder = new File(getUserDataDirectory());
		if (!folder.exists() || !folder.isDirectory()) {
			MailVersand.log.log(Level.FINE, "Einstellungsordner nicht gefunden.");
			folder.mkdirs();
			MailVersand.log.log(Level.INFO, "Einstellungsordner neu erstellt unter: " + folder.getPath());
		}

		File config_file = new File(getUserDataDirectory() + "config.properties");
		if (!config_file.exists() || config_file.isDirectory()) {
			MailVersand.log.log(Level.FINE, "Einstellungen nicht gefunden.");
			inputStream = getClass().getClassLoader().getResourceAsStream("config.properties");
			try {
				Files.copy(inputStream, config_file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				inputStream.close();
				MailVersand.log.log(Level.INFO, "Einstellungen Standard erstellt unter: " + config_file.toPath());
			} catch (IOException e) {
				MailVersand.log.log(Level.SEVERE, "Einstellungen Standard konnte nicht erstellt werden:", e);
				e.printStackTrace();
				System.exit(0);
			}

		}

		File template_file = new File(getUserDataDirectory() + "template.html");
		if (!template_file.exists() || template_file.isDirectory()) {
			MailVersand.log.log(Level.FINE, "Mail Template nicht gefunden.");
			inputStream = getClass().getClassLoader().getResourceAsStream("template.html");
			try {
				Files.copy(inputStream, template_file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				inputStream.close();
				MailVersand.log.log(Level.INFO, "Mail Template Standard erstellt unter: " + template_file.toPath());
			} catch (IOException e) {
				MailVersand.log.log(Level.SEVERE, "Mail Template Standard konnte nicht erstellt werden:", e);
				e.printStackTrace();
				System.exit(0);
			}
		}

		try {
			inputStream = new FileInputStream(config_file);
			this.config = new Properties();
			this.config.load(inputStream);
			inputStream.close();
			MailVersand.log.log(Level.FINE, "Einstellungen geladen.");
		} catch (Exception e) {
			MailVersand.log.log(Level.SEVERE, "Einstellungen konnten nicht geladen:", e);
			e.printStackTrace();
			System.exit(0);
		}
		
		try {
			inputStream = new FileInputStream(template_file);
			this.mailTemplate = convertInputStreamToString(inputStream);
			inputStream.close();
			MailVersand.log.log(Level.FINE, "Mail Template geladen");
		} catch (Exception e) {
			MailVersand.log.log(Level.SEVERE, "Mail Template konnten nicht geladen:", e);
			e.printStackTrace();
			System.exit(0);
		}

		String host = this.config.getProperty("smtp.host");
		String port = this.config.getProperty("smtp.port");
		String user = this.config.getProperty("smtp.user");
		String mail = this.config.getProperty("smtp.mail");
		String personal = this.config.getProperty("smtp.personal");

		String password = this.config.getProperty("smtp.password");

		this.smtpData = new SMTPData(host, port, user, password, mail, personal);

		this.subject = this.config.getProperty("mail.subject");
		try {
		this.attachmentPath = this.config.getProperty("path.attachments").replaceAll("[/\\\\]+", Matcher.quoteReplacement(File.separator));
		this.importPath = this.config.getProperty("path.import").replaceAll("[/\\\\]+", Matcher.quoteReplacement(File.separator));
		} catch (Exception e) {
			MailVersand.log.log(Level.SEVERE, "Pfad Seperatoren sind nicht korrekt:", e);
			e.printStackTrace();
			System.exit(0);
		}

	}

	public SMTPData getSMTPData() {
		return this.smtpData;
	}

	public String getAttachmentPath() {
		return this.attachmentPath;
	}

	public String getImportDataPath() {
		return this.importPath;
	}

	public String getSubject() {
		return this.subject;
	}

	public String getTemplate() {
		return this.mailTemplate;
	}

	public static class SMTPData {
		String          host;
		String          port;
		String          user;
		String          password;
		InternetAddress mail;

		public SMTPData(String host, String port, String user, String password, String mail, String personal) {
			this.host = host;
			this.port = port;
			this.user = user;
			this.password = password;
			try {
				this.mail = new InternetAddress(mail, personal);
			} catch (UnsupportedEncodingException e) {
				MailVersand.log.log(Level.WARNING, "Email Adresse '" + mail + "' ist nicht korrekt formatiert.");
			}
		}

		public String getHost() {
			return this.host;
		}

		public String getPort() {
			return this.port;
		}

		public String getUser() {
			return this.user;
		}

		public String getPassword() {
			return this.password;
		}

		public InternetAddress getMail() {
			return this.mail;
		}
	}

	public static String getUserDataDirectory() {
		return System.getProperty("user.home") + File.separator + "MailVersand" + File.separator;
	}

	private static String convertInputStreamToString(InputStream is) throws IOException {

		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[8192];
		int length;
		while ((length = is.read(buffer)) != -1) {
			result.write(buffer, 0, length);
		}
		return result.toString("UTF-8");
	}

}
