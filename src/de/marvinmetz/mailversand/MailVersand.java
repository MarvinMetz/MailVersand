package de.marvinmetz.mailversand;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;

public class MailVersand {

	public static void main(String[] args) {
		createLogger();

		for (String a : args) {
			if (a.equals("debug"))
				debug = true;
			else if (a.equals("test"))
				test = true;
			else if (a.equals("testV"))
				testV = true;
		}

		if (debug)
			log.setLevel(Level.ALL);

		log.log(Level.INFO, "Programm gestartet.");

		MailVersand main = new MailVersand();

		main.loadBelege(main.getProperties().getImportDataPath());
		MailVersand.log.log(Level.FINE, "Alle Belege wurden geladen.");
		MailVersand.log.log(Level.INFO, "Starte Versand von " + main.getBelege().size() + " Mails.");
		main.sendMail(main.getBelege());
		MailVersand.log.log(Level.INFO, "Alle Mails wurden versandt.");
	}

	static final String PATTERN = "yyyyMMdd";
	static boolean debug = false;
	static boolean test = false;
	static boolean testV = false;

	public static Logger log;

	PropertiesM prop;
	ArrayList<Beleg> belege;

	int mailStatus;

	public MailVersand() {
		this.prop = new PropertiesM();
		MailVersand.log.log(Level.FINE, "Alle Einstellungen wurden geladen.");
	}

	private static void createLogger() {
		MailVersand.log = Logger.getLogger("Log");

		File path = new File(MailVersand.getFilePath() + File.separator + "Logs" + File.separator);
		if (!path.exists() || !path.isDirectory())
			path.mkdirs();

		DateFormat df = new SimpleDateFormat(PATTERN);
		Date today = Calendar.getInstance().getTime();
		try {
			String pa;
			int roll = 0;
			do {
				pa = path.getPath() + File.separator + "" + df.format(today) + "_" + roll + ".log";
				roll++;
			} while (new File(pa).exists());
			FileHandler fh = new FileHandler(pa, false);
			LogFormatter fm = new LogFormatter();
			fh.setFormatter(fm);
			log.addHandler(fh);
		} catch (SecurityException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public ArrayList<Beleg> getBelege() {
		return this.belege;
	}

	public PropertiesM getProperties() {
		return this.prop;
	}

	public int getMailStatus() {
		return this.mailStatus;
	}

	public void loadBelege(String impPath) {
		CSV data = new CSV(impPath);
		this.belege = data.getData();

	}

	public void sendMail(ArrayList<Beleg> belegeI) {
		Session session = Mail.createSession(getProperties().getSMTPData());
		MailVersand.log.log(Level.FINE, "Mail Session erzeugt.");
		try {
			for (Beleg b : belegeI) {
				MailVersand.log.log(Level.FINEST, "Bereite Mail für " + b.belegnummer + " vor.");
				Mail m = new Mail(session);
				m.fillHeader(getProperties().getSMTPData().getMail(), b.getMail(),
						fitt(getProperties().getSubject(), b), getProperties().getSMTPData().getBcc());
				MailVersand.log.log(Level.FINEST, "Kopfdaten für " + b.belegnummer + " gefüllt.");
				m.addBodyText(fitt(getProperties().getTemplate(), b));
				MailVersand.log.log(Level.FINEST, "Mail Inhalt für " + b.belegnummer + " gefüllt.");
				if (!b.getBelegFile().equals("")) {
					File atta = new File(getProperties().getAttachmentPath() + b.getBelegFile());
					if (atta.exists() && !atta.isDirectory()) {
						m.addAttachment(atta);
						MailVersand.log.log(Level.FINEST, "Anhang für " + b.belegnummer + " gefüllt.");
					} else {
						MailVersand.log.log(Level.WARNING, "Kein Anhang für " + b.belegnummer + " gefunden.");
					}
				}
				m.send();
				MailVersand.log.log(Level.INFO, "Mail für " + b.belegnummer + " versandt.");
			}
		} catch (MessagingException e) {
			MailVersand.log.log(Level.SEVERE, "Fehler beim Versand von Mails:", e);
			e.printStackTrace();
			System.exit(0);
		}

	}

	public static String fitt(String s, Beleg b) {
		String fitted = s;
		fitted = fitted.replace("%mail%", b.getMail().getAddress());
		fitted = fitted.replace("%kundennummer%", b.getKundennummer());
		fitted = fitted.replace("%belegnummer%", b.getBelegnummer());
		fitted = fitted.replace("%anrede%", b.getAnrede());
		fitted = fitted.replace("%nachname%", b.getNachname());
		fitted = fitted.replace("%vorname%", b.getVorname());
		return fitted;
	}

	public static String getFilePath() {
		try {
			return new File(MailVersand.class.getProtectionDomain().getCodeSource().getLocation().toURI())
					.getParentFile().getPath();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

}
