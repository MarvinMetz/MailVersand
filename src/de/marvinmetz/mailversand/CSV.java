package de.marvinmetz.mailversand;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSV{

	CSVParser records;
	ArrayList<Beleg> list;
	
	
	public CSV(String path){
		this.list = new ArrayList<>();
		try (Reader file = new FileReader(path);) {
			MailVersand.log.log(Level.FINE, "Importdatei gefunden.");
			this.records = new CSVParser(file, CSVFormat.DEFAULT.withDelimiter(';').withHeader());
			MailVersand.log.log(Level.FINE, "Importdatei gelesen.");
			for (CSVRecord rec : this.records)
				try {
					this.list.add(new Beleg(rec.get("rechnungsnummer"), rec.get("kundennummer"), rec.get("vorname"), rec.get("nachname"), rec.get("anrede"), rec.get("rechnung"), new InternetAddress(rec.get("mail"))));
					MailVersand.log.log(Level.FINEST, "Eintrag: " + rec.toString());
				} catch (AddressException e) {
					MailVersand.log.log(Level.WARNING, "Email Adresse '" + rec.get("mail") + "' ist nicht korrekt formatiert.");
				}
		} catch (FileNotFoundException e1) {
			MailVersand.log.log(Level.SEVERE, "Keine Importdatei gefunden:", e1);
			e1.printStackTrace();
			System.exit(0);
		} catch (IOException e1) {
			MailVersand.log.log(Level.SEVERE, "Fehler beim Lesen der Importdatei:", e1);
			e1.printStackTrace();
			System.exit(0);
		}
	}

	public ArrayList<Beleg> getData() {
		return this.list;
	}
}
